#!/usr/bin/python
#
import secam, secamctrl, time, datetime, os, subprocess, smtplib
import logging
import RPi.GPIO as GPIO, picamera
from email.mime.text import MIMEText
from thread import *


# TODO: harden code - need to check CAMERA is set ???
#       or, are we sure camera will never be invoked when RUN_STATUS == 'stop'?

USER = "georgeb"
PWD = "giga8yte"
PIR = 4
EVENT_COUNTER = 0
GPIO.setmode(GPIO.BCM)
GPIO.setup(PIR, GPIO.IN, GPIO.PUD_DOWN)
CAMERA = None
FNULL = open(os.devnull, 'w')

MAIL_FROM = "george@slepeweb.com"
MAIL_TO = "george@buttigieg.org.uk"
MAIL_SUBJECT = "Security Alarm"
WEB_PAGE = "http://www.slepeweb.com/secam/app/index.py"
MAIL_BODY = """
A security alarm (#%d) has been raised @ %s.

Please investigate further @ %s
"""

RUN_STATUS = secamctrl.GO
logging.info("====================================================================")

logging.basicConfig(filename="/home/pi/spibox.log", format="%(asctime)s (%(filename)s) [%(levelname)s] %(message)s", level=logging.DEBUG)
logging.getLogger("requests").setLevel(logging.WARNING)
logging.getLogger("urllib3").setLevel(logging.WARNING)
logging.info("Spibox application started")
    
def get_filename_prefix(event_id, time_mark):
    return "%d-%s" % (event_id, time_mark.strftime("%Y%m%d%H%M%S"))

# def photo(CAMERA, event_id):
#     print('%d) Motion detected! ...' % event_id)
#     for i in range(1,3):
#         capturename = ''.join(["/home/pi/spibox/capture/", get_filename_prefix(event_id), "-", str(event_id), "-", str(i), ".jpg"])
#         print('... ' + capturename)
#         CAMERA.capture(capturename)
#         time.sleep(0.5)

def record_video(event_id, time_mark):
    global CAMERA
    logging.info("%d) Motion detected! ..." % event_id)
    h264_path = ''.join([secam.video_folder, get_filename_prefix(event_id, time_mark), ".h264"])
    out(event_id, "recording to [%s]" % h264_path)
    CAMERA.start_recording(h264_path, quality=23)
    CAMERA.wait_recording(15)
    CAMERA.stop_recording()    
    return h264_path

def convert2mp4(event_id, h264_path):
    mp4_path = h264_path.replace("h264", "mp4")
    out(event_id, "converting to [%s]" % mp4_path)
    try:
        subprocess.check_call(["/usr/bin/MP4Box", "-add", h264_path, mp4_path], stdout=FNULL, stderr=FNULL)
        out(event_id, "video conversion complete")
        os.remove(h264_path)
        out(event_id, "deleted h264 file")
    except subprocess.CalledProcessError as e:
        out(event_id, "*** error converting record_video file [%s]" % e)
    
    return mp4_path

def send_mail(event_id, time_mark):
    event_time = time_mark.strftime("%Y/%m/%d %H:%M:%S")
    msg = MIMEText(MAIL_BODY % (event_id, event_time, WEB_PAGE))
    msg['Subject'] = MAIL_SUBJECT
    msg['From'] = MAIL_FROM
    msg['To'] = MAIL_TO
    server = None
    
    try:
        out(event_id, "sending mail")
        server = smtplib.SMTP("smtp.gmail.com", 587)
        server.starttls()
        server.login("george.buttigieg@gmail.com", "br1cktop1")
        server.sendmail(MAIL_FROM, [MAIL_TO], msg.as_string())
        out(event_id, "mail sent ok")
    except:
        out(event_id, "*** problem sending mail")
        
    finally:
        if server != None:
            server.quit() 

def take_photo(time_mark):
    global CAMERA
    file_path = ''.join([secam.video_folder, get_filename_prefix(-1, time_mark), ".jpg"])
    CAMERA.capture(file_path)    

def check_action_messages():
    global CAMERA, CTRL, RUN_STATUS
    msg = CTRL.get_message()
    not_recognized = "Message not recognized [%s]" % msg
    error = False
    
    if msg:
        if len(msg) > 1:
            parts = msg.split(",")
            if len(parts) != 2:
                error = True
            else:
                action = parts[1]
                
                if action == "photo":
                    if RUN_STATUS == secamctrl.GO and CAMERA:
                        time_mark = datetime.datetime.now()
                        take_photo(time_mark)
                        logging.info("Photo-taken; confirmation sent")
                    else:
                        logging.warn("Camera is paused - cannot take snapshot")
                elif action == secamctrl.STOP:
                    if RUN_STATUS == secamctrl.GO:
                        RUN_STATUS = action
                        CTRL.set_status(action)
                        CAMERA.close()
                        logging.info("STOP message received")
                    else:
                        logging.warn("Current status is already STOP")
                elif action == secamctrl.GO:
                    if RUN_STATUS == secamctrl.STOP:
                        RUN_STATUS = action
                        CTRL.set_status(action)
                        CAMERA = initialise_camera()
                        logging.info("GO message received")
                    else:
                        logging.warn("Current status is already GO")
                else:
                    error = True
    
                CTRL.dequeue_message(msg)
        else:
            error = True     
    
        if error:
            logging.info(not_recognized)     
                
    
def initialise_pir():
    start_time = time.time()
    
    while GPIO.input(PIR) == GPIO.HIGH:
        time.sleep(0.1)
        
    stop_time = time.time()
    logging.info("Sensor reset took: %0.1f secs" % (stop_time - start_time))
    
def initialise_camera():
    try:
        c = picamera.PiCamera()
        c.resolution = (1280, 720)
        c.vflip = True
        # c.iso = 100 for daytime, 4-800 for low light (sensitivity)
        #
        # For daylight conditions:
        # CAMERA.shutter_speed = CAMERA.exposure_speed
        # CAMERA.exposure_mode = 'off'
        # g = CAMERA.awb_gains
        # CAMERA.awb_mode = 'off'
        # CAMERA.awb_gains = g
        #
        # For low-light conditions:
        # CAMERA.framerate = Fraction(1, 6)
        # CAMERA.shutter_speed = 6000000
        # CAMERA.exposure_mode = 'off'
        # CAMERA.iso = 800
    
        c.start_preview()
        logging.info("Camera initialised")
        return c
    except:
        logging.info("Failed to initialise the camera")
        return None

def send_mail_and_backup_video(event_id, time_mark, h264_path):
    send_mail(event_id, time_mark)    
    mp4_path = convert2mp4(event_id, h264_path)    
    msg, ok = secam.backup_file(file_part(mp4_path))
    out(event_id, msg)

# def spawn_remaining_tasks(event_id, time_mark, h264_path):
#     task = threading.Thread(target=send_mail_and_backup_video, args=[event_id, time_mark, h264_path])
#     task.start()        


def spawn_secam_controller(ctrl):
    ctrl.start()        

def file_part(file_path):
    return file_path.split("/")[-1]

def out(event_id, s):
    logging.info("%d) ... %s" % (event_id, s))
    
    
CTRL = secamctrl.SecamController()
start_new_thread(spawn_secam_controller, (CTRL,))     

initialise_pir()
CAMERA = initialise_camera()
TIMER = 0
SMALL_SLEEP = 0.2
BIG_SLEEP = 2

pin_start_high = 0
pin_end_high = 0

try:    
    while True:
        if RUN_STATUS == secamctrl.GO:
            if GPIO.input(PIR) == GPIO.HIGH and CAMERA:        
                time_mark = datetime.datetime.now()
                if not pin_start_high:
                    pin_start_high = time_mark
                #logging.info("GPIO pin is HIGH @ %s" % time_mark.strftime("%Y/%m/%d %H:%M:%S"))
                #h264_path = record_video(EVENT_COUNTER, time_mark)
                # spawn remaining tasks
                #start_new_thread(send_mail_and_backup_video, (EVENT_COUNTER, time_mark, h264_path))
                EVENT_COUNTER += 1
            else:
                pin_end_high = datetime.datetime.now()
                
        if pin_start_high and pin_end_high:
            logging.info("GPIO went HIGH @ %s, and went LOW %d secs later" % (pin_start_high.strftime("%Y/%m/%d %H:%M:%S"), (pin_end_high - pin_start_high) / 1000))
            pin_start_high = 0
            pin_end_high = 0
           
        if TIMER >= BIG_SLEEP:
            TIMER = 0
            check_action_messages()
            
        time.sleep(SMALL_SLEEP)
        TIMER += SMALL_SLEEP

except KeyboardInterrupt:
    logging.info("Secam application finished")
    GPIO.cleanup()

finally:
    if CAMERA != None:
        CAMERA.close()