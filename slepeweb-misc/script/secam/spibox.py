import secam, time, datetime, subprocess, smtplib, picamera
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

def get_filename_prefix(j, time_mark):
    return "%d-%s" % (j, time_mark.strftime("%Y%m%d%H%M%S"))

# def photo(camera, j):
#     print('%d) Motion detected! ...' % j)
#     for i in range(1,3):
#         capturename = ''.join(["/home/pi/spibox/capture/", get_filename_prefix(j), "-", str(j), "-", str(i), ".jpg"])
#         print('... ' + capturename)
#         camera.capture(capturename)
#         time.sleep(0.5)

def record_video(camera, j, time_mark):
    print("%d) Motion detected! ..." % j)
    video_file_path = ''.join([secam.video_folder, get_filename_prefix(j, time_mark), ".h264"])
    out("recording to [%s]" % video_file_path)
    camera.start_recording(video_file_path, quality=23)
    camera.wait_recording(15)
    camera.stop_recording()    
    return convert2mp4(video_file_path)

def convert2mp4(infile_path):
    outfile_path = infile_path.replace("h264", "mp4")
    out("converting to [%s]" % outfile_path)
    try:
        subprocess.check_call(["/usr/bin/MP4Box", "-add", infile_path, outfile_path])
        out("video conversion complete")
    except subprocess.CalledProcessError as e:
        out("*** error converting record_video file [%s]" % e)
    
    return outfile_path

def send_mail(event_id, time_mark):
    event_time = time_mark.strftime("%Y/%m/%d %H:%M:%S")
    msg = MIMEText(mail_body % (event_id, event_time, web_page))
    msg['Subject'] = mail_subject
    msg['From'] = mail_from
    msg['To'] = mail_to
    server = None
    
    try:
        out("sending mail")
        server = smtplib.SMTP("smtp.gmail.com", 587)
        server.starttls()
        server.login("george.buttigieg@gmail.com", "br1cktop1")
        server.sendmail(mail_from, [mail_to], msg.as_string())
        out("mail sent ok")
    except:
        out("*** problem sending mail")
        
    finally:
        if server != None:
            server.quit() 

def initialise_pir():
    start_time = time.time()
    
    while GPIO.input(PIR) == 1:
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

def file_part(file_path):
    return file_path.split("/")[-1]

def out(s):
    print("... %s" % s)

PIR = 4
counter = 0
GPIO.setmode(GPIO.BCM)
GPIO.setup(PIR, GPIO.IN, GPIO.PUD_DOWN)
camera = None

try:
    initialise_pir()
    camera = initialise_camera()
    
    while True:
        GPIO.wait_for_edge(PIR, GPIO.RISING)
        time_mark = datetime.datetime.now()
        video_file_path = record_video(camera, counter, time_mark)        
        send_mail(counter, time_mark)
        
        msg, ok = secam.backup_file(file_part(video_file_path))
        out(msg)
            
        counter += 1
        initialise_pir()

except KeyboardInterrupt:
    print "  Bye for now"
    GPIO.cleanup()

finally:
    if camera != None:
        camera.close()