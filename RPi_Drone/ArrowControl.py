#from subprocess import call
import RPi.GPIO as GPIO
import atexit
import time
import pygame

def exit_handler():
	GPIO.output(blueLED, GPIO.LOW)
        GPIO.output(greenLED, GPIO.LOW)
	GPIO.cleanup()

blueLED = 18
greenLED = 8
redLED = 10
whiteLED = 11
delay = 0.5

#Board setup
GPIO.setmode(GPIO.BOARD)

#BlueLED
GPIO.setup(blueLED, GPIO.OUT)
GPIO.output(blueLED, GPIO.HIGH)

#GreenLED
GPIO.setup(greenLED, GPIO.OUT)
GPIO.output(greenLED, GPIO.LOW)

#RedLED
GPIO.setup(redLED, GPIO.OUT)
GPIO.output(redLED, GPIO.LOW)

#WhiteLED
GPIO.setup(whiteLED, GPIO.OUT)
GPIO.output(whiteLED, GPIO.LOW)

atexit.register(exit_handler)

#Switch back and forth between the lights
while True:
	GPIO.output(blueLED, GPIO.LOW)
        GPIO.output(greenLED, GPIO.HIGH)
        time.sleep(delay)
        GPIO.output(greenLED, GPIO.LOW)
        GPIO.output(blueLED, GPIO.HIGH)
	time.sleep(delay)
	GPIO.output(blueLED, GPIO.LOW)
	GPIO.output(whiteLED, GPIO.HIGH)
	time.sleep(delay)
	GPIO.output(whiteLED, GPIO.LOW)
	GPIO.output(redLED, GPIO.HIGH)
	time.sleep(delay)
	GPIO.output(redLED, GPIO.LOW)
