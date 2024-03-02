# runner.sh
#!/bin/bash
echo "===================================================running=========================================="
#export GST_DEBUG=3
~/submarine-control/libcam.sh &
java -jar ~/submarine-control/target/submarine-control.jar &

