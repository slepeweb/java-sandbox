import constants, document, subprocess, os, time
import re, smtplib
from email.mime.text import MIMEText
import dropbox
from dropbox.files import WriteMode
from dropbox.exceptions import ApiError, AuthError

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
    def backup_file(self, task, source_file):
        msg = ""
        file_exists = False
        for d in self.get_videos_as_documents():
            if d.filename == source_file:
                file_exists = True
                break
                
        if not file_exists:
            msg = "File not found [%s]" % source_file
            task.add_event(msg)
            return msg, False
    
        access_token = '4wPGw33d4lcAAAAAAAAE2yEVxIuEqa8tJugyLWewvArmg79Hhd-9e9DUrDU4hKXj'
        dbx = dropbox.Dropbox(access_token)
    
        # Check that the access token is valid
        try:
            dbx.users_get_current_account()
        except AuthError as err:
            msg = "ERROR: Invalid access token"
            task.add_event(msg)
            return msg, False
            
        source_file_path = self.const.video_folder + source_file
        dest_file_path = '/' + source_file
        
        with open(source_file_path, 'rb') as f:
            try:
                dbx.files_upload(f.read(), dest_file_path, mode=WriteMode('overwrite'))
            except ApiError as err:
                tmplt = "Failed to upload %s [%s]"
                if (err.error.is_path() and
                        err.error.get_path().error.is_insufficient_space()):
                    task.add_event(tmplt % (source_file_path, "Insufficient space"))                    
                elif err.user_message_text:
                    task.add_event(err.user_message_text)                    
                else:
                    task.add_event(tmplt % (source_file_path, err)) 
                    
                return "Error uploading file to Dropbox", False
                   
            self.update_backup_register(source_file)
            msg = "Uploaded %s to dropbox path %s" % (source_file_path, dest_file_path)
            task.add_event(msg)
            return msg, True
    
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
            return h264_path
        else:
            task.add_event("video recording failed [%s]" % h264_path)
            return None
        
    
    def convert2mp4(self, task, h264_path):
        mp4_path = h264_path.replace("h264", "mp4")
        ok = True

        try:
            subprocess.check_call(["/usr/bin/MP4Box", "-add", h264_path, mp4_path], stdout=self.null_device, stderr=self.null_device)
            task.add_event("video conversion complete")
            os.remove(h264_path)
            task.add_event("deleted h264 file")
        except subprocess.CalledProcessError as e:
            task.add_event("*** error converting record_video file [%s]" % e)
            ok = False
        
        return mp4_path, ok
    

    def start_mjpeg_stream(self, task, camera):
        if not camera.playing_live_video:
            if os.system("/home/pi/mjpg-streamer.sh start > /dev/null 2>&1") == 0:
                time.sleep(1)
                task.add_event("Started mjpeg-streamer")
                camera.playing_live_video = True
            else:
                task.add_event("*** error starting mjpg-streamer")
        else:
            task.add_event("*** mjpg-streamer already active")
        
   
    def stop_mjpeg_stream(self, task, camera):
        if camera.playing_live_video:        
            if os.system("/home/pi/mjpg-streamer.sh stop > /dev/null") == 0:
                time.sleep(1)
                task.add_event("Stopped mjpeg-streamer")
                camera.playing_live_video = False
            else:
                task.add_event("*** error stopping mjpg-streamer")
        else:
            task.add_event("*** mjpg-streamer was not active")
        
   
    def send_mail(self, task):
        mail_from = "donna@buttigieg.org.uk"
        mail_to = "george@buttigieg.org.uk"
        web_page = "http://secam.buttigieg.org.uk/py/index.py"
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
            server.login("george.buttigieg@gmail.com", "g!g@5Eftg00g6E")
            server.sendmail(mail_from, [mail_to], msg.as_string())
            task.add_event("mail sent ok")
        except Exception as err:
            task.add_event("*** problem sending mail [%s]" % err, "ERROR")
            
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
        mp4_path, ok = self.convert2mp4(task, h264_path)    
            
        if ok:
            self.backup_file(task, self.file_part(mp4_path))

        task.log_history()
