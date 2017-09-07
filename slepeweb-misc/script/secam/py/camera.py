import picamera, time
from datetime import datetime
import constants, logging

class Camera:
    def __init__(self):
        self.const = constants.Constants()
        self.status = self.const.stop
        self.brightness = "70"
        self.contrast = "70"
        self.exposure_mode = "auto"
        self.iso = "0"
        self.recording = False
        self.playing_live_video = False
        self.logger = logging.getLogger("secam")
        
    def is_busy(self):
        return self.recording or self.playing_live_video
    
    def record_video(self, file_path, duration):
        if not self.is_busy():
            self.recording = True
            
            with picamera.PiCamera() as camera:
                self._prepare(camera)
                camera.start_recording(file_path, quality=23)
                camera.wait_recording(duration)
                camera.stop_recording() 
                self._complete(camera)
                #LOG.info("Video recording completed")
                
            self.recording = False
            return True
        else:
            return False

    def capture_photo(self, file_path):
        if not self.is_busy():
            self.recording = True
            
            with picamera.PiCamera() as camera:
                self._prepare(camera)
                camera.capture(file_path)
                self._complete(camera)
               
            self.recording = False
            return True;
        else:
            return False

    def _prepare(self, camera):
        camera.resolution = (1280, 720)
        camera.vflip = True
        camera.brightness = int(self.brightness)
        camera.contrast = int(self.contrast)
        camera.exposure_mode = self.exposure_mode
        camera.iso = int(self.iso)
        camera.start_preview()

    def _complete(self, camera):
        camera.stop_preview()
        camera.close()

    def set_status(self, s):
        if s in [self.const.stop, self.const.go]:
            self.status = s
        
    def get_status(self):
        response = {}
        response[self.const.settings] = self.get_settings()
        response[self.const.msg] = "Surveillance is " + ("on" if self.status == self.const.go else "paused")
        response[self.const.stat] = self.status
        return response
    
    def set_setting(self, ctrl, value):
        if ctrl == self.const.brightness:
            self.brightness = value
            return "Brightness set to '%s'" % value
        elif ctrl == self.const.contrast:
            self.contrast = value
            return "Contrast set to '%s'" % value
        elif ctrl == self.const.exposure_mode:
            self.exposure_mode = value
            return "Exposure mode set to '%s'" % value
        elif ctrl == self.const.iso:
            self.iso = value
            return "ISO set to '%s'" % value
        
    
    def get_settings(self):
        return { 
                self.const.brightness: self.brightness,
                self.const.contrast: self.contrast,
                self.const.exposure_mode: self.exposure_mode,
                self.const.iso: self.iso}
