#!/bin/bash
echo "===================================================running=========================================="
#export GST_DEBUG=3
sudo pkill pigpiod
sudo raspi-config nonint do_i2c 0
sudo ifconfig wlan0 down
export WATCHDOG_ENABLED="true"
#~/submarine-control/libcam.sh &
cd ~/submarine-control
WIDTH=640 HEIGHT=480 FPS=25 ./usb_mjpeg_rtsp.sh&
sudo java -jar target/submarine-control.jar 2>&1 | tee startup-error.log
