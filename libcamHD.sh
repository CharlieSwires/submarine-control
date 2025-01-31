#!/bin/bash

# Function to clean up on exit
cleanup() {
    echo "Cleaning up..."
    pkill -P $$ # Kill all child processes
}

# Trap script exit for cleanup
trap cleanup EXIT

# Get the host IP address, assuming the first IP is the correct one
HOST_IP=$(hostname -I | cut -d' ' -f1)

# Export the HOST_IP variable if needed by other scripts or applications
export HOST_IP

libcamera-vid -t 0 --width 1920 --height 1080 --codec yuv420 --framerate 25 --inline --listen -o - --verbose=0| cvlc -vvv stream:///dev/stdin --sout "#transcode{vcodec=h264,vb=2000,scale=Auto,acodec=none}:rtp{sdp=rtsp://${HOST_IP}:8554/}" --demux=rawvideo --rawvid-fps=25 --rawvid-width=1920 --rawvid-height=1080 --rawvid-chroma=I420
