#!/bin/bash

# Function to clean up on exit
cleanup() {
    echo "Cleaning up..."
    pkill -P $$ # Kill all child processes
}

# Trap script exit for cleanup
trap cleanup EXIT

# Start libcamera-vid and pipe its output directly to the GStreamer pipeline
libcamera-vid -t 0 --width 640 --height 480 --codec h264 --framerate 30 --inline --listen -o - --bitrate=8000000 awb-mode=tungsten preview=false | \
gst-launch-1.0 fdsrc ! h264parse ! rtph264pay name=pay0 pt=96 ! udpsink host=192.168.137.205 port=8554
