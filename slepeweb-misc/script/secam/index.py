import os, secam 
from operator import attrgetter

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
