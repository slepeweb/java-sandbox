import constants, document, subprocess, os, time
import re, dropbox, smtplib
from email.mime.text import MIMEText

class Support:
    
    def __init__(self):
        self.const = constants.Constants();
        self.null_device = open(os.devnull, 'w')
    
    def get_videos(self):
        a = []  
        register = self.get_backup_register()
        for f in os.listdir(self.const.video_folder):
            if re.match(".*?(mp4|jpg)$", f): # In case any failed conversions are still lying around
                obj = {}
                obj['filename'] = f
                obj['backedup'] = register.has_key(f)
                a.append(obj)
        
        return a
    
    def get_videos_as_documents(self):
        return self.to_docs(self.get_videos())

    def get_backup_register(self):
        entries = {}
        with open(self.const.webroot + self.const.backup_register, 'r') as f:
            for filename in f:
                entries[filename.strip()] = 1
        
        return entries

    # Copy source_file to dropbox folder
    def backup_file(self, source_file):
        file_exists = False
        for d in self.get_videos_as_documents():
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
            self.update_backup_register(source_file)
            return "Uploaded %s to dropbox path %s (%d bytes)" % (source_file_path, dest_file_path, resp['bytes']), True
        except Exception as err:
            return "Failed to upload %s [%s]" % (source_file_path, err), False
    
    def update_backup_register(self, backup_filename):
        # Identify videos stored locally on webserver; store in a dictionary
        videos_stored_locally = {}
        for d in self.get_videos_as_documents():
            videos_stored_locally[d.filename] = d
            
        # Identify files previously backed up that are still resident on the web server
        register = self.get_backup_register()
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
    
    def delete_files(self, files):
        count = 0
        for d in self.get_videos_as_documents():
            for filename in files:
                if d.filename == filename:
                    try:
                        os.remove(self.const.video_folder + filename)
                        count += 1
                    except:
                        return "Failed to delete file [%s]" % filename, False
        
        return "Deleted %d file(s)" % count, count == len(files)
    
    def get_filename_prefix(self, event_id, time_mark):
        return "%s-%s" % (event_id, time_mark.strftime("%Y%m%d%H%M%S"))
    
    def to_docs(self, get_video_results):
        docs = []
        for obj in get_video_results:
            d = document.Document(obj['filename'], self.const)
            d.backedup = obj['backedup']
            docs.append(d)
        return docs
             
    def record_video(self, task, camera):
        h264_path = ''.join([self.const.video_folder, self.get_filename_prefix(task.id, task.get_start()), ".h264"])
        # PIR stays high for 8 secs, then low for 8 secs; cycle is 16 secs
        if camera.record_video(h264_path, 16):
            task.add_event("video recorded [%s]" % h264_path)
            return True
        else:
            task.add_event("video recording failed [%s]" % h264_path)
            return False
    
    def convert2mp4(self, task, h264_path):
        mp4_path = h264_path.replace("h264", "mp4")

        try:
            subprocess.check_call(["/usr/bin/MP4Box", "-add", h264_path, mp4_path], stdout=self.null_device, stderr=self.null_device)
            task.add_event("video conversion complete")
            os.remove(h264_path)
            task.add_event("deleted h264 file")
        except subprocess.CalledProcessError as e:
            task.add_event("*** error converting record_video file [%s]" % e)
        
        return mp4_path
    

    def start_mjpeg_stream(self, task):
        if os.system("/home/pi/mjpg-streamer.sh start > /dev/null 2>&1") == 0:
            task.add_event("Started mjpeg-streamer")
            time.sleep(1)
        else:
            task.add_event("*** error starting mjpg-streamer")
        
   
    def stop_mjpeg_stream(self, task):
        if os.system("/home/pi/mjpg-streamer.sh stop > /dev/null") == 0:
            task.add_event("Stopped mjpeg-streamer")
            time.sleep(1)
        else:
            task.add_event("*** error stopping mjpg-streamer")
        
   
    def send_mail(self, task):
        mail_from = "george@slepeweb.com"
        mail_to = "george@buttigieg.org.uk"
        web_page = "http://www.slepeweb.com/secam/app/py/index.py"
        mail_body = """
        A security alarm (#%d) has been raised @ %s.
        
        Please investigate further @ %s
        """
    
        event_time = task.get_start_as_string()
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
    
    def take_photo(self, task, camera):
        file_path = ''.join([self.const.video_folder, self.get_filename_prefix("P", task.get_start()), ".jpg"])
        if camera.capture_photo(file_path): 
            task.add_event("Photo captured")
            return True
        else:
            task.add_event("*** Failed to capture photo")
            return False
            
        
    def file_part(self, file_path):
        return file_path.split("/")[-1]
    
    def send_mail_and_backup_video(self, task, h264_path):
        self.send_mail(task)    
        mp4_path = self.convert2mp4(task, h264_path)    
        msg, ok = self.backup_file(self.file_part(mp4_path))
        task.add_event(msg)
        task.log_history()
