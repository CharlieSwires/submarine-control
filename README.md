submarine-control
-----------------
<p>in git bash</p>
<p>git clone https://github.com/CharlieSwires/submarine-control</p>

<p>This contains both the Java and Bash. Note: if you want to be sure 
that you have the latest code delete the submarine-control directory and re clone,
git pull isn't reliable.</p>

build
-----
<p>mvn clean package</p>

<p>produces submarine-control.jar in target</p>

deploy
------
<p>Turn on your set up Raspberry Pi in my case
the Pi4b revision 1.5 Running Raspian Bullseye 32bit Full. 
This is for the first time or after you make a change in your repo.</p>
<p>Using putty on windows ssh onto the Pi, make sure you've enabled ssh on the Pi.</p>
<p>type: git clone (your copy of the submarine-control repo)</p>
<p>type: cd submarine-control</p>
<p>type: chmod 777 runner.sh</p>
<p>type: chmod 777 libcam.sh</p>
<p>type: chmod 777 runnerHD.sh</p>
<p>type: chmod 777 libcamHD.sh</p>
<p>type: chmod 777 run_file.sh</p>
<p>type: cd ..</p>
<p>type for SD: ~/submarine-control/runner.sh</p>
<p>or type for HD not both: ~/submarine-control/runnerHD.sh</p>
<p> I put the cd's and chmod's into the .bashrc just so logging in corrects the permissions.</p>
<p> In the .bashrc you will need: export WATCHDOG_ENABLED="true" if you wish for emergency 
surface on loss of contact. If you don't want to alter your .bashrc for the watchdog 
then you can do this in the runner.sh: sudo WATCHDOG_ENABLED="true" java -jar 
~/submarine-control/target/submarine-control.jar</p>
<p> crontab -e and add this line:</p>
<p>@reboot /path/to/run_file.sh</p>
<p>for auto run. </p>

Playing the Video Stream
------------------------

<p>On your PC which is sharing it's WiFi hotspot with the Pi 
connect to rtsp://192.168.137.205:8554/ on VLC. Your IP address may be different.</p>

RESTful
-------

EngineControl
-------------

<p>http://192.168.137.205:8080/submarine/engine/left/{percentPower} - done</p>
<p>http://192.168.137.205:8080/submarine/engine/right/{percentPower} - done</p>
<p>http://192.168.137.205:8080/submarine/engine/cpu-temp - done</p>
<p>returns integer Celcius * 10</p>

DiveControl
-----------

<p>http://192.168.137.205:8080/submarine/dive/front/{angleDegrees} - done</p>
<p>http://192.168.137.205:8080/submarine/dive/back/{angleDegrees} - done</p>
<p>http://192.168.137.205:8080/submarine/dive/fill-tank/true - done</p>
<p>http://192.168.137.205:8080/submarine/dive/fill-tank/false - done</p>
<p>http://192.168.137.205:8080/submarine/dive/dive-angle - done</p>
<p>returns angleDegrees - offset</p>
<p>http://192.168.137.205:8080/submarine/dive/depth - done</p>
<p>returns integer mm - offset</p>
<p>http://192.168.137.205:8080/submarine/dive/depth/zero - done - sets the 
offset for diveAngle and depth</p>

NavigationControl
-----------------

<p>http://192.168.137.205:8080/submarine/navigation/bearing - done</p>
<p>returns angleDegrees
<p>http://192.168.137.205:8080/submarine/navigation/rudder/{angleDegrees} - done</p>

WiFi Dongle
-----------

<p>https://thepihut.com/blogs/raspberry-pi-tutorials/how-to-setup-a-rtl881cu-usb-wifi-adapter-with-the-raspberry-pi-4</p>
<p>added rfkill block 0 to runner.sh and runnerHD.sh</p>

