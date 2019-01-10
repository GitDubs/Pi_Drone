from Adafruit_PWM_Servo_Driver import PWM
import time

pwm = PWM(0x40)

max_value = 2500
min_value = 1600

ESC1 = 0

def setServoPulse(channel, pulse):
  pulseLength = 1000000                   # 1,000,000 us per second
  pulseLength /= 60                       # 60 Hz
  print "%d us per period" % pulseLength
  pulseLength /= 4096                     # 12 bits of resolution
  print "%d us per bit" % pulseLength
  pulse *= 1000
  pulse /= pulseLength
  pwm.setPWM(channel, 0, pulse)

def control():
	time.sleep(1)
	speed = 1700    # change your speed if you want to.... it should be between 700 - 2000
	print "Controls - a to decrease speed & d to increase speed OR q to decrease a lot of speed & e to increase a lot"
	while True:
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
		setServoPulse(ESC1, speed)

#Execution start
setServoPulse(ESC1, 0)
print("Disconnect the battery and press Enter")
inp = raw_input()
if inp == '':
	setServoPulse(ESC1, min_value)
	print("Connect the battery NOW and wait for two beeps to set the max pulsewidth of " + str(max_value))
	inp = raw_input()
        if inp == '':
		print("Max pulsewidth set: " + str(max_value) )
		setServoPulse(ESC1, min_value)
		time.sleep(7)
		print ("Minimum pulsewidth set: " + str(min_value))
		setServoPulse(ESC1, 0)
control()
