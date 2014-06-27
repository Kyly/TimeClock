TimeClock
==========

This program is designed to help employees and employer
alike track hours in the workplace. This is done by tracking idle time on
company work stations during regular working hours. TimeClock reports idle
time by using native Windows utilities that track key stroke and mouse movement.
0 is reported when when there is movement, each second that the IOs are in active
a integer representing the number of seconds since last active are sent to the 
company server, when the number of seconds of inactivity exceed a threshold 
detriment by the administrator during the next period of activity the user's 
browser will open and request accounting for the period of inactivity. A small 
icon is shown in the system try allowing the user to exit the program or read 
short dialog about the program. All fatal errors are reported by alert pop-ups 
while less server errors are reported to the standard error in the command line. 

The program can be run from the terminal in two ways.
- ````java -jar TimeClock.jar```` for normal output
- ````java -jar TimeClock.jar --debug```` will print details of the programs
execution to the terminal via standard err.

The program can also be run by simply double clicking on the jar file itself.

To exit the program right click on the TimeClock tray icon and select exit.

Dependencies
------------
- Java 7 or above, which can be downloaded at ````https://www.java.com/en/download/````
- Window 7

Setup
-----
- Must have configuration file named ````timeclock.conf.txt```` in ````C:/login/```` folder.
you can find a example of a configuration file in the root directory of this
repository.
- Of course you will need a Internet connection, if no Internet connection is 
the program will run but will be ineffective.