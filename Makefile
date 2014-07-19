make:
	sbt clean
	sbt android:package 
	adb install -r bin/LoLWithFriends-debug.apk   