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
libcamera-vid -t 0 --width 1920 --height 1080 --codec yuv420 --framerate 25 --inline --listen -o - | cvlc -vvv stream:///dev/stdin --sout '#transcode{vcodec=h264,vb=800,scale=Auto,acodec=none}:rtp{sdp=rtsp://192.168.137.205:8554/}' --demux=rawvideo --rawvid-fps=25 --rawvid-width=1920 --rawvid-height=1080 --rawvid-chroma=I420
