import socket, sys, json, threading
from thread import *
import re, os, dropbox, logging, picamera
from datetime import datetime
import time, subprocess, smtplib
#import RPi.GPIO as GPIO
from email.mime.text import MIMEText
import RPi.GPIO as GPIO
    
# def check_action_messages(camera, ctrl):
#     msg = ctrl.get_message()
#     not_recognized = "Message not recognized [%s]" % msg
#     error = False
#     
#     if msg:
#         if len(msg) > 1:
#             parts = msg.split(",")
#             if len(parts) < 2:
#                 error = True
#             else:
#                 action = parts[1]
#                 
#                 if action == _const.photo:
#                     # Photos can be taken whilst surveillance is paused
#                     time_mark = datetime.now()
#                     take_photo(camera, time_mark)
#                 elif action == _const.stop:
#                     if ctrl.get_status() == _const.go:
#                         camera.set_status(action)
#                         logging.info("STOP message received")
#                     else:
#                         logging.warn("Current status is already STOP")
#                 elif action == _const.go:
#                     if ctrl.get_status() == _const.stop:
#                         camera.set_status(action)
#                         logging.info("GO message received")
#                     else:
#                         logging.warn("Current status is already GO")
#                 elif len(parts) == 3:
#                     arg = parts[2]
#                     if action == _const.brightness:
#                         logging.info("Brightness was set to %s" % camera.get_brightness())
#                         camera.set_brightness(int(arg))
#                         logging.info("Brightness changed to %s" % arg)
#                     elif action == _const.contrast:
#                         logging.info("Contrast was set to %s" % camera.contrast)
#                         camera.set_contrast(int(arg))
#                         logging.info("Contrast changed to %s" % arg)
#                     elif action == _const.exposure_mode:
#                         logging.info("Exposure mode was set to %s" % camera.get_exposure_mode())
#                         camera.set_exposure_mode(arg)
#                         logging.info("Exposure mode changed to %s" % arg)
#                     elif action == _const.iso:
#                         logging.info("ISO sensitivity was set to %s" % camera.get_iso())
#                         camera.set_iso(int(arg))
#                         logging.info("ISO sensitivity changed to %s" % arg)
#                         
#                 else:
#                     error = True
#     
#                 ctrl._dequeue(msg)
#         else:
#             error = True     
#     
#         if error:
#             logging.info(not_recognized)     
                
    
    

def spawn_messaging_service(ctrl):
    ctrl.start()        

# def spawn_messaging_client(ctrl, camera):
#     timer = 0
#     small_sleep = 0.2
#     big_sleep = 2
#     
#     while True:        
#         if timer >= big_sleep:
#             timer = 0
#             check_action_messages(camera, ctrl)
#             
#         time.sleep(small_sleep)
#         timer += small_sleep

    
class Constants:
    def __init__(self):
        self.user = "georgeb"
        self.pwd = "giga8yte"
        self.videotype = "h264"
        self.imagetype = "jpg"
        self.ctx = "/secam/"
        self.app = self.ctx + "app/"
        self.index_page_path = self.app + "index.py"
        self.webroot = "/var/www/html/"
        self.video_subfolder = "video/"
        self.video_folder = self.webroot + self.video_subfolder
        self.backup_register = "resource/backup-register"
        self.go = "go"
        self.stop = "stop"
        self.photo = "photo"
        self.settings = "settings"
        self.brightness = "brightness"
        self.contrast = "contrast"
        self.iso = "iso"
        self.exposure_mode = "mode"
        self.ok = "ok"
        self.q = "q"
        self.dq = "dq"
        self.nr = "-1"
        self.next = "next"
        self.getq = "getq"
        self.clrq = "clrq"
        self.close = "close"
        self.stat = "status"
        self.host = ''   # Symbolic name, meaning all available interfaces
        self.port = 8888 # Arbitrary non-privileged port    


class Document:
    def __init__(self, filename, const):
        self.event = None
        self.filename = filename
        self.path = const.video_folder + filename
        self.backedup = False
        self.timestamp = None
        self.size = None
        m = re.search("(P|\d{1,})-(\d{14})\.[%s|%s]" % (const.videotype, const.imagetype), filename)

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
        self.brightness = "50"
        self.contrast = "0"
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
                logging.info("Video recording completed")
                
            self.recording = False
        else:
            logging.warn("Camera is already recording")
               
        return file_path

    def capture_photo(self, file_path):
        with picamera.PiCamera() as camera:
            self._prepare(camera)
            camera.capture(file_path)
            self._complete(camera)
            logging.info("Photo taken")
               
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
        response["settings"] = self.get_settings()
        response[self.const.stat] = self.status
        return response
    
    def set_setting(self, ctrl, value):
        if ctrl == self.const.brightness:
            self.brightness = value
        elif ctrl == self.const.contrast:
            self.contrast = value
        elif ctrl == self.const.exposure_mode:
            self.exposure_mode = value
        elif ctrl == self.const.iso:
            self.iso = value
        
    
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
            logging.debug("Sending message [%s] ..." % s)
            sock.sendall(s)
            
            # Wait for a response
            s = sock.recv(2048)
            
            # Unmarshall the returned json string into an object, and return same to caller
            logging.debug("... received response [%s]" % s)
            return s if return_json else json.loads(s)
        finally:
            try:
                sock.close()
            except:
                logging.warn("Failed to close the socket")
            
        return self.const.nr;

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
        logging.debug('Socket created')
        
        # Bind socket to local host and port
        try:
            self.server.bind((self.const.host, self.const.port))
        except socket.error as msg:
            logging.error('Bind failed. Error Code : ' + str(msg[0]) + ' Message ' + msg[1])
            sys.exit()
             
        logging.debug('Socket bind complete')
         
        # Start listening on socket
        self.server.listen(5)
        logging.debug('Socket now listening')
         
        # now keep talking with the client
        try:
            while True:
                # wait to accept a connection - blocking call
                conn, addr = self.server.accept()
                
                dataStr = conn.recv(2048).strip(" \t\n\r")
                obj = json.loads(dataStr)
                action = obj["action"]
                args = obj["args"]
                
                # Do actions that require an immediate response here
                if action == self.const.stat:
                    conn.sendall(json.dumps(self.camera.get_status()))
                    
                elif action == "get_file_register":
                    response = self._get_videos()
                    conn.sendall(json.dumps(response))
                    
                elif action == "camera":
                    camera_ctrl = args["ctrl"]
                    value = args["value"]
                    self.camera.set_setting(camera_ctrl, value)
                    conn.sendall(json.dumps(self.camera.get_status()))
                    
                elif action == self.const.stop:
                    if self.camera.status != self.const.stop:
                        self.camera.status = self.const.stop
                        conn.sendall(json.dumps(self.camera.get_status()))
                    else:
                        logging.info("Camera already stopped")
                        
                elif action == self.const.go:
                    if self.camera.status != self.const.go:
                        self.camera.status = self.const.go
                        conn.sendall(json.dumps(self.camera.get_status()))
                    else:
                        logging.info("Camera already started")
                        
                elif action == self.const.stop:
                    if self.camera.status != self.const.stop:
                        self.camera.status = self.const.stop
                        conn.sendall(json.dumps(self.camera.get_status()))
                        
                elif action == "delete":
                    reply, ok = self._delete_files(args["files"])
                    logging.info(reply)
                    obj = {"status": ok, "msg": reply}
                    conn.sendall(json.dumps(obj))
                    
                elif action == "backup":
                    reply, ok = self._backup_file(args["plik"])
                    logging.info(reply)
                    obj = {"status": ok, "msg": reply}
                    conn.sendall(json.dumps(obj))
                    
                else:
                    self.enqueue(obj)
                    self._process_tasks()
                
                conn.close()
                    
        except KeyboardInterrupt:
            logging.info("Keyboard interrupt")
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
            obj = self._dequeue()
            action = obj["action"]
            args = obj["args"]
            
            if action == self.const.photo:
                self._take_photo(datetime.now())
            elif action == "video":
                h264_path = self.record_video(args["event_id"], args["time_mark"])
                start_new_thread(self._send_mail_and_backup_video, (args["event_id"], args["time_mark"], h264_path))
            
        self._set_thread_status(False);

    def enqueue(self, obj):
        self.q_lock.acquire()
        try:
            self.queue.append(obj)
            logging.info("En-queued message: %s" % obj)
            return obj
        finally:
            self.q_lock.release()
                    
    def _dequeue(self):
        self.q_lock.acquire()
        try:
            if len(self.queue) > 0:
                obj = self.queue[0]
                self.queue.remove(obj)
                logging.info("De-queued message: %s" % obj)
                return obj
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
    
    def record_video(self, event_id, time_mark):
        logging.info("%d) Motion detected! ..." % event_id)
        h264_path = ''.join([self.const.video_folder, self._get_filename_prefix(event_id, time_mark), ".h264"])
        self._out(event_id, "recording to [%s]" % h264_path)
        # PIR stays high for 8 secs, then low for 8 secs; cycle is 16 secs
        self.camera.record_video(h264_path, 16)
        return h264_path
    
    def _convert2mp4(self, event_id, h264_path):
        mp4_path = h264_path.replace("h264", "mp4")
        self._out(event_id, "converting to [%s]" % mp4_path)
        try:
            subprocess.check_call(["/usr/bin/MP4Box", "-add", h264_path, mp4_path], stdout=self.null_device, stderr=self.null_device)
            self._out(event_id, "video conversion complete")
            os.remove(h264_path)
            self._out(event_id, "deleted h264 file")
        except subprocess.CalledProcessError as e:
            self._out(event_id, "*** error converting record_video file [%s]" % e)
        
        return mp4_path
    
    def _send_mail(self, event_id, time_mark):
        mail_from = "george@slepeweb.com"
        mail_to = "george@buttigieg.org.uk"
        web_page = "http://www.slepeweb.com/secam/app/py/index.py"
        mail_body = """
        A security alarm (#%d) has been raised @ %s.
        
        Please investigate further @ %s
        """
    
        event_time = time_mark.strftime("%Y/%m/%d %H:%M:%S")
        msg = MIMEText(mail_body % (event_id, event_time, web_page))
        msg['Subject'] = "Security Alarm"
        msg['From'] = mail_from
        msg['To'] = mail_to
        server = None
        
        try:
            self._out(event_id, "sending mail")
            server = smtplib.SMTP("smtp.gmail.com", 587)
            server.starttls()
            server.login("george.buttigieg@gmail.com", "br1cktop1")
            server.sendmail(mail_from, [mail_to], msg.as_string())
            self._out(event_id, "mail sent ok")
        except:
            self._out(event_id, "*** problem sending mail")
            
        finally:
            if server != None:
                server.quit() 
    
    def _take_photo(self, time_mark):
        file_path = ''.join([self.const.video_folder, self._get_filename_prefix("P", time_mark), ".jpg"])
        self.camera.capture_photo(file_path) 
        
    def _file_part(self, file_path):
        return file_path.split("/")[-1]
    
    def _send_mail_and_backup_video(self, event_id, time_mark, h264_path):
        self._send_mail(event_id, time_mark)    
        mp4_path = self._convert2mp4(event_id, h264_path)    
        msg, ok = self._backup_file(self._file_part(mp4_path))
        self._out(event_id, msg)
    
    def _spawn_remaining_tasks(self, event_id, time_mark, h264_path):
        task = threading.Thread(target=self._send_mail_and_backup_video, args=[event_id, time_mark, h264_path])
        task.start()        

    def _out(self, event_id, s):
        logging.info("%d) ... %s" % (event_id, s))
    
        
class Spibox:
    def __init__(self, ctrl):
        self.ctrl = ctrl
        self.const = Constants()
        GPIO.setmode(GPIO.BCM)
        GPIO.setup(PIR, GPIO.IN, GPIO.PUD_DOWN)
        start_time = time.time()
         
        while GPIO.input(PIR) == GPIO.HIGH:
            time.sleep(0.1)
        
        stop_time = time.time()
        logging.info("Sensor reset took: %0.1f secs" % (stop_time - start_time))

        
    def _service(self):
        # From observation, it seems that the PIR will go LOW after being HIGH for 8 seconds,
        # then will not go high again for another 8 seconds. This means that there will always
        # be 8 second gaps in videos taken during a long event.
        
        try:    
            while True:
                if GPIO.wait_for_edge(PIR, GPIO.RISING): 
                    time_mark = datetime.now()
                    time_str = time_mark.strftime("%Y/%m/%d %H:%M:%S")
                       
                    if self.ctrl.get_status() == self.const.go:
                        self.ctrl.enqueue({"action": "video", "args": {"event_id": EVENT_COUNTER, "time_mark": time_mark}})
                        EVENT_COUNTER += 1
                    else:
                        logging.info("Alarm raised at %s, but surveillance is currently OFF" % time_str)
                 
                 
        except KeyboardInterrupt:
            logging.info("Keyboard interrupt - Spibox thread terminating")
            GPIO.cleanup()


if __name__ == "__main__":
    logging.basicConfig(filename="/var/www/html/log/secam.log", format="%(asctime)s (%(filename)s) [%(levelname)s] %(message)s", level=logging.DEBUG)
    logging.info("====================================================================")
    logging.info("Secam application started")

    EVENT_COUNTER = 1
    PIR = 4
    
    CAMERA = Camera()
    CTRL = SecamController(CAMERA)
    
    start_new_thread(spawn_messaging_service, (CTRL,))     
    Spibox(CTRL)._service()
