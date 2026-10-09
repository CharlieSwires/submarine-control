#!/bin/bash
sudo apt update
sudo apt upgrade -y
sudo apt install git -y
sudo apt install default-jdk -y
sudo apt install vlc -y
git clone https://github.com/CharlieSwires/submarine-control

echo "========================================================"
echo "===================== FINISHED ========================="
echo "========================================================"
