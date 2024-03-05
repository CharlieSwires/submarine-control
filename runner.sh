# runner.sh
#!/bin/bash
echo "===================================================running=========================================="
#export GST_DEBUG=3
sudo pkill pigpiod
~/submarine-control/libcam.sh &
sudo java -jar ~/submarine-control/target/submarine-control.jar &

