#!/bin/bash

# Wait until Wi-Fi is connected
while ! ping -c 1 -W 1 8.8.8.8 > /dev/null 2>&1; do
    echo "Waiting for Wi-Fi connection..."
    sleep 5
done

# Execute your file (replace 'your_command_here' with the actual command)
echo "Wi-Fi connected. Running your file."
cd ~/submarine-control
./runnerHD.sh&
cd ~
