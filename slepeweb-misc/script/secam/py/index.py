import messaging, constants, document, controls, socket
from operator import attrgetter

_const = constants.Constants()

def reboot(req, pwd=""):
    req.content_type="Content-Type: text/plain"
    return send_message("reboot", {"pwd": pwd}, True)            
    
def send_message(action, argsObject, return_json=False):
    ctrl = messaging.SecamControllerClient()
    return ctrl.send_message({"action": action, "args": argsObject}, return_json)
    
def putm(req, msg, json=False):
    req.content_type="Content-Type: text/plain"
    return send_message(msg, {}, json)

def status(req):
    req.content_type="Content-Type: text/plain"
    return send_message("status", {}, True)
        
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
    # results is an array of objects. Each object has keys 'filename' and 'backedup'
    results = send_message("get_file_register", {})
    if len(results) == 0:
        return "<h2>No media items found</h2>"
    
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
        d = document.Document(obj['filename'], _const)
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
    return " ".join([b_start, ' '.join(rows), b_end])


def index(req):
    return " ".join([head(req), controls.controls(req), tail(req)])


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
        <h1><a href="index.py">Video index</a></h1>    
        <div id="video-table"><p>(Video table is loading ...)</p></div>
    """
    return s


def tail(req):    
    s = """
        </div>
    </div>
  </body> 
</html>
    """
    return s


def live_video(req):
    req.content_type='Content-Type: multipart/x-mixed-replace;boundary="--jpgboundary"'
    #req.content_type="Content-Type: video/x-motion-jpeg"
    
    #req.headers_out["Connection"] = "keep-alive"
    
    try:
        sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        sock.connect((_const.host, _const.live_video_port))
        
        while True:
            chunk = sock.recv(2048)
            if not chunk:
                break
            
            req.write("--jpgboundary")
            req.write("Content-Type: image/jpeg")
            req.write("Content-Length: %d" % len(chunk))
            req.write(chunk)
        
    finally:
        try:
            sock.close()
        except:
            ''
