make:
	sbt android:package 
	adb install -r bin/LoLWithFriends-debug.apk   