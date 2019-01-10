import pigpio
import time
import os
import atexit

def exit_handler():
	pi.set_servo_pulsewidth(ESC, 0)

ESC = 18

pi = pigpio.pi();
pi.set_servo_pulsewidth(ESC, 0)
max_value = 2500
min_value = 1400



pi.set_servo_pulsewidth(ESC, max_value)
print "connect battery"
input = raw_input()
pi.set_servo_pulsewidth(ESC, min_value)
time.sleep(5)
pi.set_servo_pulsewidth(ESC, 1800)

print "press enter to stop"
input = raw_input()
atexit.register(exit_handler)

