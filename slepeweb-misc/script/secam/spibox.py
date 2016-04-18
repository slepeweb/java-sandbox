#!/usr/bin/python
#
import secam, secamctrl, time, datetime, os, subprocess, smtplib
import logging
import RPi.GPIO as GPIO
from email.mime.text import MIMEText
from thread import *

USER = "georgeb"
PWD = "giga8yte"
FNULL = open(os.devnull, 'w')

logging.info("====================================================================")

logging.basicConfig(filename="/var/www/html/log/secam.log", format="%(asctime)s (%(filename)s) [%(levelname)s] %(message)s", level=logging.DEBUG)
logging.info("Spibox application started")
    
def get_filename_prefix(event_id, time_mark):
    return "%s-%s" % (event_id, time_mark.strftime("%Y%m%d%H%M%S"))

def record_video(camera, event_id, time_mark):
    logging.info("%d) Motion detected! ..." % event_id)
    h264_path = ''.join([secam.video_folder, get_filename_prefix(event_id, time_mark), ".h264"])
    out(event_id, "recording to [%s]" % h264_path)
    # PIR stays high for 8 secs, then low for 8 secs; cycle is 16 secs
    camera.record_video(h264_path, 16)
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
    mail_from = "george@slepeweb.com"
    mail_to = "george@buttigieg.org.uk"
    web_page = "http://www.slepeweb.com/secam/app/py/index.py"
    mail_body = """
    A security alarm (#%d) has been raised @ %s.
    
    Please investigate further @ %s
    """

    event_time = time_mark.strftime("%Y/%m/%d %H:%M:%S")
    msg = MIMEText(mail_body % (event_id, event_time, web_page))
    msg['Subject'] = "Security Alarm"
    msg['From'] = mail_from
    msg['To'] = mail_to
    server = None
    
    try:
        out(event_id, "sending mail")
        server = smtplib.SMTP("smtp.gmail.com", 587)
        server.starttls()
        server.login("george.buttigieg@gmail.com", "br1cktop1")
        server.sendmail(mail_from, [mail_to], msg.as_string())
        out(event_id, "mail sent ok")
    except:
        out(event_id, "*** problem sending mail")
        
    finally:
        if server != None:
            server.quit() 

def take_photo(camera, time_mark):
    file_path = ''.join([secam.video_folder, get_filename_prefix("P", time_mark), ".jpg"])
    camera.capture_photo(file_path) 
    
def check_action_messages(camera, ctrl):
    msg = ctrl.get_message()
    not_recognized = "Message not recognized [%s]" % msg
    error = False
    
    if msg:
        if len(msg) > 1:
            parts = msg.split(",")
            if len(parts) < 2:
                error = True
            else:
                action = parts[1]
                
                if action == "photo":
                    # Photos can be taken whilst surveillance is paused
                    time_mark = datetime.datetime.now()
                    take_photo(camera, time_mark)
                elif action == secamctrl.STOP:
                    if ctrl.get_status() == secamctrl.GO:
                        ctrl.set_status(action)
                        logging.info("STOP message received")
                    else:
                        logging.warn("Current status is already STOP")
                elif action == secamctrl.GO:
                    if ctrl.get_status() == secamctrl.STOP:
                        ctrl.set_status(action)
                        logging.info("GO message received")
                    else:
                        logging.warn("Current status is already GO")
                elif len(parts) == 3:
                    arg = parts[2]
                    if action == secamctrl.BRIGHTNESS:
                        logging.info("Brightness was set to %s" % camera.brightness)
                        camera.brightness = int(arg)
                        ctrl.set_brightness(int(arg))
                        logging.info("Brightness changed to %s" % arg)
                    elif action == secamctrl.CONTRAST:
                        logging.info("Contrast was set to %s" % camera.contrast)
                        camera.contrast = int(arg)
                        ctrl.set_contrast(camera.contrast)
                        logging.info("Contrast changed to %s" % arg)
                    elif action == secamctrl.EXPOSURE_MODE:
                        logging.info("Exposure mode was set to %s" % camera.exposure_mode)
                        camera.exposure_mode = arg
                        ctrl.set_exposure_mode(camera.exposure_mode)
                        logging.info("Exposure mode changed to %s" % arg)
                    elif action == secamctrl.ISO:
                        logging.info("ISO sensitivity was set to %s" % camera.iso)
                        camera.iso = int(arg)
                        ctrl.set_iso(camera.iso)
                        logging.info("ISO sensitivity changed to %s" % arg)
                        
                else:
                    error = True
    
                ctrl.dequeue_message(msg)
        else:
            error = True     
    
        if error:
            logging.info(not_recognized)     
                
    
def initialise_pir():
    GPIO.setmode(GPIO.BCM)
    GPIO.setup(PIR, GPIO.IN, GPIO.PUD_DOWN)
    start_time = time.time()
    
    while GPIO.input(PIR) == GPIO.HIGH:
        time.sleep(0.1)
        
    stop_time = time.time()
    logging.info("Sensor reset took: %0.1f secs" % (stop_time - start_time))
    
def send_mail_and_backup_video(event_id, time_mark, h264_path):
    send_mail(event_id, time_mark)    
    mp4_path = convert2mp4(event_id, h264_path)    
    msg, ok = secam.backup_file(file_part(mp4_path))
    out(event_id, msg)

# def spawn_remaining_tasks(event_id, time_mark, h264_path):
#     task = threading.Thread(target=send_mail_and_backup_video, args=[event_id, time_mark, h264_path])
#     task.start()        


def spawn_messaging_service(ctrl):
    ctrl.start()        

def spawn_messaging_client(ctrl, camera):
    timer = 0
    small_sleep = 0.2
    big_sleep = 2
    
    while True:        
        if timer >= big_sleep:
            timer = 0
            check_action_messages(camera, ctrl)
            
        time.sleep(small_sleep)
        timer += small_sleep

def file_part(file_path):
    return file_path.split("/")[-1]

def out(event_id, s):
    logging.info("%d) ... %s" % (event_id, s))
    
    
CTRL = secamctrl.SecamController()
CAMERA = secam.Secam()
EVENT_COUNTER = 1
PIR = 4

initialise_pir()
start_new_thread(spawn_messaging_service, (CTRL,))     
start_new_thread(spawn_messaging_client, (CTRL, CAMERA))     

# From observation, it seems that the PIR will go LOW after being HIGH for 8 seconds,
# then will not go high again for another 8 seconds. This means that there will always
# be 8 second gaps in videos taken during a long event.

try:    
    while True:
        if GPIO.wait_for_edge(PIR, GPIO.RISING): 
            time_mark = datetime.datetime.now()
            time_str = time_mark.strftime("%Y/%m/%d %H:%M:%S")
            
            if CTRL.get_status() == secamctrl.GO:
                if  not CAMERA.recording:       
                    h264_path = record_video(CAMERA, EVENT_COUNTER, time_mark)
                    start_new_thread(send_mail_and_backup_video, (EVENT_COUNTER, time_mark, h264_path))
                    EVENT_COUNTER += 1
                else:
                    logging.info("Alarm raised at %s, but previous event video is still being recorded" % time_str)
            else:
                logging.info("Alarm raised at %s, but surveillance is currently OFF" % time_str)
                
                
except KeyboardInterrupt:
    logging.info("Secam application finished")
    GPIO.cleanup()

