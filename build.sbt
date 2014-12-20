import android.Keys._

android.Plugin.androidBuild

name := "LoL Hangouts"

scalaVersion := "2.11.4"

minSdkVersion in Android := "14"

platformTarget in Android := "android-21"

proguardCache in Android ++= Seq(
  ProguardCache("org.scaloid") % "org.scaloid",
  ProguardCache("play") % "play"
)

apkbuildExcludes in Android ++= Seq(
  "META-INF/notice.txt",
  "META-INF/license.txt",
  "META-INF/LICENSE",
  "META-INF/NOTICE",
  "META-INF/LICENSE.txt",
  "META-INF/NOTICE.txt"
)

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

proguardOptions in Android ++= Seq(
  "-dontobfuscate",
  "-dontoptimize",
  "-keepattributes Signature",
  "-dontwarn scala.collection.**",
  "-dontwarn scala.collection.mutable.**",
  "-dontwarn scala.**",
  "-dontwarn com.squareup.okhttp.**",
  "-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry",
  "-dontwarn javax.xml.bind.DatatypeConverter",
  "-dontwarn javax.annotation.**",
  "-dontwarn javax.inject.**",
  "-dontwarn sun.misc.Unsafe",
  "-keep class com.thangiee.LoLHangouts.api.stats.RiotLiveStats",
  "-keep class org.scaloid.common.**",
  "-keep class org.jivesoftware.smack.** {*;}",
  "-keep class org.jsoup.Jsoup.** {*;}",
  "-keep class net.simonvt.menudrawer.** {*;}",
  "-keep class com.activeandroid.** {*;}",
  "-keep class com.thangiee.LoLWithFriends.** {*;}",
  "-keepclassmembers class com.thangiee.LoLHangouts.api.stats {*;}",
  "-keepclassmembers class ** {public void processHTML(**);}",
  "-keepclassmembers class ** {public static Document parse(**);}",
  "-keepclassmembers class ** {public void onEvent*(**);}")

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.3.3",
  "org.scalaj" %% "scalaj-http" % "0.3.16",
  "org.scaloid" %% "scaloid" % "3.6.1-10",
  "com.android.support" % "support-v13" % "19.+",
  "com.android.support" % "support-v4" % "19.+",
  "com.google.code.gson" % "gson" % "2.2.4",
  "com.github.amigold.fundapter2" % "library" % "1.01",
  "de.keyboardsurfer.android.widget" % "crouton" % "1.8.5",
  "com.github.nscala-time" %% "nscala-time" % "1.2.0",
  "org.jsoup" % "jsoup" % "1.7.3",
  "ch.acra" % "acra" % "4.5.0",
  aar("com.github.gabrielemariotti.changeloglib" % "library" % "1.5.1"),
  aar("com.github.gabrielemariotti.cards" % "library-extra" % "1.9.0"),
  aar("de.psdev.licensesdialog" % "licensesdialog" % "1.5.0"),
  aar("com.google.android.gms" % "play-services" % "5.0.+"),
  aar("com.anjlab.android.iab.v3" % "library" % "1.0.+"),
  aar("com.github.dmytrodanylyk.circular-progress-button" % "library" % "1.1.2"),
  aar("net.simonvt.menudrawer" % "menudrawer" % "3.0.6"),
  aar("info.hoang8f" % "android-segmented" % "1.0.2"),
  aar("com.makeramen" % "roundedimageview" % "1.3.0"),
  aar("com.pixplicity.easyprefs" % "library" % "1.3"),
  aar("fr.nicolaspomepuy" % "discreetapprate" % "1.0.5"),
  aar("com.github.johnkil.android-progressfragment" % "progressfragment-native" % "1.4.0"),
  aar("com.astuetz" % "pagerslidingtabstrip" % "1.0.1"))

run <<= run in Android

install <<= install in Android
