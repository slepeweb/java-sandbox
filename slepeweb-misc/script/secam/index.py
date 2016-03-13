import os, secam, logging
from operator import attrgetter
from secamctrl import SecamControllerClient

logging.basicConfig(filename="/home/pi/index.log", format="%(asctime)s (%(filename)s) [%(levelname)s] %(message)s", level=logging.INFO)
logging.getLogger("requests").setLevel(logging.WARNING)
logging.info("Index page loaded")

MESSAGE_BODY = """
<h1>Messaging</h1>
<p>Message received: '%s'</p>
<a href="%s">Return to index</a>
"""

CTRL = SecamControllerClient()

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
    CTRL.send_message("q|" + msg)
    h = head(req) 
    t = tail(req)
    b = MESSAGE_BODY % (msg, secam.index_page_path)
    return " ".join([h, b, t])
    
def clrm(req):
    s = "Message queue cleared"
    logging.info(s)
    CTRL.send_message("clrq")
    h = head(req) 
    t = tail(req)
    b = MESSAGE_BODY % (s, secam.index_page_path)
    return " ".join([h, b, t])
    
def get_q_status():
    q = CTRL.send_message("getq")
    a = q.split("|")
    return "Message queue has %d entries %s" % (len(a), q)

# param d: name of file for deletion
# param b: name of file for backup to dropbox
def index(req, d="", b=""):
    msg = s = heading = None
    ok = True
    
    if d:
        # File deltion request
        heading = "File deletion"
        s, ok = delete_file(d)
    elif b:
        # File backup request
        heading = "File backup"
        s, ok = secam.backup_file(b)
#     elif mp:
#         secam.MESSAGE_STACK.append(mp)
        
    h = head(req) 
    t = tail(req)
    
    if d or b:
        h1 = "<h1>%s</h1>" % heading
        msg = """<h2 class="%s">%s</h2>""" % ("green" if ok else "red", s)
        rtn = """<a href="%sindex.py">Return to index</a>""" % secam.app
        return " ".join([h, h1, msg, rtn, t])

    h1 = "<h1>Video index</h1>"
    a = secam.get_videos()
    if len(a) == 0:
        return " ".join([h, h1, "<h2>No media items found</h2>", t])
    
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
        
        s = "Done" if d.backedup else """<a href="%sindex.py?b=%s">Backup</a>""" % (secam.app, d.filename)
        row += """<td>%s</td>""" % s 
        row += "</tr>"
        rows.append(row)

    b_end = "</table></body>"

    return " ".join([h, h1, b_start, ' '.join(rows), b_end, t])

def head(req): 
    s = """<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>  
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Security camera application</title>
    <link rel="stylesheet" href="http://code.jquery.com/ui/1.9.2/themes/base/jquery-ui.css" type="text/css">
    <link rel="stylesheet" href="/secam/app/style.css" />
    <script src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
    <script src="http://code.jquery.com/ui/1.10.3/jquery-ui.min.js"></script>
    <script src="/secam/app/secam.js"></script>
  </head> 
  <body>
    """
    return s


def tail(req):
    stop_start_param = "stop"
    stop_start_label = "Stop camera"
    snapshot_link = """<a href="%s/putm?msg=photo">Take snaphshot</a>""" % secam.index_page_path
    
    status = CTRL.send_message("status")
    if status == "stop":
        stop_start_param = "go"
        stop_start_label = "Re-start camera"
        snapshot_link = "(Take snaphshot)"
    
    s = """
        <table><tr>
            <td>%s</td>
            <td><a href="%s/clrm">Clear messages</a></td>
            <td><a href="%s/putm?msg=%s">%s</a></td>
        </tr></table>
        <p></p>
        <div id="dialog-trash-confirm" class="hide" title="Delete file?">
            <p>
                <span class="ui-icon ui-icon-alert"></span>
                Are you sure you want to delete <span id="num-files-target"></span> file(s)?
            </p>
        </div>
  </body> 
</html>
    """ % (snapshot_link, secam.index_page_path, secam.index_page_path, stop_start_param, stop_start_label)
    return s