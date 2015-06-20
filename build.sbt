import android.Keys._
import Dependencies._

android.Plugin.androidBuild

name := "LoL Hangouts"

scalaVersion := "2.11.6"

minSdkVersion in Android := "14"

platformTarget in Android := "android-22"

apkbuildExcludes in Android ++= Seq(
  "META-INF/notice.txt",
  "META-INF/license.txt",
  "META-INF/LICENSE",
  "META-INF/NOTICE",
  "META-INF/LICENSE.txt",
  "META-INF/NOTICE.txt"
)

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "smack repo" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "material-dialogs" at "https://dl.bintray.com/drummer-aidan/maven"

proguardOptions in Android ++= ProguardSettings.buildSettings

libraryDependencies ++= Seq(
  "org.igniterealtime.smack" % "smack-tcp" % "4.1.1",
  "org.igniterealtime.smack" % "smack-core" % "4.1.1",
  "org.igniterealtime.smack" % "smack-extensions" % "4.1.1",
  "org.igniterealtime.smack" % "smack-android" % "4.1.1",
  "org.scalactic" % "scalactic_2.11" % "2.2.5"
)

// core dependencies
libraryDependencies ++= appCompat ++ googleBilling ++ smack :+ scaloid :+ scalaLogging :+ playJson :+ scalajHttp

libraryDependencies ++= androidViewAnimations ++ cardsLib :+ smoothProgressBar :+ materialTabs :+ errorView :+
                        snackbar :+ efficientAdapter :+ rippleView :+ changeLogLib :+ materialDialog :+
                        licensesDialog :+ roundedImageView :+ discreetAppRate :+ easyPrefs :+ materialMenu :+
                        circularProgressBtn :+ funAdapter :+ crouton :+ jsoup :+ acra :+ expandableBtnMenu :+
                        nscalaTime :+ revealColorView :+ parse :+ helloChart :+ recyclerViewAnimator

run <<= run in Android

install <<= install in Android
