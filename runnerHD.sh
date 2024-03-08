# runner.sh
#!/bin/bash
echo "===================================================runningHD=========================================="
#export GST_DEBUG=3
sudo pkill pigpiod
~/submarine-control/libcamHD.sh &
sudo java -jar ~/submarine-control/target/submarine-control.jar &

