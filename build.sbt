import android.Keys._

android.Plugin.androidBuild

name := "LoLWithFriends"

scalaVersion := "2.11.0"

minSdkVersion in Android := "14"

platformTarget in Android := "android-19"

proguardCache in Android ++= Seq(
  ProguardCache("org.scaloid") % "org.scaloid"
)

proguardOptions in Android ++= Seq("-dontobfuscate", "-dontoptimize", "-dontwarn scala.collection.mutable.**",
  "-dontwarn scala.**", "-keep class org.scaloid.common.**",
  "-dontwarn com.squareup.okhttp.**",
  "-keep class org.jivesoftware.smack.** {*;}",
  "-keep class com.nostra13.universalimageloader.** {*;}",
  "-keep class net.simonvt.menudrawer.** {*;}",
  "-keep class com.activeandroid.** {*;}",
  "-keepclassmembers class ** {public void onEvent*(**);}")

libraryDependencies ++= Seq(
  "org.scaloid" %% "scaloid" % "3.4-10",
  "org.scaloid" %% "scaloid-support-v4" % "3.4-10",
  "org.scala-lang" % "scala-xml" % "2.11.0-M4",
  "com.android.support" % "support-v4" % "13.0.0",
  "com.google.code.gson" % "gson" % "2.2.4",
  "com.github.amigold.fundapter2" % "library" % "1.01",
  "de.keyboardsurfer.android.widget" % "crouton" % "1.8.4",
  "de.greenrobot" % "eventbus" % "2.2.1",
  aar("com.github.dmytrodanylyk.circular-progress-button" % "library" % "1.0.5"),
  aar("net.simonvt.menudrawer" % "menudrawer" % "3.0.6"),
  aar("com.github.gabrielemariotti.cards" % "library-extra" % "1.8.0"))

run <<= run in Android

install <<= install in Android
