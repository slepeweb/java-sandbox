import os, secam 
from operator import attrgetter

def delete_file(filename):
    for d in secam.get_videos():
        if d.filename == filename:
            try:
                os.remove(secam.video_folder + filename)
                return "File successfully deleted [%s]" % filename, True
            except:
                return "Failed to delete file [%s]" % filename, False
            
    return "File not found [%s]" % filename, False
    
# param d: name of file for deletion
# param b: name of fo;e for backup to dropbox
def index(req, d="", b=""):
    msg = s = heading = None
    ok = True
    
    if d:
        heading = "File deletion"
        s, ok = delete_file(d)
    elif b:
        heading = "File backup"
        s, ok = secam.backup_file(b)
        
    h = secam.head(req) 
    t = secam.tail(req)
    
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
            <th>Delete?</th>
            <th>Backup?</th>
        </tr>"""
        
    a = sorted(a, key=attrgetter("timestamp"))
    rows = []

    for d in a: 
        row = """<tr><td>%s</td><td>%s</td><td>%s</td>""" % (d.event, d.date.strftime("%d/%m/%y"), d.date.strftime("%H:%M:%S"))
        url = secam.app + secam.video_subfolder + d.filename
        row += """<td><a href="%s">%s</a></td><td>%s</td>""" % (url, "View", d.size)
        row += """<td><span class="del-check" data-f="%s">Delete</span></td>""" % d.filename 
        
        s = "Done" if d.backedup else """<a href="%sindex.py?b=%s">Backup</a>""" % (secam.app, d.filename)
        row += """<td>%s</td>""" % s 
        row += "</tr>"
        rows.append(row)

    b_end = "</table></body>"

    return " ".join([h, h1, b_start, ' '.join(rows), b_end, t])
