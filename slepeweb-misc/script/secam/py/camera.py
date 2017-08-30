import constants, logging
import picamera, io, socket, time

class Camera:
    def __init__(self):
        self.const = constants.Constants()
        self.status = self.const.stop
        self.brightness = "70"
        self.contrast = "70"
        self.exposure_mode = "auto"
        self.iso = "0"
        self.recording = False
        self.live_viewing = False
        self.logger = logging.getLogger("secam")
        
    def record_video(self, file_path, duration):
        if not self.recording:
            self.recording = True
            
            with picamera.PiCamera() as camera:
                self._prepare(camera)
                camera.start_recording(file_path, quality=23)
                camera.wait_recording(duration)
                camera.stop_recording() 
                self._complete(camera)
                #LOG.info("Video recording completed")
                
            self.recording = False
        else:
            self.logger.warning("Camera is already recording")
               
        return file_path

    def start_live_video_0(self, duration):
        if not self.live_viewing:
            self.live_viewing = True
            
            try:
                # Create new socket, and connect to the server
                sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                sock.connect((self.const.host, self.const.live_video_port))
                
                # Make a file-like object out of the connection
                connection = sock.makefile('wb')
                
                with picamera.PiCamera() as camera:
                    self._prepare(camera)
                    camera.start_recording(connection, format="mjpeg", quality=23)
                    camera.wait_recording(duration)
                    camera.stop_recording() 
                    self._complete(camera)
                
            finally:
                try:
                    connection.close()
                    sock.close()
                except:
                    self.logger.warning("Failed to close the connection or socket")
                    
            self.live_viewing = False
        else:
            self.logger.warning("Camera is already live_viewing")

    def start_live_video(self, duration):
        if not self.live_viewing:
            self.live_viewing = True
            
            try:
                # Create new socket, and connect to the server
                sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                sock.connect((self.const.host, self.const.live_video_port))                
                stream=io.BytesIO()
                
                with picamera.PiCamera() as camera:
                    self._prepare(camera)
                    for foo in camera.capture_continuous(stream, "jpeg"):
                        sock.send(stream.getvalue())
                        stream.seek(0)
                        stream.truncate()
                        time.sleep(0.2);
                        
                    self._complete(camera)
                
            finally:
                try:
                    sock.close()
                except:
                    self.logger.warning("Failed to close the connection or socket")
                    
            self.live_viewing = False
        else:
            self.logger.warning("Camera is already live_viewing")

    def capture_photo(self, file_path):
        with picamera.PiCamera() as camera:
            self._prepare(camera)
            camera.capture(file_path)
            self._complete(camera)
            self.logger.info("Photo taken")
               
        return file_path

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
