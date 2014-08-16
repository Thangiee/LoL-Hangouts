make:
	sbt android:package-release
	cp bin/LoLWithFriends-release.apk /home/thangiee/Data/Google\ Drive/LoLWithFriends-release.apk

clean:
	sbt clean
	make
	
install:
	adb install -r bin/LoLWithFriends-release.apk