#!/bin/bash
sudo apt update
sudo apt upgrade -y
sudo apt install git -y
git clone https://github.com/CharlieSwires/submarine-control
sudo apt install default-jdk -y
sudo apt install vlc -y

echo "========================================================"
echo "===================== FINISHED ========================="
echo "========================================================"
