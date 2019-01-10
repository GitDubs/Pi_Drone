# This program will let you test your ESC and brushless motor.
# Make sure your battery is not connected if you are going to calibrate it at first.
# Since you are testing your motor, I hope you don't have your propeller attached to it otherwise you are in trouble my friend...?
# This program is made by AGT @instructable.com. DO NOT REPUBLISH THIS PROGRAM... actually the program itself is harmful                                             pssst Its not, its safe.

import os     #importing os library so as to communicate with the system
import time   #importing time library to make Rpi wait because its too impatient 
os.system ("sudo pigpiod") #Launching GPIO library
time.sleep(1) # As i said it is too impatient and so if this delay is removed you will get an error
import pigpio #importing GPIO library
import atexit

ESC1 = 14 #Green  Connect the ESC in this GPIO pin, 12, 14, is a working BCM pwm pin
ESC2 = 15 #Red
ESC3 = 23 #White
ESC4 = 24 #Gray

pi = pigpio.pi();
pi.set_servo_pulsewidth(ESC1, 0) 
pi.set_servo_pulsewidth(ESC2, 0)
pi.set_servo_pulsewidth(ESC3, 0)
pi.set_servo_pulsewidth(ESC4, 0)


max_value = 2500 #change this if your ESC's max value is different or leave it be
min_value = 1600  #change this if your ESC's min value is different or leave it be
print "For first time launch, select calibrate"
print "Type the exact word for the function you want"
print "calibrate OR manual OR control OR arm OR stop"

def exit_handler():
        pi.set_servo_pulsewidth(ESC1, 0)
	pi.set_servo_pulsewidth(ESC2, 0)
	pi.set_servo_pulsewidth(ESC3, 0)
	pi.set_servo_pulsewidth(ESC4, 0)
def manual_drive(): #You will use this function to program your ESC if required
    print "You have selected manual option so give a value between 0 and you max value"    
    while True:
        inp = raw_input()
        if inp == "stop":
                stop()
                break
        elif inp == "control":
            control()
            break
        elif inp == "arm":
            arm()
            break
        else:
            pi.set_servo_pulsewidth(ESC1, inp)
            pi.set_servo_pulsewidth(ESC2, inp)
            pi.set_servo_pulsewidth(ESC3, inp)
            pi.set_servo_pulsewidth(ESC4, inp)
                
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
        individual_control() # You can change this to any other function you want
            
def control(): 
    time.sleep(1)
    speed = 1700    # change your speed if you want to.... it should be between 700 - 2000
    print "Controls - a to decrease speed & d to increase speed OR q to decrease a lot of speed & e to increase a lot of speed"
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
        elif inp == "manual":
            manual_drive()
            break
        elif inp == "arm":
            arm()
            break
        elif inp == "manual calibrate":
            manual_calibrate()
        elif inp == "individual control":
            individual_control()
        else:
            print "WHAT DID I SAID!! Press a,q,d or e"

        pi.set_servo_pulsewidth(ESC1, speed)
        pi.set_servo_pulsewidth(ESC2, speed)
        pi.set_servo_pulsewidth(ESC3, speed)
        pi.set_servo_pulsewidth(ESC4, speed)
            
def arm(): #This is the arming procedure of an ESC 
    print "Connect the battery and press Enter"
    inp = raw_input()    
    if inp == '':
        pi.set_servo_pulsewidth(ESC1, 0)
	pi.set_servo_pulsewidth(ESC2, 0)
	pi.set_servo_pulsewidth(ESC3, 0)
	pi.set_servo_pulsewidth(ESC4, 0)
        time.sleep(1)
        pi.set_servo_pulsewidth(ESC1, max_value)
	pi.set_servo_pulsewidth(ESC2, max_value)
	pi.set_servo_pulsewidth(ESC3, max_value)
	pi.set_servo_pulsewidth(ESC4, max_value)
        time.sleep(1)
        pi.set_servo_pulsewidth(ESC1, min_value)
	pi.set_servo_pulsewidth(ESC2, min_value)
	pi.set_servo_pulsewidth(ESC3, min_value)
	pi.set_servo_pulsewidth(ESC4, min_value)
        time.sleep(1)
        control() 
        
def stop(): #This will stop every action your Pi is performing for ESC ofcourse.
    pi.set_servo_pulsewidth(ESC1, 0)
    pi.set_servo_pulsewidth(ESC2, 0)
    pi.set_servo_pulsewidth(ESC3, 0)
    pi.set_servo_pulsewidth(ESC4, 0)
    pi.stop()

def manual_calibrate():
    invalidESC = True
    print "Enter ESC number to calibrate"
    while invalidESC == True:	#Choosing a valid ESC
        input = int(raw_input())
        invalidESC = False
        if input == 1:
            tempESC = ESC1
        elif input == 2:
            tempESC = ESC2
        elif input == 3:
            tempESC = ESC3
        elif input == 4:
            tempESC = ESC4
        else:
            invalidESC = True
            print "Please enter a valid ESC number (1-4)"

    print "Disconnect battery and press enter"	#Arming process for one ESC
    input = raw_input()
    pi.set_servo_pulsewidth(tempESC, max_value)
    print "Connect the battery now and press enter"
    input = raw_input()
    print("Max pulsewidth set: " + str(max_value))
    pi.set_servo_pulsewidth(tempESC, min_value)
    print ("Setting minimum pulsewidth of " + str(min_value))
    time.sleep(7)
    print ("Minimum pulsewidth set: " + str(min_value))
    pi.set_servo_pulsewidth(tempESC, 0)
    time.sleep(2)
    print "ESC calibrated"
    print "Arming ESC now..."
    pi.set_servo_pulsewidth(tempESC, min_value)
    time.sleep(1)
    individual_control()

def individual_control():
    time.sleep(1)
    invalidESC = True
    while True:
        print "Enter ESC number to calibrate"
        while invalidESC == True:  # Choosing a valid ESC
            input = int(raw_input())
            invalidESC = False
            if input == 1:
                tempESC = ESC1
            elif input == 2:
                tempESC = ESC2
            elif input == 3:
                tempESC = ESC3
            elif input == 4:
                tempESC = ESC4
            else:
                invalidESC = True
                print "Please enter a valid ESC number (1-4)"

        print "Enter a valid speed (1600 - 2400): "
        while True:
            inp = raw_input()
            if inp == "arm":
                arm()
                break
            elif inp == "manual calibrate":
                manual_calibrate()
            elif inp =="quit":
        	        break
            elif int(inp) >= 1600 and int(inp) <= 2500:
                pi.set_servo_pulsewidth(tempESC, inp)
	    else:
		print "Nothing changed"
	invalidESC = True
        print "Quit individual speed control (y or n): "
        inp = raw_input()
        if inp == "y":
            break
    control()	

#Begin program execution here    
atexit.register(stop)
inp = raw_input()
if inp == "manual":
    manual_drive()
elif inp == "calibrate":
    calibrate()
elif inp == "arm":
    arm()
elif inp == "control":
    control()
elif inp == "stop":
    stop()
elif inp == "manual calibrate":
    manual_calibrate()
else :
    print "Thank You for not following the things I'm saying... now you gotta restart the program STUPID!!"
