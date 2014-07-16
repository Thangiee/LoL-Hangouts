import android.Keys._

android.Plugin.androidBuild

name := "LoLWithFriends"

scalaVersion := "2.11.0"

minSdkVersion in Android := 14

platformTarget in Android := "android-19"

proguardCache in Android ++= Seq(
  ProguardCache("org.scaloid") % "org.scaloid"
)

proguardOptions in Android ++= Seq("-dontobfuscate", "-dontoptimize", "-dontwarn scala.collection.mutable.**")

libraryDependencies ++= Seq(
  "org.scaloid" %% "scaloid" % "3.4-10",
  "org.scaloid" %% "scaloid-support-v4" % "3.4-10",
  "com.android.support" % "support-v4" % "13.0.0",
  "com.google.code.gson" % "gson" % "2.2.4")

scalacOptions in Compile += "-feature"

run <<= run in Android

install <<= install in Android
