import RPi.GPIO as GPIO
import time

GPIO.setmode(GPIO.BCM)
PIR = 4
GPIO.setup(PIR, GPIO.IN)
timing_started = False

try:
    while True:
        i = GPIO.input(PIR)
        if i==1 and not timing_started:
            timing_started = True
            start_time = time.time()
        elif i==0 and timing_started:
            stop_time = time.time()
            timing_started = False
            print ("Reset took: %0.3f ms" %((stop_time-start_time)*1000.0))

except KeyboardInterrupt:
    print("Keyboard interrupt - exiting program")

finally:
    GPIO.cleanup()
