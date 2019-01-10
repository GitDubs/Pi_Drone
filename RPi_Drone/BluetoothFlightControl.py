import bluetooth
import time
import os
os.system("sudo pigpiod")
time.sleep(1)
import pigpio
import atexit
import sys
import struct
import math
#from Adafruit_PWM_Servo_Driver import PWM

ESC1 = 14 #Green  Connect the ESC in this GPIO pin, 12, 14, is a working BCM pwm pin
ESC2 = 15 #Red
ESC3 = 23 #White
ESC4 = 24 #Gray

pi = pigpio.pi();
pi.set_servo_pulsewidth(ESC1, 0)
pi.set_servo_pulsewidth(ESC2, 0)
pi.set_servo_pulsewidth(ESC3, 0)
pi.set_servo_pulsewidth(ESC4, 0)

def exit_handler():
        pi.set_servo_pulsewidth(ESC1, 0)
	pi.set_servo_pulsewidth(ESC2, 0)
	pi.set_servo_pulsewidth(ESC3, 0)
	pi.set_servo_pulsewidth(ESC4, 0)

def motor_control(speed1, speed2, speed3, speed4):
	print "%d,  %d,  %d,  %d" % (speed1, speed2, speed3, speed4)
        pi.set_servo_pulsewidth(ESC1, speed1)
	pi.set_servo_pulsewidth(ESC2, speed2)
	pi.set_servo_pulsewidth(ESC3, speed3)
	pi.set_servo_pulsewidth(ESC4, speed4)
        return


max_value = 2500 #change this if your ESC's max value is different or leave it be
min_value = 1600  #change this if your ESC's min value is different or leave it be

def calibrate():   #This is the auto calibration procedure of a normal ESC
    pi.set_servo_pulsewidth(ESC1, 0)
    pi.set_servo_pulsewidth(ESC2, 0)
    pi.set_servo_pulsewidth(ESC3, 0)
    pi.set_servo_pulsewidth(ESC4, 0)
    print("Disconnect the battery and press Enter")
    inp = raw_input()
    if inp == '':
        pi.set_servo_pulsewidth(ESC1, max_value)
        pi.set_servo_pulsewidth(ESC2, max_value)
        pi.set_servo_pulsewidth(ESC3, max_value)
        pi.set_servo_pulsewidth(ESC4, max_value)
        print("Connect the battery NOW and wait for two beeps to set the max pulsewidth of " + str(max_value))
        inp = raw_input()
        if inp == '':
            print("Max pulsewidth set: " + str(max_value) )
            pi.set_servo_pulsewidth(ESC1, min_value)
            pi.set_servo_pulsewidth(ESC2, min_value)
            pi.set_servo_pulsewidth(ESC3, min_value)
            pi.set_servo_pulsewidth(ESC4, min_value)
            print ("Setting minimum pulsewidth of " + str(min_value))
            time.sleep(7)
            print ("Minimum pulsewidth set: " + str(min_value))
            pi.set_servo_pulsewidth(ESC1, 0)
            pi.set_servo_pulsewidth(ESC2, 0)
            pi.set_servo_pulsewidth(ESC3, 0)
            pi.set_servo_pulsewidth(ESC4, 0)
            time.sleep(2)
            print "ESC calibrated"
            print "Arming ESC now..."
            pi.set_servo_pulsewidth(ESC1, min_value)
            pi.set_servo_pulsewidth(ESC2, min_value)
            pi.set_servo_pulsewidth(ESC3, min_value)
            pi.set_servo_pulsewidth(ESC4, min_value)
        time.sleep(1)


calibrate()

port = 0
server_sock=bluetooth.BluetoothSocket( bluetooth.RFCOMM )
server_sock.bind(("",port))
server_sock.listen(1)
uuid = "0a76efc0-6f89-46f3-be94-92055b259ab6"
bluetooth.advertise_service( server_sock, "Drone", uuid )

print "Starting Bluetooth Drone Control server on port: %d" % port

while True:
	print "listening on port %d" % port
        client_sock,address = server_sock.accept()
        print "Accepted connection from ",address

	try:
                data = client_sock.recv(1024)
                print "received [%s]" % data
        except bluetooth.btcommon.BluetoothError:
                motor_control(0,0,0,0)
        while True:
                try:
                        data = client_sock.recv(16)
                        speed1 = struct.unpack_from('f', data, 0)
                        speed2 = struct.unpack_from('f', data, 4)
			speed3 = struct.unpack_from('f', data, 8)
			speed4 = struct.unpack_from('f', data, 12)
                        motor_control(speed1[0],speed2[0],speed3[0],speed4[0])
                except bluetooth.btcommon.BluetoothError:
                        print "Connection lost"
                        motor_control(0,0,0,0)
                        break
