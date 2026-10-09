#!/bin/bash

#==============================================================
# copy this file to the Raspberry Pi's ~ then run and have a
# cuppa. Should update the OS and install everything you need.
# might need: chmod +x setup.sh
# > sudo ./setup.sh
#==============================================================

sudo apt update
sudo apt upgrade -y
sudo apt install git -y
git clone https://github.com/CharlieSwires/submarine-control
sudo apt install default-jdk -y
sudo apt install vlc -y

echo "========================================================"
echo "===================== FINISHED ========================="
echo "========================================================"
