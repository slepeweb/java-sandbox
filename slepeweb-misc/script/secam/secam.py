import re, os, dropbox
from datetime import datetime

videotype = "avi"
ctx = "/secam/"
app = ctx + "app/"
webroot = "/var/www/html/"
video_subfolder = "video/"
video_folder = webroot + video_subfolder
backup_register = ".backup-register"

def get_videos():
    a = []  
    register = get_backup_register()
    for f in os.listdir(video_folder):
        d = Document(f)
        if d.event:
            d.backedup = register.has_key(d.filename)
            a.append(d)
                
    return a

# Copy source_file to dropbox folder
def backup_file(source_file):
    file_exists = False
    for d in get_videos():
        if d.filename == source_file:
            file_exists = True
            
    if file_exists == False:
        return "File not found [%s]" % source_file, False 

    access_token = '4wPGw33d4lcAAAAAAAAAfVl873ag8OxmIoay_NNAGqol8rtv8QH3oPEADSSHiLhf'
    dropbox_client = dropbox.client.DropboxClient(access_token)

    source_file_path = video_folder + source_file
    dest_file_path = '/' + source_file
    
    try:
        f = open(source_file_path, 'rb')
        resp = dropbox_client.put_file(dest_file_path, f)
        update_backup_register(source_file)
        return "Uploaded %s to dropbox path %s (%d bytes)" % (source_file_path, dest_file_path, resp['bytes']), True
    except Exception as err:
        return "Failed to upload %s [%s]" % (source_file_path, err), False

def update_backup_register(backup_filename):
    # Identify videos stored locally on webserver; store in a dictionary
    videos_stored_locally = {}
    for d in get_videos():
        videos_stored_locally[d.filename] = d
        
    # Identify files previously backed up that are still resident on the web server
    register = get_backup_register()
    for old_backup_filename in register:
        d = videos_stored_locally.get(old_backup_filename)
        if d != None:
            d.backedup = True
    
    # Mark this latest file as backed up        
    d = videos_stored_locally.get(backup_filename)
    d.backedup = True 
    
    # Re-write the register file
    with open(webroot + backup_register, 'w') as f:
        for key in videos_stored_locally:
            d = videos_stored_locally.get(key)
            if d.backedup:
                f.write(d.filename + "\n")
                
def get_backup_register():
    entries = {}
    with open(webroot + backup_register, 'r') as f:
        for filename in f:
            entries[filename.strip()] = 1
    
    return entries

class Document:
    def __init__(self, filename):
        self.event = None
        self.filename = filename
        self.path = video_folder + filename
        self.backedup = False
        m = re.search("(\d{2})-(\d{14})\.%s" % videotype, filename)
        if m:
            self.timestamp = m.group(2)
            self.date = datetime.strptime(self.timestamp, '%Y%m%d%H%M%S')
            self.event = m.group(1)
            self.size = self.get_file_size()
            
    def get_file_size(self):
        l = os.stat(self.path).st_size
        thousand = 1000
        million = 1000000
        
        if l < thousand:
            return "%d bytes" % l
        elif l < million:
            return "%d Kb" % (l/thousand)
        else:
            return "%d Mb" % (l/million)
    
def head(req): 
    s = """<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>  
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Webcam Access</title>
  </head> 
  <body>
    """
    return s


def tail(req):
    s = """
  </body> 
</html>
    """
    return s