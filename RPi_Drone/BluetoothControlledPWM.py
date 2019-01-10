import RPi.GPIO as GPIO
import time
import atexit
import sys
import bluetooth

#Setup the bluetooth connection
uuid = "0a76efc0-6f89-46f3-be94-92055b259ab6"
service_matches = bluetooth.find_service( uuid = uuid )
if len(service_matches) == 0:
	print "No active connection found"
	sys.exit(0)

first_match = service_matches[0]
port = first_match["port"]
name = first_match["name"]
host = first_match["host"]

print "connecting to \"%s\" on %s" % (name, host)

sock=bluetooth.BluetoothSocket( bluetooth.RFCOMM )
sock.connect((host, port))
sock.send("Connecting")

def exit_handler():
        GPIO.output(yellowLED, GPIO.LOW)
        GPIO.cleanup()

#GPIO pin setup
yellowLED = 12
GPIO.setmode(GPIO.BOARD)
GPIO.setup(yellowLED, GPIO.OUT)
yellowPWM = GPIO.PWM(yellowLED, 100)
yellowPWM.start(0)

atexit.register(exit_handler)

while True:
	data = sock.recv(1)
	myFloat = float(ord(data[0]))
	yellowPWM.ChangeDutyCycle(myFloat)
