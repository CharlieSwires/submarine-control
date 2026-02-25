# runner.sh
#!/bin/bash
echo "===================================================running=========================================="
#export GST_DEBUG=3
sudo pkill pigpiod
sudo raspi-config nonint do_i2c 0
sudo ifconfig wlan0 down
sudo export WATCHDOG_ENABLED="true"
#~/submarine-control/libcam.sh &
WIDTH=320 HEIGHT=240 FPS=25 ~/submarine-control/usb_mjpeg_rtsp.sh&
sudo -E java -jar ~/submarine-control/target/submarine-control.jar &

