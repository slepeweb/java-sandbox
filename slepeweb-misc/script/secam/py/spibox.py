import constants, messaging
import time, logging
import RPi.GPIO as GPIO
from datetime import datetime


class Spibox:
    def __init__(self, ctrl):
        self.ctrl = ctrl
        self.const = constants.Constants()
        self.event_counter = 1
        self.logger = logging.getLogger("secam")
        
        GPIO.setmode(GPIO.BCM)
        GPIO.setup(self.const.pir_pin, GPIO.IN, GPIO.PUD_DOWN)
        start_time = time.time()
         
        while GPIO.input(self.const.pir_pin) == GPIO.HIGH:
            time.sleep(0.1)
        
        stop_time = time.time()
        self.logger.info("Sensor reset took: %0.1f secs" % (stop_time - start_time))

        
    def _service(self):
        # From observation, it seems that the PIR will go LOW after being HIGH for 8 seconds,
        # then will not go high again for another 8 seconds. This means that there will always
        # be 8 second gaps in videos taken during a long event.
        
        try:    
            while True:
                if GPIO.wait_for_edge(self.const.pir_pin, GPIO.RISING):
                    if self.ctrl.camera.get_status()[self.const.stat] == self.const.go:
                        task = messaging.Task(self.const.video, {})
                        task.id = self.event_counter
                        self.ctrl.enqueue(task)
                        self.event_counter += 1
                    else:
                        self.logger.info("Alarm raised at %s, but surveillance is currently OFF" % datetime.now().strftime("%Y/%m/%d %H:%M:%S"))
                 
                 
        except KeyboardInterrupt:
            self.logger.info("Keyboard interrupt - Spibox thread terminating")
            GPIO.cleanup()
