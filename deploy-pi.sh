#!/usr/bin/bash
set -e

PI="student@192.168.137.100"
REMOTE="/home/student/submarine-control"
SCP="/c/Windows/System32/OpenSSH/scp.exe"

echo "Building submarine-control..."
mvn clean package -DskipTests

echo "Creating Pi directories..."
ssh "$PI" "mkdir -p '$REMOTE/target'"

echo "Copying application JAR..."
"$SCP" target/submarine-control.jar "$PI:$REMOTE/target/"

echo "Copying Bash scripts..."
"$SCP" ./*.sh "$PI:$REMOTE/"

echo "Setting script permissions..."
ssh "$PI" "chmod +x '$REMOTE'/*.sh"

echo "Deployment complete."
