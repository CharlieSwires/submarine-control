# runner.sh
#!/bin/bash
echo "===================================================runningHD=========================================="
#export GST_DEBUG=3
sudo pkill pigpiod
sudo raspi-config nonint do_i2c 0
~/submarine-control/libcamHD.sh &
sudo java -jar ~/submarine-control/target/submarine-control.jar &

