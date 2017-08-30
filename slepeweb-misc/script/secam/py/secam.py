import logging, logging.handlers, camera, messaging, spibox
from thread import *
       

def spawn_messaging_service(ctrl):
    ctrl.start()        

    
LOG = logging.getLogger("secam")
        
if __name__ == "__main__":
    LOG.setLevel(logging.INFO)    
    fh = logging.handlers.RotatingFileHandler("/var/www/html/log/secam.log", maxBytes=128000, backupCount=5)
    fh.setFormatter(logging.Formatter("%(asctime)s (%(filename)s) [%(levelname)s] %(message)s"))
    LOG.addHandler(fh)

    LOG.info("====================================================================")
    LOG.info("Logger initialised")

    CAMERA = camera.Camera()    
    CTRL = messaging.SecamController(CAMERA)    
    start_new_thread(spawn_messaging_service, (CTRL,))     
    spibox.Spibox(CTRL)._service()
