from subprocess import call
import RPi.GPIO as gpio

def loop():
	raw_input()

def shutdown(pin):
	call('halt', shell = False)

gpio.setmode(gpio.BOARD)
gpio.setup(5, gpio.IN)
gpio.add_event_detect(5, gpio.RISING, callback = shutdown, bouncetime = 200)

loop()
