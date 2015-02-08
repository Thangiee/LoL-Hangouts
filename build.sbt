import android.Keys._
import Dependencies._

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

proguardOptions in Android ++= ProguardSettings.buildSettings

// core dependencies
libraryDependencies ++= appCompat ++ googleBilling :+ scaloid :+ scalaLogging :+ playJson :+ scalajHttp

libraryDependencies ++= androidViewAnimations ++ cardsLib :+ smoothProgressBar :+ materialTabs :+ errorView :+
                        snackbar :+ efficientAdapter :+ materialDialog :+ rippleView :+ changeLogLib :+
                        licensesDialog :+ roundedImageView :+ discreetAppRate :+ easyPrefs :+ materialMenu :+
                        circularProgressBtn :+ expandableBtnMenu :+ funAdapter :+ crouton :+ jsoup :+ acra :+
                        nscalaTime :+ revealColorView

run <<= run in Android

install <<= install in Android
