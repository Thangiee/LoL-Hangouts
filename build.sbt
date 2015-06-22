import android.Keys._

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

// dependencies for lolchat
libraryDependencies ++= Seq(
  "org.igniterealtime.smack" % "smack-tcp" % "4.1.1",
  "org.igniterealtime.smack" % "smack-core" % "4.1.1",
  "org.igniterealtime.smack" % "smack-extensions" % "4.1.1",
  "org.igniterealtime.smack" % "smack-android" % "4.1.1",
  "org.scalactic" % "scalactic_2.11" % "2.2.5"
)

// scala 3th party libraries
libraryDependencies ++= Seq(
  "org.scaloid" %% "scaloid" % "3.6.1-10",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "com.typesafe.play" % "play-json_2.11" % "2.4.0-M2",
  "org.scalaj" %% "scalaj-http" % "1.1.0",
  "com.github.nscala-time" %% "nscala-time" % "1.6.0"
)

// google billing
libraryDependencies ++= Seq(
  aar("com.google.android.gms" % "play-services" % "6.1.+"),
  aar("com.anjlab.android.iab.v3" % "library" % "1.0.+")
)

// android support libs
libraryDependencies ++= Seq(
  aar("com.android.support" % "recyclerview-v7" % "22.1.1"),
  aar("com.android.support" % "appcompat-v7" % "22.1.1"),
  "com.android.support" % "palette-v7" % "22.1.1",
  "com.android.support" % "support-v13" % "22.1.1",
  "com.google.code.findbugs" % "jsr305" % "3.0.0" // fix Missing dependency 'class javax.annotation.Nullable' for guava lib
)

// ========= android 3th party libs ==============

// androidViewAnimations
libraryDependencies ++= Seq(
  "com.nineoldandroids" % "library" % "2.4.0",
  aar("com.daimajia.easing" % "library" % "1.0.2"),
  aar("com.daimajia.androidanimations" % "library" % "1.1.3")
)

// cardsLib
libraryDependencies ++= Seq(
  aar("com.github.gabrielemariotti.cards" % "cardslib-core" % "2.1.0"),
  aar("com.github.gabrielemariotti.cards" % "cardslib-cards" % "2.1.0"),
  aar("com.github.gabrielemariotti.cards" % "cardslib-recyclerview" % "2.1.0"),
  aar("com.nhaarman.listviewanimations" % "lib-core" % "3.1.0")
)

libraryDependencies ++= Seq(
  aar("com.github.castorflex.smoothprogressbar"  % "library-circular"       % "1.1.0"),
  aar("it.neokree"                               % "MaterialTabs"           % "0.11"),
  aar("com.github.xiprox.errorview"              % "library"                % "2.2.0"),
  aar("com.nispok"                               % "snackbar"               % "2.7.4"), //todo: DEPRECATED
  aar("com.skocken"                              % "efficientadapter.lib"   % "1.2.0"),
  aar("com.github.traex.rippleeffect"            % "library"                % "1.3"),
  aar("com.github.gabrielemariotti.changeloglib" % "library"                % "1.5.2"),
  aar("com.afollestad"                           % "material-dialogs"       % "0.7.4.2"),
  aar("de.psdev.licensesdialog"                  % "licensesdialog"         % "1.5.0"),
  aar("com.makeramen"                            % "roundedimageview"       % "2.1.0"),
  aar("fr.nicolaspomepuy"                        % "discreetapprate"        % "1.0.5"),
  aar("com.pixplicity.easyprefs"                 % "library"                % "1.5"),
  aar("com.balysv.materialmenu"                  % "material-menu-toolbar"  % "1.5.4"),
  aar("com.github.dmytrodanylyk.circular-progress-button" % "library"       % "1.1.3"),
  aar("co.lemonlabs"                             % "expandable-button-menu" % "1.0.0"),
  aar("com.github.markushi"                      % "android-ui"             % "1.2"),
  aar("com.github.lecho"                         % "hellocharts-library"    % "1.5.5"),
  aar("jp.wasabeef"                              % "recyclerview-animators" % "1.2.0")
)

libraryDependencies ++= Seq(
  "com.github.amigold.fundapter2"     % "library"       % "1.01",
  "de.keyboardsurfer.android.widget"  % "crouton"       % "1.8.5",
  "org.jsoup"                         % "jsoup"         % "1.8.2",
  "ch.acra"                           % "acra"          % "4.6.2",
  "com.parse.bolts"                   % "bolts-android" % "1.2.0"
)

run <<= run in Android

install <<= install in Android
