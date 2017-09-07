import constants, support, socket, logging, json, threading, os, sys
from datetime import datetime
from thread import *


class SecamControllerClient:    
    def __init__(self):
        self.const = constants.Constants()
        self.logger = logging.getLogger("secam")
        
    def send_message(self, obj, return_json=False):
        try:
            # Create new socket, and connect to the server
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.connect((self.const.host, self.const.port))
            util = SocketUtil(sock)
            
            # Marshall the supplied object into a string, and send to the server
            s = json.dumps(obj)
            self.logger.debug("Sending message [%s] ..." % s)
            util.send(s)
            
            # Wait for a response
            s = util.recv()
            
            # Unmarshall the returned json string into an object, and return same to caller
            self.logger.debug("... received response [%s]" % s)
            return s if return_json else json.loads(s)
        finally:
            try:
                sock.close()
            except:
                self.logger.warning("Failed to close the socket")
            
        return -1;


class SecamController:
    def __init__(self, camera):
        self.const = constants.Constants()
        self.camera = camera
        self.server = None
        self.queue = []
        self.counter = 0
        self.queue_processing_thread_activated = False
        self.q_lock = threading.Lock()
        self.service_lock = threading.Lock()
        self.logger = logging.getLogger("secam")
        self.support = support.Support()
     
    def start(self):
        self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.logger.debug('Socket created')
        
        # Bind socket to local host and port
        try:
            self.server.bind((self.const.host, self.const.port))
        except socket.error as msg:
            self.logger.error('Bind failed. Error Code : ' + str(msg[0]) + ' Message ' + msg[1])
            sys.exit()
             
        self.logger.debug('Socket bind complete')
         
        # Start listening on socket
        self.server.listen(5)
        self.logger.debug('Socket now listening')
         
        # now keep talking with the client
        try:
            while True:
                # wait to accept a connection - blocking call
                conn, addr = self.server.accept()
                util = SocketUtil(conn)
                
                dataStr = util.recv().strip(" \t\n\r")
                obj = json.loads(dataStr)
                task = Task(obj["action"], obj["args"])
                self._action(task, util)
                
                conn.close()
                    
        except KeyboardInterrupt:
            self.logger.info("Keyboard interrupt")
        finally:
            self.server.close()

    
    def _action(self, task, util):
        # Do actions that require an immediate response here
        if task.action == self.const.stat:
            response = self.camera.get_status()
            # Return response as a json string
            util.send(json.dumps(response))
            
        elif task.action == "get_file_register":
            response = self.support.get_videos()
            util.send(json.dumps(response))
            
        elif task.action == "camera":
            camera_ctrl = task.args["ctrl"]
            value = task.args["value"]
            self.logger.info("Setting camera %s to %s" % (camera_ctrl, value))
            
            msg = self.camera.set_setting(camera_ctrl, value)
            response = self.camera.get_status()
            response["msg"] = msg
            util.send(json.dumps(response))
            
        elif task.action == self.const.go:
            if self.camera.status != self.const.go:
                self.camera.status = self.const.go
                response = self.camera.get_status()
                response["msg"] = "Surveillance is on"
                self.logger.info(response["msg"])
                util.send(json.dumps(response))
            else:
                msg = "Surveillance is already on"
                response = {}
                response["msg"] = msg
                self.logger.info(msg)
                util.send(json.dumps(response))
                
        elif task.action == self.const.stop:
            if self.camera.status != self.const.stop:
                self.camera.status = self.const.stop
                response = self.camera.get_status()
                response["msg"] = "Surveillance paused"
                self.logger.info(response["msg"])
                util.send(json.dumps(response))
            else:
                msg = "Surveillance is already paused"
                response = {}
                response["msg"] = msg
                self.logger.info(msg)
                util.send(json.dumps(response))
                
        elif task.action == "delete":
            reply, ok = self.support.delete_files(task.args["files"])
            self.logger.info(reply)
            response = {"status": ok, "msg": reply}
            util.send(json.dumps(response))
            
        elif task.action == "backup":
            reply, ok = self.support.backup_file(task.args["plik"])
            self.logger.info(reply)
            response = {"status": ok, "msg": reply}
            util.send(json.dumps(response))
                                
        elif task.action == "reboot":
            cf = datetime.now().strftime("%H%d%m%Y")
            if task.args["pwd"] == cf:            
                self.logger.info("Reboot requested")
                os.system("sudo shutdown -r now")                    
            else:
                self.logger.error("*** Bad password provided for reboot [%s]" % task.args["pwd"])
            
        else:
            self.enqueue(task)
            response = {"msg": task.response}
            util.send(json.dumps(response))


    def _process_tasks(self):
        # Start a new thread to process the task queue, but only if one is not already queue_processing_thread_activated.
        if not self._get_thread_status():
            self._set_thread_status(True)
            self.logger.info("Starting new queue-processing thread")
            start_new_thread(self._service, ())
        else:
            self.logger.info("The message queue is being processed by an existing thread")            
            
    
    def _service(self):
        while len(self.queue) > 0:
            task = self._dequeue()
            
            if task.action == self.const.photo:
                task.id = "P"
                self.stop_live_video(task)
                self.support.take_photo(task, self.camera)
                task.log_history()
            elif task.action == self.const.video:
                self.stop_live_video(task)
                h264_path = self.support.record_video(task, self.camera)
                start_new_thread(self.support.send_mail_and_backup_video, (task, h264_path))
                # leave this new thread to log the history
            elif task.action == self.const.live_video:
                task.id = "Live video"
                if len(self.queue) > 0:
                    task.add_event("Live video not available - other jobs waiting in queue")
                else: 
                    if not self.camera.is_busy():
                        task.add_event("Starting live video")
                        # start mjpg-streamer process; pause evry N secs to check queue,
                        # and stop if queue not empty
                        self.camera.playing_live_video = True
                        self.support.start_mjpeg_stream(task)

                        #self.support.start_mjpeg_stream(task)
                    else:
                        self.stop_live_video(task)
                        
                task.log_history()
                                       
            
        self._set_thread_status(False);


    def stop_live_video(self, task):
        if self.camera.playing_live_video:
            self.support.stop_mjpeg_stream(task)
            task.add_event("Stopped live video")
            self.camera.playing_live_video = False
        
        
    def enqueue(self, task):
        self.q_lock.acquire()
        try:
            self.queue.append(task)
            task.add_event("Task queued")
            task.response = "Queued task (%s)" % task.action
            self._process_tasks()
            return task
        finally:
            self.q_lock.release()
                    
    def _dequeue(self):
        self.q_lock.acquire()
        try:
            if len(self.queue) > 0:
                task = self.queue[0]
                self.queue.remove(task)
                task.add_event("Task de-queued")
                #self.logger.info("De-queued task: %s" % task.action)
                return task
        finally:
            self.q_lock.release()
            
        return None
        
    def _set_thread_status(self, value):
        self.service_lock.acquire()
        try:
            self.queue_processing_thread_activated = value
        finally:
            self.service_lock.release()
            
    def _get_thread_status(self):
        self.service_lock.acquire()
        try:
            return self.queue_processing_thread_activated
        finally:
            self.service_lock.release()
            

class SocketUtil:
    def __init__(self, sock=None):
        self.terminator = "$$$"
        self.bufflen = 2048
        
        if sock is None:
            self.sock = socket.socket(
                socket.AF_INET, socket.SOCK_STREAM)
        else:
            self.sock = sock    
            
    def send(self, msg):
        # append $$$ delimiter to the messag
        totalsent = 0
        terminated_msg = msg + self.terminator
        total_chars_in_msg = len(terminated_msg)
        while totalsent < total_chars_in_msg:
            sent = self.sock.send(terminated_msg[totalsent:])
            if sent == 0:
                raise RuntimeError("socket connection broken")
            totalsent = totalsent + sent

    def recv(self):
        chunks = []
        bytes_recd = 0
        terminated = False
        
        while not terminated:
            chunk = self.sock.recv(self.bufflen)
            if chunk == '':
                raise RuntimeError("socket connection broken")
            chunks.append(chunk)
            bytes_recd = bytes_recd + len(chunk)
            terminated = chunk.endswith(self.terminator)
            
        return ''.join(chunks)[0:bytes_recd - len(self.terminator)]


class Task:
    def __init__(self, action, args):
        self.id = "-"
        self.action = action
        self.args = args
        self.events = []
        self.start = datetime.now()
        self.logger = logging.getLogger("secam")
        self.response = ""
        #self.events.append(Event(datetime.now(), "Start: %s" % action))
        
    def get_start(self):
        return self.start
        
    def get_start_as_string(self):
        return self.get_start().strftime("%Y/%m/%d %H:%M:%S")
        
    def elapsed(self, to):
        delta = to - self.get_start()
        return "%.3f" % (delta.seconds + (delta.microseconds/1000000.0))
    
    def add_event(self, msg):
        self.events.append(Event(datetime.now(), msg))
        
    def log_history(self):
        self.logger.info("==============================")
        self.logger.info("Task history [%s]" % self.id)
        
        for e in self.events:
            self.logger.info("%s secs: %s", self.elapsed(e.date), e.msg)

        self.logger.info("------------------------------")

        
class Event:
    def __init__(self, date, msg):
        self.date = date
        self.msg = msg
        
