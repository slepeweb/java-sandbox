import logging, secam, os
from operator import attrgetter
from datetime import datetime

LOG = logging.getLogger("secam")
logging.getLogger("requests").setLevel(logging.WARNING)
LOG.info("Index page loaded")
_const = secam.Constants()

def reboot(req, pwd=""):
    cf = datetime.now().strftime("%H%d%m%Y")
    if pwd == cf:            
        LOG.info("Reboot requested")
        os.system("sudo shutdown -r now")
                    
    else:
        LOG.error("*** Bad password provided for reboot [%s]" % pwd)
            
    
def send_message(action, argsObject, return_json=False):
    ctrl = secam.SecamControllerClient()
    return ctrl.send_message({"action": action, "args": argsObject}, return_json)
    
def putm(req, msg, json=False):
    req.content_type="Content-Type: text/plain"
    return send_message(msg, {}, json)
        
def camera(req, ctrl, value):
    req.content_type="Content-Type: text/plain"
    return send_message("camera", {"ctrl": ctrl, "value": value}, True)
        
def delete(req, files=""):
    req.content_type="Content-Type: text/plain"
    return send_message("delete", {"files": files.split(",")}, True)
    
def backup(req, plik=""):
    req.content_type="Content-Type: text/plain"
    return send_message("backup", {"plik": plik}, True)
    
def table(req):
    h1 = """<h1>Video index</h1>"""
    
    # results is an array of objects. Each object has keys 'filename' and 'backedup'
    results = send_message("get_file_register", {})
    if len(results) == 0:
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
    
    # Convert list of objects into list of Document objects
    docs = []
    for obj in results:
        d = secam.Document(obj['filename'], _const)
        d.backedup = obj['backedup']
        docs.append(d)
        
    # Now sort the documents into date order
    results = sorted(docs, key=attrgetter("timestamp"))    
    rows = []

    for d in results:         
        row = """<tr><td>%s</td><td>%s</td><td>%s</td>""" % (d.event, d.get_date().strftime("%d/%m/%y"), d.get_date().strftime("%H:%M:%S"))
        url = _const.video_folder_web + d.filename
        row += """<td><a href="%s" class="iframe group2">%s</a></td><td>%s</td>""" % (url, "View", d.size)
        row += """<td><input class="deleteable-video" type="checkbox" value="%s" /></td>""" % d.filename
        row += "<td>Done</td>" if d.backedup else """<td><button class="backup-button" value="%s">Backup</button></td>""" % d.filename 
        row += "</tr>"
        rows.append(row)

    b_end = "</table>"
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
    <link rel="stylesheet" href="/secam/app/resource/colorbox.css" />
    <script src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
    <script src="http://code.jquery.com/ui/1.10.3/jquery-ui.min.js"></script>
    <script src="/secam/app/resource/jquery.colorbox-min.js"></script>
    <script src="/secam/app/resource/secam.js"></script>
  </head> 
  <body>
      <div id="main">
    """
    return s


def tail(req):    
    s = """
        </div>
        <table><tr>
            <td><a href="/secam/app/log/secam.log">Show log</a></td>
            <td><button id="button-photo" value="photo">Take photo</button></td>
            <td><button id="button-stopgo" value="stop">Pause surveillance</button></td></td>
            <td><button id="button-refresh">Refresh</button></td>
            <td class="flash"></td>
        </tr></table>
        
        <table id="controls">
            <tr><td>Brightness</td><td><select class="ctrl" id="brightness">%s</select></td></tr>
            <tr><td>Contrast</td><td><select class="ctrl" id="contrast">%s</select></td></tr>
            <tr><td>Mode</td><td><select class="ctrl" id="mode">%s</select></td></tr>
            <tr><td>ISO</td><td><select class="ctrl" id="iso">%s</select></td></tr>
        </table>
        
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
    """ % (int_options_stepped(10, 100, 5), int_options_stepped(-80, 100, 5), 
           str_options_arr(["auto", "night", "nightpreview", "backlight", "spotlight"]), 
           int_options_arr([0, 100, 200, 400, 800]))
    return s

def int_options_stepped(start, end, step):
    s = ""
    for i in range(start, end, step):
        s += """<option value="%d">%d</option>""" % (i, i)
    return s

def str_options_arr(lis):
    s = ""
    for ss in lis:
        s += """<option value="%s">%s</option>""" % (ss, ss)
    return s

def int_options_arr(lis):
    s = ""
    for i in lis:
        s += """<option value="%d">%d</option>""" % (i, i)
    return s