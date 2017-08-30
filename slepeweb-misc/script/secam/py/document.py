import re, os
from datetime import datetime


class Document:
    def __init__(self, filename, const):
        self.event = None
        self.filename = filename
        self.path = const.video_folder + filename
        self.backedup = False
        self.timestamp = "20000101235959" # a default/arbitrary date
        self.size = None
        m = re.search("(P|\d{1,})-(\d{14})\.(%s|%s)" % (const.videotype, const.imagetype), filename)

        if m:
            self.timestamp = m.group(2)
            self.event = m.group(1)
            self.size = self._get_file_size()
            
    def _get_file_size(self):
        l = os.stat(self.path).st_size
        thousand = 1000
        million = 1000000
        
        if l < thousand:
            return "%d bytes" % l
        elif l < million:
            return "%d Kb" % (l/thousand)
        else:
            return "%d Mb" % (l/million)
    
    def get_date(self):
        return datetime.strptime(self.timestamp, '%Y%m%d%H%M%S') 
    
    def to_obj(self):
        obj = {}
        obj['event'] = self.event
        obj['filename'] = self.filename
        obj['path'] = self.path
        obj['backedup'] = self.backedup
        obj['timestamp'] = self.timestamp
        obj['size'] = self.size
        return obj        
        