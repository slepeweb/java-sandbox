import re, os, dropbox
from datetime import datetime

# Constants
videotype = "mp4"
imagetype = "jpg"
ctx = "/secam/"
app = ctx + "app/"
index_page_path = app + "index.py"
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
            
    if not file_exists:
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
        d = videos_stored_locally.get_message(old_backup_filename)
        if d != None:
            d.backedup = True
    
    # Mark this latest file as backed up        
    d = videos_stored_locally.get_message(backup_filename)
    d.backedup = True 
    
    # Re-write the register file
    with open(webroot + backup_register, 'w') as f:
        for key in videos_stored_locally:
            d = videos_stored_locally.get_message(key)
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
        m = re.search("(-?\d{1,})-(\d{14})\.[%s|%s]" % (videotype, imagetype), filename)
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
        
