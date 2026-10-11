#!/bin/bash
set -e

PI="student@RaspberryPi.local"
REMOTE="/home/student/submarine-control"

echo "Building submarine-control..."
mvn clean package -DskipTests

echo "Creating Pi directories..."
ssh "$PI" "mkdir -p '$REMOTE/target'"

echo "Copying application JAR..."
scp target/submarine-control.jar "$PI:$REMOTE/target/"

echo "Copying Bash scripts..."
scp ./*.sh "$PI:$REMOTE/"

echo "Setting script permissions..."
ssh "$PI" "chmod +x '$REMOTE'/*.sh"

echo "Deployment complete."