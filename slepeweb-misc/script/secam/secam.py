import socket, sys, json, threading
from thread import *
import re, os, dropbox, logging, logging.handlers, picamera
from datetime import datetime
import time, subprocess, smtplib
#import RPi.GPIO as GPIO
from email.mime.text import MIMEText
import RPi.GPIO as GPIO
       

def spawn_messaging_service(ctrl):
    ctrl.start()        

    
class Constants:
    def __init__(self):
        self.videotype = "mp4"
        self.imagetype = "jpg"
        
        self.webroot = "/var/www/html/"
        self.video_folder = self.webroot + "video/"
        self.app_folder_web = "/secam/app/"
        self.video_folder_web = self.app_folder_web + "video/"
        self.backup_register = "resource/backup-register"
        
        self.go = "go"
        self.stop = "stop"
        self.photo = "photo"
        self.video = "video"
       
        self.settings = "settings"
        self.brightness = "brightness"
        self.contrast = "contrast"
        self.iso = "iso"
        self.exposure_mode = "mode"
        
        self.msg = "msg"
        self.stat = "status"
        self.host = ''   # Symbolic name, meaning all available interfaces
        self.port = 8888 # Arbitrary non-privileged port    


class Document:
    def __init__(self, filename, const):
        self.event = None
        self.filename = filename
        self.path = const.video_folder + filename
        self.backedup = False
        self.timestamp = "20000101235959" # a default/arbitrary date
        self.size = None
        m = re.search("(P|\d{1,})-(\d{14})\.(%s|%s)" % (const.videotype, const.imagetype), filename)

        if m:
            self.timestamp = m.group(2)
            self.event = m.group(1)
            self.size = self._get_file_size()
            
    def _get_file_size(self):
        l = os.stat(self.path).st_size
        thousand = 1000
        million = 1000000
        
        if l < thousand:
            return "%d bytes" % l
        elif l < million:
            return "%d Kb" % (l/thousand)
        else:
            return "%d Mb" % (l/million)
    
    def get_date(self):
        return datetime.strptime(self.timestamp, '%Y%m%d%H%M%S') 
    
    def to_obj(self):
        obj = {}
        obj['event'] = self.event
        obj['filename'] = self.filename
        obj['path'] = self.path
        obj['backedup'] = self.backedup
        obj['timestamp'] = self.timestamp
        obj['size'] = self.size
        return obj        
        
class Camera:
    def __init__(self):
        self.const = Constants()
        self.status = self.const.stop
        self.brightness = "70"
        self.contrast = "70"
        self.exposure_mode = "auto"
        self.iso = "0"
        self.recording = False
        
    def record_video(self, file_path, duration):
        if not self.recording:
            self.recording = True
            
            with picamera.PiCamera() as camera:
                self._prepare(camera)
                camera.start_recording(file_path, quality=23)
                camera.wait_recording(duration)
                camera.stop_recording() 
                self._complete(camera)
                #LOG.info("Video recording completed")
                
            self.recording = False
        else:
            LOG.warn("Camera is already recording")
               
        return file_path

    def capture_photo(self, file_path):
        with picamera.PiCamera() as camera:
            self._prepare(camera)
            camera.capture(file_path)
            self._complete(camera)
            LOG.info("Photo taken")
               
        return file_path

    def _prepare(self, camera):
        camera.resolution = (1280, 720)
        camera.vflip = True
        camera.brightness = int(self.brightness)
        camera.contrast = int(self.contrast)
        camera.exposure_mode = self.exposure_mode
        camera.iso = int(self.iso)
        camera.start_preview()

    def _complete(self, camera):
        camera.stop_preview()
        camera.close()

    def set_status(self, s):
        if s in [self.const.stop, self.const.go]:
            self.status = s
        
    def get_status(self):
        response = {}
        response[self.const.settings] = self.get_settings()
        response[self.const.msg] = "Surveillance is " + ("on" if self.status == self.const.go else "paused")
        response[self.const.stat] = self.status
        return response
    
    def set_setting(self, ctrl, value):
        if ctrl == self.const.brightness:
            self.brightness = value
            return "Brightness set to '%s'" % value
        elif ctrl == self.const.contrast:
            self.contrast = value
            return "Contrast set to '%s'" % value
        elif ctrl == self.const.exposure_mode:
            self.exposure_mode = value
            return "Exposure mode set to '%s'" % value
        elif ctrl == self.const.iso:
            self.iso = value
            return "ISO set to '%s'" % value
        
    
    def get_settings(self):
        return { 
                self.const.brightness: self.brightness,
                self.const.contrast: self.contrast,
                self.const.exposure_mode: self.exposure_mode,
                self.const.iso: self.iso}
                


class SecamControllerClient:    
    def __init__(self):
        self.const = Constants()
        
    def send_message(self, obj, return_json=False):
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

        try:
            # Connect to the server
            sock.connect((self.const.host, self.const.port))
            
            # Marshall the supplied object into a string, and send to the server
            s = json.dumps(obj)
            LOG.debug("Sending message [%s] ..." % s)
            sock.sendall(s)
            
            # Wait for a response
            s = sock.recv(2048)
            
            # Unmarshall the returned json string into an object, and return same to caller
            LOG.debug("... received response [%s]" % s)
            return s if return_json else json.loads(s)
        finally:
            try:
                sock.close()
            except:
                LOG.warn("Failed to close the socket")
            
        return -1;

class SecamController:
    def __init__(self, camera):
        self.const = Constants()
        self.camera = camera
        self.server = None
        self.queue = []
        self.counter = 0
        self.running = False
        self.null_device = open(os.devnull, 'w')
        self.q_lock = threading.Lock()
        self.service_lock = threading.Lock()

     
    def start(self):
        self.server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        LOG.debug('Socket created')
        
        # Bind socket to local host and port
        try:
            self.server.bind((self.const.host, self.const.port))
        except socket.error as msg:
            LOG.error('Bind failed. Error Code : ' + str(msg[0]) + ' Message ' + msg[1])
            sys.exit()
             
        LOG.debug('Socket bind complete')
         
        # Start listening on socket
        self.server.listen(5)
        LOG.debug('Socket now listening')
         
        # now keep talking with the client
        try:
            while True:
                # wait to accept a connection - blocking call
                conn, addr = self.server.accept()
                
                dataStr = conn.recv(2048).strip(" \t\n\r")
                obj = json.loads(dataStr)
                task = Task(obj["action"], obj["args"])
                
                # Do actions that require an immediate response here
                if task.action == self.const.stat:
                    response = self.camera.get_status()
                    # Return response as a json string
                    conn.sendall(json.dumps(response))
                    
                elif task.action == "get_file_register":
                    response = self._get_videos()
                    conn.sendall(json.dumps(response))
                    
                elif task.action == "camera":
                    camera_ctrl = task.args["ctrl"]
                    value = task.args["value"]
                    LOG.info("Setting camera %s to %s" % (camera_ctrl, value))
                    
                    msg = self.camera.set_setting(camera_ctrl, value)
                    response = self.camera.get_status()
                    response["msg"] = msg
                    conn.sendall(json.dumps(response))
                    
                elif task.action == self.const.go:
                    if self.camera.status != self.const.go:
                        self.camera.status = self.const.go
                        response = self.camera.get_status()
                        response["msg"] = "Surveillance is on"
                        LOG.info(response["msg"])
                        conn.sendall(json.dumps(response))
                    else:
                        msg = "Surveillance is already on"
                        response = {}
                        response["msg"] = msg
                        LOG.info(msg)
                        conn.sendall(json.dumps(response))
                        
                elif task.action == self.const.stop:
                    if self.camera.status != self.const.stop:
                        self.camera.status = self.const.stop
                        response = self.camera.get_status()
                        response["msg"] = "Surveillance paused"
                        LOG.info(response["msg"])
                        conn.sendall(json.dumps(response))
                    else:
                        msg = "Surveillance is already paused"
                        response = {}
                        response["msg"] = msg
                        LOG.info(msg)
                        conn.sendall(json.dumps(response))
                        
                elif task.action == "delete":
                    reply, ok = self._delete_files(task.args["files"])
                    LOG.info(reply)
                    obj = {"status": ok, "msg": reply}
                    conn.sendall(json.dumps(obj))
                    
                elif task.action == "backup":
                    reply, ok = self._backup_file(task.args["plik"])
                    LOG.info(reply)
                    obj = {"status": ok, "msg": reply}
                    conn.sendall(json.dumps(obj))
                                        
                elif task.action == "reboot":
                    cf = datetime.now().strftime("%H%d%m%Y")
                    if task.args["pwd"] == cf:            
                        LOG.info("Reboot requested")
                        os.system("sudo shutdown -r now")                    
                    else:
                        LOG.error("*** Bad password provided for reboot [%s]" % task.args["pwd"])
                    
                else:
                    self.enqueue(task)
                
                conn.close()
                    
        except KeyboardInterrupt:
            LOG.info("Keyboard interrupt")
        finally:
            self.server.close()
    
    def _to_docs(self, get_video_results):
        docs = []
        for obj in get_video_results:
            d = Document(obj['filename'], self.const)
            d.backedup = obj['backedup']
            docs.append(d)
        return docs
             
    def _process_tasks(self):
        if not self._get_thread_status():
            self._set_thread_status(True)
            start_new_thread(self._service, ())
    
    def _service(self):
        while len(self.queue) > 0:
            task = self._dequeue()
            
            if task.action == self.const.photo:
                task.id = "P"
                self._take_photo(task)
                task.log_history()
            elif task.action == self.const.video:
                h264_path = self._record_video(task)
                start_new_thread(self._send_mail_and_backup_video, (task, h264_path))
                # leave this new thread to log the history
            
        self._set_thread_status(False);

    def enqueue(self, task):
        self.q_lock.acquire()
        try:
            self.queue.append(task)
            LOG.info("Queued task: %s" % task.action)
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
                LOG.info("De-queued task: %s" % task.action)
                return task
        finally:
            self.q_lock.release()
            
        return None
        
    def _set_thread_status(self, value):
        self.service_lock.acquire()
        try:
            self.running = value
        finally:
            self.service_lock.release()
            
    def _get_thread_status(self):
        self.service_lock.acquire()
        try:
            return self.running
        finally:
            self.service_lock.release()
            
    def _get_videos(self):
        a = []  
        register = self._get_backup_register()
        for f in os.listdir(self.const.video_folder):
            if re.match(".*?(mp4|jpg)$", f): # In case any failed conversions are still lying around
                obj = {}
                obj['filename'] = f
                obj['backedup'] = register.has_key(f)
                a.append(obj)
        
        return a
    
    def _get_videos_as_documents(self):
        return self._to_docs(self._get_videos())

    def _get_backup_register(self):
        entries = {}
        with open(self.const.webroot + self.const.backup_register, 'r') as f:
            for filename in f:
                entries[filename.strip()] = 1
        
        return entries

    # Copy source_file to dropbox folder
    def _backup_file(self, source_file):
        file_exists = False
        for d in self._get_videos_as_documents():
            if d.filename == source_file:
                file_exists = True
                break
                
        if not file_exists:
            return "File not found [%s]" % source_file, False 
    
        access_token = '4wPGw33d4lcAAAAAAAAAfVl873ag8OxmIoay_NNAGqol8rtv8QH3oPEADSSHiLhf'
        dropbox_client = dropbox.client.DropboxClient(access_token)
    
        source_file_path = self.const.video_folder + source_file
        dest_file_path = '/' + source_file
        
        try:
            f = open(source_file_path, 'rb')
            resp = dropbox_client.put_file(dest_file_path, f)
            self._update_backup_register(source_file)
            return "Uploaded %s to dropbox path %s (%d bytes)" % (source_file_path, dest_file_path, resp['bytes']), True
        except Exception as err:
            return "Failed to upload %s [%s]" % (source_file_path, err), False
    
    def _update_backup_register(self, backup_filename):
        # Identify videos stored locally on webserver; store in a dictionary
        videos_stored_locally = {}
        for d in self._get_videos_as_documents():
            videos_stored_locally[d.filename] = d
            
        # Identify files previously backed up that are still resident on the web server
        register = self._get_backup_register()
        for old_backup_filename in register:
            d = videos_stored_locally.get(old_backup_filename)
            if d != None:
                d.backedup = True
        
        # Mark this latest file as backed up        
        d = videos_stored_locally.get(backup_filename)
        d.backedup = True 
        
        # Re-write the register file
        with open(self.const.webroot + self.const.backup_register, 'w') as f:
            for key in videos_stored_locally:
                d = videos_stored_locally.get(key)
                if d.backedup:
                    f.write(d.filename + "\n")
    
    def _delete_files(self, files):
        count = 0
        for d in self._get_videos_as_documents():
            for filename in files:
                if d.filename == filename:
                    try:
                        os.remove(self.const.video_folder + filename)
                        count += 1
                    except:
                        return "Failed to delete file [%s]" % filename, False
        
        return "Deleted %d file(s)" % count, count == len(files)
    
    def _get_filename_prefix(self, event_id, time_mark):
        return "%s-%s" % (event_id, time_mark.strftime("%Y%m%d%H%M%S"))
    
    def _record_video(self, task):
        h264_path = ''.join([self.const.video_folder, self._get_filename_prefix(task.id, task.get_start()), ".h264"])
        # PIR stays high for 8 secs, then low for 8 secs; cycle is 16 secs
        self.camera.record_video(h264_path, 16)
        task.add_event("video recorded [%s]" % h264_path)
        return h264_path
    
    def _convert2mp4(self, task, h264_path):
        mp4_path = h264_path.replace("h264", "mp4")

        try:
            subprocess.check_call(["/usr/bin/MP4Box", "-add", h264_path, mp4_path], stdout=self.null_device, stderr=self.null_device)
            task.add_event("video conversion complete")
            os.remove(h264_path)
            task.add_event("deleted h264 file")
        except subprocess.CalledProcessError as e:
            task.add_event("*** error converting record_video file [%s]" % e)
        
        return mp4_path
    
    def _send_mail(self, task):
        mail_from = "george@slepeweb.com"
        mail_to = "george@buttigieg.org.uk"
        web_page = "http://www.slepeweb.com/secam/app/py/index.py"
        mail_body = """
        A security alarm (#%d) has been raised @ %s.
        
        Please investigate further @ %s
        """
    
        event_time = task.get_start().strftime("%Y/%m/%d %H:%M:%S")
        msg = MIMEText(mail_body % (task.id, event_time, web_page))
        msg['Subject'] = "Security Alarm"
        msg['From'] = mail_from
        msg['To'] = mail_to
        server = None
        
        try:
            task.add_event("sending mail")
            server = smtplib.SMTP("smtp.gmail.com", 587)
            server.starttls()
            server.login("george.buttigieg@gmail.com", "g1ga5Eftg00g6E")
            server.sendmail(mail_from, [mail_to], msg.as_string())
            task.add_event("mail sent ok")
        except:
            task.add_event("*** problem sending mail")
            
        finally:
            if server != None:
                server.quit() 
    
    def _take_photo(self, task):
        file_path = ''.join([self.const.video_folder, self._get_filename_prefix("P", task.get_start()), ".jpg"])
        self.camera.capture_photo(file_path) 
        task.add_event("photo captured")
        
    def _file_part(self, file_path):
        return file_path.split("/")[-1]
    
    def _send_mail_and_backup_video(self, task, h264_path):
        self._send_mail(task)    
        mp4_path = self._convert2mp4(task, h264_path)    
        msg, ok = self._backup_file(self._file_part(mp4_path))
        task.add_event(msg)
        task.log_history()
        
        
class Spibox:
    def __init__(self, ctrl):
        self.ctrl = ctrl
        self.const = Constants()
        self.event_counter = 1
        
        GPIO.setmode(GPIO.BCM)
        GPIO.setup(PIR, GPIO.IN, GPIO.PUD_DOWN)
        start_time = time.time()
         
        while GPIO.input(PIR) == GPIO.HIGH:
            time.sleep(0.1)
        
        stop_time = time.time()
        LOG.info("Sensor reset took: %0.1f secs" % (stop_time - start_time))

        
    def _service(self):
        # From observation, it seems that the PIR will go LOW after being HIGH for 8 seconds,
        # then will not go high again for another 8 seconds. This means that there will always
        # be 8 second gaps in videos taken during a long event.
        
        try:    
            while True:
                if GPIO.wait_for_edge(PIR, GPIO.RISING):
                    if self.ctrl.camera.get_status()[self.const.stat] == self.const.go:
                        task = Task(self.const.video, {})
                        task.id = self.event_counter
                        self.ctrl.enqueue(task)
                        self.event_counter += 1
                    else:
                        LOG.info("Alarm raised at %s, but surveillance is currently OFF" % datetime.now().strftime("%Y/%m/%d %H:%M:%S"))
                 
                 
        except KeyboardInterrupt:
            LOG.info("Keyboard interrupt - Spibox thread terminating")
            GPIO.cleanup()


class Event:
    def __init__(self, date, msg):
        self.date = date
        self.msg = msg
        
    
class Task:
    def __init__(self, action, args):
        self.id = "-"
        self.action = action
        self.args = args
        self.events = []
        self.events.append(Event(datetime.now(), "Start: %s" % action))
        
    def get_start(self):
        return self.events[0].date
        
    def elapsed(self, to):
        delta = to - self.get_start()
        return "%.3f" % (delta.seconds + (delta.microseconds/1000000.0))
    
    def add_event(self, msg):
        self.events.append(Event(datetime.now(), msg))
        
    def log_history(self):
        LOG.info("==============================")
        LOG.info("Task history [%s]" % self.id)
        
        for e in self.events:
            LOG.info("%s secs: %s", self.elapsed(e.date), e.msg)

        LOG.info("------------------------------")
        
LOG = logging.getLogger("secam")

if __name__ == "__main__":
    LOG.setLevel(logging.INFO)
    fh = logging.handlers.RotatingFileHandler("/var/www/html/log/secam.log", maxBytes=128000, backupCount=5)
    fh.setFormatter(logging.Formatter("%(asctime)s (%(filename)s) [%(levelname)s] %(message)s"))
    LOG.addHandler(fh)
    
    LOG.info("====================================================================")
    LOG.info("Secam application started")

    PIR = 4    
    CTRL = SecamController(Camera())    
    start_new_thread(spawn_messaging_service, (CTRL,))     
    Spibox(CTRL)._service()
