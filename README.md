submarine-control
-----------------
<p>in git bash</p>
<p>git clone https://github.com/CharlieSwires/submarine-control</p>

<p>This contains both the Java and Bash</p>

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
<p>type: cd ..</p>
<p>type: ~/submarine-control/runner.sh</p>
<p> I put the cd's and chmod's into the .bashrc just so logging in corrects the permissions.</p>
<p> running the runner from .bashrc doesn't work fully: the java works but not the libcam.</p>

Playing the Video Stream
------------------------

<p>On your PC which is sharing it's WiFi hotspot with the Pi
connect to rtsp://192.168.137.205:8554/ on VLC. Your IP address may be different.</p>

RESTful
-------

EngineControl
-------------

<p>http://192.168.137.205:8080/submarine/engine/left/{percentPower}</p>
<p>http://192.168.137.205:8080/submarine/engine/right/{percentPower}</p>

DiveControl
-----------

<p>http://192.168.137.205:8080/submarine/dive/front/{angleDegrees}</p>
<p>http://192.168.137.205:8080/submarine/dive/back/{angleDegrees}</p>
<p>http://192.168.137.205:8080/submarine/dive/fill-tank/true</p>
<p>http://192.168.137.205:8080/submarine/dive/fill-tank/false</p>
<p>http://192.168.137.205:8080/submarine/dive/dive-angle</p>
<p>returns angleDegrees</p>
<p>http://192.168.137.205:8080/submarine/dive/depth</p>
<p>returns integer mm</p>

NavigationControl
-----------------

<p>http://192.168.137.205:8080/submarine/navigation/bearing</p>
<p>returns angleDegrees
<p>http://192.168.137.205:8080/submarine/navigation/rudder/{angleDegrees}</p>

