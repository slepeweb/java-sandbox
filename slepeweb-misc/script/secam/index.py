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
    msg = None
    s = None
    if d:
        s, ok = delete_file(d)
        msg = "<h2>%s</h2>" % s
    elif b:
        s, ok = secam.backup_file(b)
        msg = "<h2>%s</h2>" % s
        
    h = secam.head(req) 
    t = secam.tail(req)
    
    if d or b:
        rtn = """<a href="%sindex.py">Return to index</a>""" % secam.app
        return " ".join([h, msg, rtn, t])

    a = secam.get_videos()
    if len(a) == 0:
        return " ".join([h, "<h2>No media items found</h2>", t])
    
    b_start = """<table border="1"><tr>
            <th>Event id</th>
            <th>Time</th>
            <th>Video</th>
            <th>Size</th>
            <th>Delete?</th>
            <th>Backup?</th>
        </tr>"""
        
    a = sorted(a, key=attrgetter("timestamp"))
    rows = []

    for d in a: 
        url = secam.app + secam.video_subfolder + d.filename
        row = """<tr><td>%s</td><td>%s</td><td><a href="%s">%s</a></td><td>%s</td>""" % (d.event, str(d.date), url, "View", d.size)
        row += """<td><a href="%sindex.py?d=%s">Delete</a></td>""" % (secam.app, d.filename) 
        
        s = "Done" if d.backedup else """<a href="%sindex.py?b=%s">Backup</a>""" % (secam.app, d.filename)
        row += """<td>%s</td>""" % s 
        row += "</tr>"
        rows.append(row)

    b_end = "</table></body>"

    return " ".join([h, b_start, ' '.join(rows), b_end, t])
