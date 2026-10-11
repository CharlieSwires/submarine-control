# Copyright

Copyright © 2026 Charlie's Tenacity Ltd. All rights reserved.

This is proprietary software. No permission is granted to copy, modify, distribute, sublicense or use this software except under a written licence issued by Charlie's Tenacity Ltd. Third-party components remain subject to their respective licences.

submarine-control
-----------------
<p>In Git Bash:</p>
<p>git clone https://github.com/CharlieSwires/submarine-control</p>

<p>This repository contains both the Java submarine-control application and the Bash scripts used to run it on the Raspberry Pi.</p>

<p>Note: if you want to be sure that you have the latest code, you can delete the local submarine-control directory and clone the repository again.</p>

Current Raspberry Pi / Camera Hardware
--------------------------------------

<p>The submarine now uses a <strong>Raspberry Pi Zero 2 W</strong> rather than the earlier Raspberry Pi 4B. The Zero 2 W was chosen because its smaller physical size and lower power consumption are better suited to the submarine.</p>

<p>The original Raspberry Pi ribbon-cable/CSI camera is no longer used. Video is provided by a <strong>waterproof USB HD camera</strong>, connected to the Pi as a standard V4L2 USB video device (normally <code>/dev/video0</code>).</p>

<p>The current video script is <code>usb_mjpeg_rtsp.sh</code>. It expects the USB camera to support MJPEG and streams the video using VLC over RTSP. The defaults are 640x480, with <code>runner.sh</code> currently requesting 25 fps.</p>

build
-----
<p>mvn clean package</p>

<p>This produces <code>submarine-control.jar</code> in the <code>target</code> directory.</p>

deploy
------
<p>Turn on the configured <strong>Raspberry Pi Zero 2 W</strong>. This procedure is needed for the first installation or after making changes to the repository.</p>

<p>Using PuTTY (or another SSH client) on Windows, SSH onto the Pi. Make sure SSH has first been enabled on the Pi.</p>

<p>Type:</p>
<p><code>git clone (your copy of the submarine-control repo)</code></p>
<p><code>cd submarine-control</code></p>
<p><code>chmod 777 runner.sh</code></p>
<p><code>chmod 777 usb_mjpeg_rtsp.sh</code></p>
<p><code>chmod 777 run_file.sh</code></p>
<p><code>cd ..</code></p>
<p><code>~/submarine-control/runner.sh</code></p>

<p>The current <code>runner.sh</code> starts the waterproof USB camera RTSP stream and the Java submarine-control application. It also enables I2C and brings <code>wlan0</code> down where required by the submarine networking configuration.</p>

<p>If you wish to enable emergency surfacing on loss of contact, set:</p>
<p><code>export WATCHDOG_ENABLED="true"</code></p>

<p><code>runner.sh</code> currently supplies this setting when the Java application is started.</p>

<p>For automatic startup, run:</p>
<p><code>crontab -e</code></p>
<p>and add:</p>
<p><code>@reboot /path/to/run_file.sh</code></p>

USB Camera / Video Stream
-------------------------

<p>The waterproof USB HD camera is normally expected at <code>/dev/video0</code>.</p>

<p>The streaming script requires VLC (<code>cvlc</code>) and <code>v4l2-ctl</code>. The camera must advertise an MJPEG mode. The script checks this before starting the stream.</p>

<p>The camera settings can be overridden with environment variables. For example:</p>
<p><code>WIDTH=640 HEIGHT=480 FPS=25 ~/submarine-control/usb_mjpeg_rtsp.sh</code></p>

<p>The RTSP server listens on port <code>8554</code>.</p>

Playing the Video Stream
------------------------

<p>On the PC sharing its Wi-Fi hotspot with the Pi, open VLC and connect to:</p>
<p><code>rtsp://192.168.137.205:8554/</code></p>

<p>The Pi IP address may be different on your network. The <code>usb_mjpeg_rtsp.sh</code> script prints the detected host IP and RTSP URL when it starts.</p>

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
<p>http://192.168.137.205:8080/submarine/dive/fill-tank/{percentPower} - done</p>

<p><strong>12 V High-Power MOSFET Switch</strong></p>
<p>The submarine's high-current 12 V supply is switched by the 100 A MOSFET module. The control input is driven from Raspberry Pi <strong>GPIO 13</strong> through a transistor interface, so the Pi GPIO does not directly switch the high-current 12 V load.</p>
<p><code>http://192.168.137.205:8080/submarine/dive/power/1</code> - enable high-power 12 V supply (GPIO 13 HIGH)</p>
<p><code>http://192.168.137.205:8080/submarine/dive/power/0</code> - disable high-power 12 V supply (GPIO 13 LOW)</p>
<p>The GPIO is configured LOW initially and LOW on shutdown, so the high-power supply defaults to off.</p>

<p>http://192.168.137.205:8080/submarine/dive/dive-angle - done</p>
<p>returns angleDegrees - offset</p>
<p>http://192.168.137.205:8080/submarine/dive/depth - done</p>
<p>returns integer mm - offset</p>
<p>http://192.168.137.205:8080/submarine/dive/zero - done - sets the
offset for diveAngle and depth</p>

NavigationControl
-----------------

<p>http://192.168.137.205:8080/submarine/navigation/bearing - done</p>
<p>returns angleDegrees</p>
<p>http://192.168.137.205:8080/submarine/navigation/rudder/{angleDegrees} - done</p>

WiFi Dongle
-----------

<p>https://thepihut.com/blogs/raspberry-pi-tutorials/how-to-setup-a-rtl881cu-usb-wifi-adapter-with-the-raspberry-pi-4</p>
<p>The runner configuration disables <code>wlan0</code> as part of the submarine's current networking setup.</p>
