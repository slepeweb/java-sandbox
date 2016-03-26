import os, secam, logging
from operator import attrgetter
from secamctrl import SecamControllerClient

logging.basicConfig(filename="/var/www/html/log/index.log", format="%(asctime)s (%(filename)s) [%(levelname)s] %(message)s", level=logging.INFO)
logging.getLogger("requests").setLevel(logging.WARNING)
logging.info("Index page loaded")

MESSAGE_BODY = """
<h1>Messaging</h1>
<p>Message received: '%s'</p>
<a href="%s">Return to index</a>
"""

CTRL = SecamControllerClient()

def send_message(msg):
    global CTRL
    
    if CTRL:
        return CTRL.send_message(msg)
    
    return 0
    
def alert(msg):
    if msg == -1:  
        return MESSAGE_BODY % ("Messaging server is not available", secam.index_page_path);  
    else:
        return MESSAGE_BODY % (msg, secam.index_page_path);

     
def delete_file(file_list):
    count = 0
    files = file_list.split(",")
    for d in secam.get_videos():
        for filename in files:
            if d.filename == filename:
                try:
                    os.remove(secam.video_folder + filename)
                    count += 1
                except:
                    return "Failed to delete file [%s]" % filename, False
            
    return "Deleted %d file(s)" % count, count == len(files)
    
def putm(req, msg):
    req.content_type="Content-Type: text/plain"
    return msg if send_message("q|" + msg) else "FAILED"
    
def status(req):
    req.content_type="Content-Type: text/plain"
    return send_message("status")
    
def delete(req, files=""):
    req.content_type="Content-Type: text/plain"
    s, ok = delete_file(files)
    logging.info(s)
    return s
    
def backup(req, plik=""):
    req.content_type="Content-Type: text/plain"
    s, ok = secam.backup_file(plik)
    logging.info(s)
    return s
    
def get_q_status():
    resp = send_message("getq")
    if resp:
        a = resp.split("|")
        return "Message queue has %d entries %s" % (len(a), resp)
    return 

def table(req):
    h1 = """<div id="main"><h1>Video index</h1>"""
    a = secam.get_videos()
    if len(a) == 0:
        return " ".join([h1, "<h2>No media items found</h2>"])
    
    b_start = """<table id="video-index-table"><tr>
            <th>Event id</th>
            <th>Date</th>
            <th>Time</th>
            <th>Video</th>
            <th>Size</th>
            <th><span class="del-check">Delete?</span></th>
            <th>Backup?</th>
        </tr>"""
        
    a = sorted(a, key=attrgetter("timestamp"))
    rows = []

    for d in a: 
        row = """<tr><td>%s</td><td>%s</td><td>%s</td>""" % (d.event, d.date.strftime("%d/%m/%y"), d.date.strftime("%H:%M:%S"))
        url = secam.app + secam.video_subfolder + d.filename
        row += """<td><a href="%s">%s</a></td><td>%s</td>""" % (url, "View", d.size)
        row += """<td><input class="deleteable-video" type="checkbox" value="%s" /></td>""" % d.filename 
        row += "<td>Done</td>" if d.backedup else """<td><button class="backup-button" value="%s">Backup</button></td>""" % d.filename 
        row += "</tr>"
        rows.append(row)

    b_end = "</table></div>"
    return " ".join([h1, b_start, ' '.join(rows), b_end])


def index(req):
    h = head(req) 
    t = tail(req)
    b = table(req)
    return " ".join([h, b, t])


def head(req): 
    s = """<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>  
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Security camera application</title>
    <link rel="stylesheet" href="http://code.jquery.com/ui/1.9.2/themes/base/jquery-ui.css" type="text/css">
    <link rel="stylesheet" href="/secam/app/resource/style.css" />
    <script src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
    <script src="http://code.jquery.com/ui/1.10.3/jquery-ui.min.js"></script>
    <script src="/secam/app/resource/secam.js"></script>
  </head> 
  <body>
    """
    return s


def tail(req):    
    s = """
        <table><tr>
            <td><button id="button-photo" value="photo">Take photo</button></td>
            <td><button id="button-stopgo" value="stop">Pause surveillance</button></td></td>
            <td><button id="button-refresh">Refresh</button></td>
            <td class="flash"></td>
        </tr></table>
        <p></p>
        <div id="dialog-trash-confirm" class="hide" title="Delete file?">
            <p>
                <span class="ui-icon ui-icon-alert"></span>
                Are you sure you want to delete <span id="num-files-target"></span> file(s)?
            </p>
        </div>
        <div id="bop"></div>
  </body> 
</html>
    """
    return s