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

# Start libcamera-vid and pipe its output directly to the GStreamer pipeline
libcamera-vid -t 0 --width 1350 --height 760 --codec yuv420 --framerate 10 --inline --listen -o - --verbose=0 | cvlc -vvv stream:///dev/stdin --sout "#transcode{vcodec=h264,vb=1000,scale=Auto,acodec=none}:rtp{sdp=rtsp://${HOST_IP}:8554/}" --demux=rawvideo --rawvid-fps=10 --rawvid-width=640 --rawvid-height=480 --rawvid-chroma=I420
