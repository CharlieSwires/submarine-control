# runner.sh
#!/bin/bash
echo "===================================================runningHD=========================================="
#export GST_DEBUG=3
sudo pkill pigpiod
sudo raspi-config nonint do_i2c 0
sudo rfkill block 0
sudo export WATCHDOG_ENABLED="true"
~/submarine-control/libcamHD.sh &
sudo -E java -jar ~/submarine-control/target/submarine-control.jar &

