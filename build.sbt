import android.Keys._

android.Plugin.androidBuild

name := "LoL Hangouts"

scalaVersion := "2.11.7"

minSdkVersion in Android := "14"

platformTarget in Android := "android-22"

apkbuildExcludes in Android ++=
  "META-INF/notice.txt" ::
  "META-INF/license.txt" ::
  "META-INF/LICENSE" ::
  "META-INF/NOTICE" ::
  "META-INF/LICENSE.txt" ::
  "META-INF/NOTICE.txt" ::
  Nil

dexMaxHeap in Android := "2048m"

resolvers ++=
  ("Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/") ::
  ("jcenter" at "https://jcenter.bintray.com/") :: Nil

proguardOptions in Android ++= ProguardSettings.buildSettings

proguardCache in Android ++= ProguardSettings.cacheValues

javacOptions ++= "-source" :: "1.7" :: "-target" :: "1.7" :: Nil

libraryDependencies ++= Seq(
  "org.igniterealtime.smack" % "smack-android" % "4.1.3",
  "com.github.thangiee" %% "league-of-legend-chat-lib-scala" % "0.2.1"
).map(_.exclude("xpp3", "xpp3"))

libraryDependencies ++= List(
  "com.github.cb372" %% "scalacache-guava" % "0.6.4",
  "org.scalactic" % "scalactic_2.11" % "2.2.5"
)

// scala 3th party libraries
libraryDependencies ++= Seq(
  "org.scaloid" %% "scaloid" % "4.0-RC1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "com.typesafe.play" % "play-json_2.11" % "2.4.0-M2",
  "org.scalaj" %% "scalaj-http" % "1.1.5",
  "com.github.nscala-time" %% "nscala-time" % "1.6.0"
)

// google billing
libraryDependencies ++= Seq(
  aar("com.google.android.gms" % "play-services-ads" % "7.+"),
  aar("com.anjlab.android.iab.v3" % "library" % "1.0.+")
)

// android support libs
libraryDependencies ++= Seq(
//  aar("com.android.support" % "multidex" % "1.0.+"),
  aar("com.android.support" % "recyclerview-v7" % "22.2.1"),
  aar("com.android.support" % "appcompat-v7" % "22.2.1"),
  aar("com.android.support" % "design" %"22.2.1"),
  "com.android.support" % "palette-v7" % "22.2.1",
  "com.android.support" % "support-v13" % "22.2.1",
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
  aar("com.skocken"                              % "efficientadapter.lib"   % "1.2.0"),
  aar("com.github.traex.rippleeffect"            % "library"                % "1.3"),
  aar("com.github.gabrielemariotti.changeloglib" % "library"                % "1.5.2"),
  aar("com.afollestad"                           % "material-dialogs"       % "0.7.4.2"),
  aar("de.psdev.licensesdialog"                  % "licensesdialog"         % "1.5.0"),
  aar("com.makeramen"                            % "roundedimageview"       % "2.1.0"),
  aar("fr.nicolaspomepuy"                        % "discreetapprate"        % "2.0.3"),
  aar("com.pixplicity.easyprefs"                 % "library"                % "1.5"),
  aar("com.balysv.materialmenu"                  % "material-menu-toolbar"  % "1.5.4"),
  aar("com.github.dmytrodanylyk.circular-progress-button" % "library"       % "1.1.3"),
  aar("co.lemonlabs"                             % "expandable-button-menu" % "1.0.0"),
  aar("com.github.markushi"                      % "android-ui"             % "1.2"),
  aar("com.github.lecho"                         % "hellocharts-library"    % "1.5.5"),
  aar("jp.wasabeef"                              % "recyclerview-animators" % "1.2.0"),
  aar("com.rengwuxian.materialedittext"          % "library"                % "2.1.4"),
  aar("com.github.medyo"                         %"fancybuttons"            % "1.3"),
  aar("com.github.clans"                         % "fab"                    % "1.5.3")
)

libraryDependencies ++= Seq(
  "com.github.amigold.fundapter2"     % "library"       % "1.01",
  "de.keyboardsurfer.android.widget"  % "crouton"       % "1.8.5",
  "org.jsoup"                         % "jsoup"         % "1.8.2",
  "ch.acra"                           % "acra"          % "4.6.2",
  "com.parse.bolts"                   % "bolts-android" % "1.2.0",
  "de.greenrobot"                     % "eventbus"      % "2.4.0",
  "com.squareup.picasso"              % "picasso"       % "2.5.2"
)

run <<= run in Android

install <<= install in Android

retrolambdaEnable in Android := false // turning it on significantly increases the build time