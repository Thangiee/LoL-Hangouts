make:
	sbt android:package-release
	cp bin/LoL\ Hangouts-release.apk /home/thangiee/Data/Google\ Drive/LoL\ Hangouts-release.apk

clean:
	sbt clean
	make
	
install:
	adb install -r bin/LoL\ Hangouts-release.apk