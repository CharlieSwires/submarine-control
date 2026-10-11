package snippet;

public class Snippet {
	#!/bin/bash
	set -e
	
	PI="student@raspberrypi.local"
	REMOTE="/home/student/submarine-control"
	
	echo "Building submarine-control..."
	mvn clean 
}

