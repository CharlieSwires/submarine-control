# runner.sh
#!/bin/bash
echo "===================================================running=========================================="
#export GST_DEBUG=3
sudo pkill pigpiod
sudo raspi-config nonint do_i2c 0
~/submarine-control/libcam.sh &
sudo -E java -jar ~/submarine-control/target/submarine-control.jar &

