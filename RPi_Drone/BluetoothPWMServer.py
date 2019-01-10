import bluetooth
import RPi.GPIO as GPIO
import atexit
import sys
import struct
import math

def exit_handler():
        GPIO.output(yellowLED, GPIO.LOW)
        GPIO.cleanup()
        client_sock.close()
        server_sock.close()
def connection_lost():
	myFloat = 0
	yellowPWM.ChangeDutyCycle(myFloat)
	return
def motor_control(x, y):
	print "x: " + str(x) 
	print "y: " + str(y)
	bluePWM.ChangeDutyCycle(abs(x/2))
	redPWM.ChangeDutyCycle(abs(x/2))
	greenPWM.ChangeDutyCycle(abs(y/2))
	whitePWM.ChangeDutyCycle(abs(y/2))
	return

server_sock=bluetooth.BluetoothSocket( bluetooth.RFCOMM )
	
#GPIO pin id's
yellowLED = 12
blueLED = 7
greenLED = 8
redLED = 10
whiteLED = 11

#Board setup
GPIO.setmode(GPIO.BOARD)

#Yellow PWM
GPIO.setup(yellowLED, GPIO.OUT)
yellowPWM = GPIO.PWM(yellowLED, 200)
yellowPWM.start(0)

#Blue PWM
GPIO.setup(blueLED, GPIO.OUT)
bluePWM = GPIO.PWM(blueLED, 200)
bluePWM.start(0)

#Green PWM
GPIO.setup(greenLED, GPIO.OUT)
greenPWM = GPIO.PWM(greenLED, 200)
greenPWM.start(0)

#Red PWM
GPIO.setup(redLED, GPIO.OUT)
redPWM = GPIO.PWM(redLED, 200)
redPWM.start(0)

#White PWM
GPIO.setup(whiteLED, GPIO.OUT)
whitePWM = GPIO.PWM(whiteLED, 200)
whitePWM.start(0)

atexit.register(exit_handler)


myFloat = 0
port = 0
server_sock.bind(("",port))
server_sock.listen(1)

uuid = "0a76efc0-6f89-46f3-be94-92055b259ab6"
bluetooth.advertise_service( server_sock, "Drone", uuid )

while True: 
	print "listening on port %d" % port
	client_sock,address = server_sock.accept()
	print "Accepted connection from ",address
	
	try:
		data = client_sock.recv(1024)
		print "received [%s]" % data
	except bluetooth.btcommon.BluetoothError:
		connection_lost()
	while True:
		try:
        		data = client_sock.recv(9)
			x = struct.unpack_from('f', data, 1)
			y = struct.unpack_from('f', data, 5)
			myFloat = float(ord(data[0]))
			if x[0] != -1:
				motor_control(x[0],y[0])
			if myFloat != 255: #255 is -1 as a float
                		yellowPWM.ChangeDutyCycle(myFloat)
		except bluetooth.btcommon.BluetoothError:
			print "Connection lost"
			connection_lost()
			break
