import RPi.GPIO as GPIO
import time
import atexit

def exit_handler():
        GPIO.output(yellowLED, GPIO.LOW)
        GPIO.cleanup()
def constant_speed():
	pwm.ChangeDutyCycle(100)
	while True:
		pass 
sleep = 0.001
yellowLED = 18
GPIO.setmode(GPIO.BCM)
GPIO.setup(yellowLED, GPIO.OUT)
pwm = GPIO.PWM(yellowLED, 400)
pwm.start(0)

atexit.register(exit_handler)
#constant_speed()
while True:
	for x in range(100):
		pwm.ChangeDutyCycle(x)
		time.sleep(sleep)
	for x in range(100):
        	pwm.ChangeDutyCycle(100 - x)
        	time.sleep(sleep)
pwm.ChangeDutyCycle(100)
