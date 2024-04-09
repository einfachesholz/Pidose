#imports the relevant modules
from PCF8574 import PCF8574_GPIO
from Adafruit_LCD2004 import Adafruit_CharLCD
from gpiozero import Button, Buzzer, Servo
from signal import pause
import time
#initialises the LCD
PCF8574_address = 0x27
PCF8574A_address = 0x3F

try:
    mcp = PCF8574_GPIO(PCF8574_address)
except:
    try:
        mcp = PCF8574_GPIO(PCF8574A_address)
    except:
        exit()

lcd = Adafruit_CharLCD(pin_rs=0, pin_e=2, pins_db=[4, 5, 6, 7], GPIO=mcp)
#Initialises the buttons, assigns them a GPIO pin and gives them a bounce time
button_stop = Button(12, bounce_time=0.1)
button_alarm = Button(16, bounce_time=0.1)
button_add = Button(7, bounce_time=0.1)
button_confirm = Button(25, bounce_time=0.1)
# Initalises the buzzer and servo and gives them a GPIO pin
buzzer = Buzzer(21)
servo = Servo(17)

alarm_hour = 0
alarm_minute = 0
alarm_set = True
alarm_list = []
count = 0

#Gets the current time and returns it in the hour:minute format
def get_time():
    t = time.localtime()
    return time.strftime("%H:%M", t)

#This function updates the display, checking whether to show the current time or inputting a custom alarm
def update_display():
    global lcd, alarm_list, alarm_set, alarm_hour, alarm_minute, count
    lcd.clear()
    
    if alarm_set:
        lcd.message("The time is - ",get_time())
        alarm = (f"{alarm_hour):{alarm_minute)")
        if alarm not in alarm_list:
            alarm_list.append(alarm)
            
    else:
        if count == 0:
            lcd.message(f"hours - {alarm_hour}")
        elif count == 1:
            lcd.message(f"minutes - {alarm_minute}")

#This function increases the time and checks whether the device is in hour mode (count = 0) or minute mode (count = 1)
def inc():
    global alarm_hour, alarm_minute, alarm_set, count
    if alarm_set:
        return
    
    if count == 0:
        alarm_hour += 5
        if alarm_hour > 23:
            alarm_hour = 0
        if alarm_hour < 10:
            alarm_hour = "0" + str(alarm_hour)
            alarm_hour = int(alarm_hour)

    elif count == 1:
        alarm_minute += 5
        if alarm_minute > 61:
            alarm_minute = 0
        if alarm_minute < 10:
            alarm_minute = "0" + str(alarm_minute)
            alarm_minute = int(alarm_minute)

    update_display()

# This function puts the program into making an alarm mode
def make_alarm():
    
    global lcd,alarm_hour, alarm_minute, count, alarm_set
    alarm_set = False
    alarm_hour = 0
    alarm_minute = 0
    count = 0
    lcd.message("making alarm")
    update_display()
    
#This function allows the program to exit out of alarm making mode and instead show the current time
def confirm():
    global count, alarm_set
    if count == 2:
        alarm_set = True
        count = 0
    else:
        count += 1
    update_display()

    now = get_time()
    if now in alarm_list:
        lcd.clear()
        lcd.message("medicine ready")
        buzzer.on()
        button_stop.wait_for_press()
        buzzer.off()
        
        update_display()

button_alarm.when_pressed = make_alarm
button_add.when_pressed = inc
button_confirm.when_pressed = confirm

if __name__ == '__main__':
    print('Program is starting ... ')
    try:
        while True:
            update_display()
            #This checks whether the current time is in the alarms list and thus alerts the user if it is
            now = get_time()
            if now in alarm_list:
                lcd.clear()
                lcd.message("medicine ready")
                buzzer.on()
                button_stop.wait_for_press()
                buzzer.off()

                time.sleep(1)
    except KeyboardInterrupt:
        exit()
