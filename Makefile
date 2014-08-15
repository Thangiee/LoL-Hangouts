make:
	sbt android:package-release
	adb install -r bin/LoLWithFriends-release.apk   