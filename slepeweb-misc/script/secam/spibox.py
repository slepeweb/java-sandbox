#!/usr/bin/python
#
import secam, time, datetime, os, subprocess, threading, smtplib, picamera
import RPi.GPIO as GPIO
from email.mime.text import MIMEText

mail_from = "george@slepeweb.com"
mail_to = "george@buttigieg.org.uk"
mail_subject = "Security Alarm"
web_page = "http://www.slepeweb.com/secam/app/index.py"
mail_body = """
A security alarm (#%d) has been raised @ %s.

Please investigate further @ %s
"""

def get_filename_prefix(event_id, time_mark):
    return "%d-%s" % (event_id, time_mark.strftime("%Y%m%d%H%M%S"))

# def photo(camera, event_id):
#     print('%d) Motion detected! ...' % event_id)
#     for i in range(1,3):
#         capturename = ''.join(["/home/pi/spibox/capture/", get_filename_prefix(event_id), "-", str(event_id), "-", str(i), ".jpg"])
#         print('... ' + capturename)
#         camera.capture(capturename)
#         time.sleep(0.5)

def record_video(camera, event_id, time_mark):
    print("%d) Motion detected! ..." % event_id)
    h264_path = ''.join([secam.video_folder, get_filename_prefix(event_id, time_mark), ".h264"])
    out(event_id, "recording to [%s]" % h264_path)
    camera.start_recording(h264_path, quality=23)
    camera.wait_recording(20)
    camera.stop_recording()    
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
    msg = MIMEText(mail_body % (event_id, event_time, web_page))
    msg['Subject'] = mail_subject
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

def initialise_pir():
    start_time = time.time()
    
    while GPIO.input(PIR) == GPIO.HIGH:
        time.sleep(0.1)
        
    stop_time = time.time()
    print ("Sensor reset took: %0.1f secs" % ((stop_time-start_time)))
    
def initialise_camera():
    c = picamera.PiCamera()
    c.resolution = (1280, 720)
    c.vflip = True
    # c.iso = 100 for daytime, 4-800 for low light (sensitivity)
    #
    # For daylight conditions:
    # camera.shutter_speed = camera.exposure_speed
    # camera.exposure_mode = 'off'
    # g = camera.awb_gains
    # camera.awb_mode = 'off'
    # camera.awb_gains = g
    #
    # For low-light conditions:
    # camera.framerate = Fraction(1, 6)
    # camera.shutter_speed = 6000000
    # camera.exposure_mode = 'off'
    # camera.iso = 800

    c.start_preview()
    return c

def send_mail_and_backup_video(event_id, time_mark, h264_path):
    send_mail(event_id, time_mark)    
    mp4_path = convert2mp4(event_id, h264_path)    
    msg, ok = secam.backup_file(file_part(mp4_path))
    out(event_id, msg)

def spawn_remaining_tasks(event_id, time_mark, h264_path):
    fred = threading.Thread(target=send_mail_and_backup_video, args=[event_id, time_mark, h264_path])
    #fred.daemon = True
    fred.start()        

def file_part(file_path):
    return file_path.split("/")[-1]

def out(event_id, str):
    print("%d) ... %s" % (event_id, str))

PIR = 4
event_counter = 0
GPIO.setmode(GPIO.BCM)
GPIO.setup(PIR, GPIO.IN, GPIO.PUD_DOWN)
camera = None
FNULL = open(os.devnull, 'w')

try:
    initialise_pir()
    camera = initialise_camera()
    
    while True:
        #GPIO.wait_for_edge(PIR, GPIO.RISING)
        if GPIO.input(PIR) == GPIO.HIGH:
            time_mark = datetime.datetime.now()
            h264_path = record_video(camera, event_counter, time_mark)
            spawn_remaining_tasks(event_counter, time_mark, h264_path)
            event_counter += 1
        
        time.sleep(0.1)

except KeyboardInterrupt:
    print "  Bye for now"
    GPIO.cleanup()

finally:
    if camera != None:
        camera.close()