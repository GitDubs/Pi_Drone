import os
import time
time.sleep(1)
import pigpio

ESC = 18

pi = pigpio.pi();
pi.set_servo_pulsewidth(ESC, 0)

min_value = 1200
max_value = 2500
speed = 1600

print("Disconnect battery and press enter")
inp = raw_input()
if inp == '':
	pi.set_servo_pulsewidth(ESC, max_value)
	print("Reconnect battery and wait for beeps")
	inp = raw_input()
	if inp == '':
		pi.set_servo_pulsewidth(ESC, min_value)
		time.sleep(7)
		pi.set_servo_pulsewidth(ESC, 0)
		time.sleep(5)

	while True:
	        pi.set_servo_pulsewidth(ESC, speed)
        	inp = raw_input()

        	if inp == "q":
            		speed -= 100    # decrementing the speed like hell
            		print "speed = %d" % speed
        	elif inp == "e":
            		speed += 100    # incrementing the speed like hell
            		print "speed = %d" % speed
        	elif inp == "d":
            		speed += 10     # incrementing the speed
            		print "speed = %d" % speed
        	elif inp == "a":
            		speed -= 10     # decrementing the speed
            		print "speed = %d" % speed
        	elif inp == "stop":
            		stop()          #going for the stop function
            		break

	

