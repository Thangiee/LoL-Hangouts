import android.Keys._

android.Plugin.androidBuild

name := "LoLWithFriends"

scalaVersion := "2.11.2"

minSdkVersion in Android := "14"

platformTarget in Android := "android-19"

proguardCache in Android ++= Seq(
  ProguardCache("org.scaloid") % "org.scaloid"
)

apkbuildExcludes in Android ++= Seq(
  "META-INF/notice.txt",
  "META-INF/license.txt",
  "META-INF/LICENSE",
  "META-INF/NOTICE"
)

proguardOptions in Android ++= Seq("-dontobfuscate", "-dontoptimize", "-dontwarn scala.collection.mutable.**",
  "-dontwarn scala.**", "-keep class org.scaloid.common.**",
  "-dontwarn com.squareup.okhttp.**",
  "-keep class org.jivesoftware.smack.** {*;}",
  "-keep class org.jsoup.Jsoup.** {*;}",
  "-keep class com.nostra13.universalimageloader.** {*;}",
  "-keep class net.simonvt.menudrawer.** {*;}",
  "-keep class com.activeandroid.** {*;}",
  "-keepclassmembers class ** {public void onEvent*(**);}")

libraryDependencies ++= Seq(
  "org.scaloid" %% "scaloid" % "3.4-10",
//  "org.scala-lang" % "scala-xml" % "2.11.0-M4",
  "com.android.support" % "support-v13" % "13.0.0",
  "com.android.support" % "support-v4" % "13.0.0",
  "com.google.code.gson" % "gson" % "2.2.4",
  "com.github.amigold.fundapter2" % "library" % "1.01",
  "de.keyboardsurfer.android.widget" % "crouton" % "1.8.4",
  "de.greenrobot" % "eventbus" % "2.2.1",
  "com.github.nscala-time" %% "nscala-time" % "1.2.0",
  "org.jsoup" % "jsoup" % "1.7.3",
  aar("com.github.dmytrodanylyk.circular-progress-button" % "library" % "1.1.0"),
  aar("net.simonvt.menudrawer" % "menudrawer" % "3.0.6"),
  aar("com.github.gabrielemariotti.cards" % "library-extra" % "1.8.0"),
  aar("info.hoang8f" % "android-segmented" % "1.0.2"),
  aar("com.makeramen" % "roundedimageview" % "1.3.0"),
  aar("com.pixplicity.easyprefs" % "library" % "1.3"),
  aar("com.astuetz" % "pagerslidingtabstrip" % "1.0.1"))

run <<= run in Android

install <<= install in Android
