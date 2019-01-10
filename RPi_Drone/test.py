import pygame, sys
import pygame.locals
import RPi.GPIO as GPIO
import atexit

def exit_handler():
        GPIO.cleanup()


pygame.init()
BLACK = (0,0,0)
WIDTH = 10
HEIGHT = 10
windowSurface = pygame.display.set_mode((WIDTH, HEIGHT), 0, 32)

windowSurface.fill(BLACK)

blueLED = 7
greenLED = 8
redLED = 10
whiteLED = 11
delay = 0.25

#Board setup
GPIO.setmode(GPIO.BOARD)

#BlueLED
GPIO.setup(blueLED, GPIO.OUT)
GPIO.output(blueLED, GPIO.LOW)

#GreenLED
GPIO.setup(greenLED, GPIO.OUT)
GPIO.output(greenLED, GPIO.LOW)

#RedLED
GPIO.setup(redLED, GPIO.OUT)
GPIO.output(redLED, GPIO.LOW)

#WhiteLED
GPIO.setup(whiteLED, GPIO.OUT)
GPIO.output(whiteLED, GPIO.LOW)


#To Determine if the buttons are pressed
Up = False
Down = False
Left = False
Right = False

atexit.register(exit_handler)

while True:
	event = pygame.event.poll()
	if event.type == pygame.KEYDOWN:
		if event.key == pygame.K_UP:    Up = not Up
                if event.key == pygame.K_DOWN:  Down = not Down
                if event.key == pygame.K_LEFT:  Left = not Left
                if event.key == pygame.K_RIGHT: Right = not Right
	if Up:
		GPIO.output(blueLED, GPIO.HIGH)
	else:
		GPIO.output(blueLED, GPIO.LOW)
	if Right:
		GPIO.output(greenLED, GPIO.HIGH)
	else:
		GPIO.output(greenLED, GPIO.LOW)
	if Down:
                GPIO.output(redLED, GPIO.HIGH)
        else:
                GPIO.output(redLED, GPIO.LOW)
	if Left:
                GPIO.output(whiteLED, GPIO.HIGH)
        else:
                GPIO.output(whiteLED, GPIO.LOW)
