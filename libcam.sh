#!/bin/bash

# Function to clean up on exit
cleanup() {
    echo "Cleaning up..."
    pkill -P $$ # Kill all child processes
}

# Trap script exit for cleanup
trap cleanup EXIT

# Start libcamera-vid and pipe its output directly to the GStreamer pipeline
#libcamera-vid -t 0 --width 720 --height 576 --codec h264 --framerate 10 --inline --listen -o - --bitrate=1000000 awb-mode=tungsten preview=false | \
#gst-launch-1.0 fdsrc ! h264parse ! rtph264pay name=pay0 pt=96 ! udpsink host=192.168.137.205 port=8554
libcamera-vid -t 0 --width 640 --height 480 --codec yuv420 --framerate 10 --inline --listen -o - | cvlc -vvv stream:///dev/stdin --sout '#transcode{vcodec=h264,vb=800,scale=Auto,acodec=none}:rtp{sdp=rtsp://192.168.137.205:8554/}' --demux=rawvideo --rawvid-fps=10 --rawvid-width=640 --rawvid-height=480 --rawvid-chroma=I420 > /dev/null 2>&1
