class Constants:
    def __init__(self):
        self.pir_pin = 4
        self.videotype = "mp4"
        self.imagetype = "jpg"
        
        self.webroot = "/var/www/html/"
        self.video_folder = self.webroot + "video/"
        self.app_folder_web = "/"
        self.video_folder_web = self.app_folder_web + "video/"
        self.backup_register = "resource/backup-register"
        
        self.go = "go"
        self.stop = "stop"
        self.photo = "photo"
        self.video = "video"
        self.live_video = "livevideo"
       
        self.settings = "settings"
        self.brightness = "brightness"
        self.contrast = "contrast"
        self.iso = "iso"
        self.exposure_mode = "mode"
        
        self.msg = "msg"
        self.stat = "status"
        self.host = ''   # Symbolic name, meaning all available interfaces
        self.port = 8888 # Arbitrary non-privileged port    
