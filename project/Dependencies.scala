import android.Keys._
import sbt._

object Dependencies {
  val smoothProgressBar   = aar("com.github.castorflex.smoothprogressbar" % "library-circular" % "1.0.2")
  val materialTabs        = aar("it.neokree" % "MaterialTabs" % "0.11")
  val errorView           = aar("com.github.xiprox.errorview" % "library" % "1.0.0")
  val snackbar            = aar("com.nispok" % "snackbar" % "2.7.4")
  val materialEditText    = aar("com.rengwuxian.materialedittext" % "library" % "1.8.2")
  val efficientAdapter    = aar("com.skocken" % "efficientadapter.lib" % "1.2.+")
  val materialDialog      = aar("com.afollestad" % "material-dialogs" % "0.6.1.6")
  val rippleView          = aar("com.github.traex.rippleeffect" % "library" % "1.2.4")
  val changeLogLib        = aar("com.github.gabrielemariotti.changeloglib" % "library" % "1.5.1")
  val licensesDialog      = aar("de.psdev.licensesdialog" % "licensesdialog" % "1.5.0")
  val roundedImageView    = aar("com.makeramen" % "roundedimageview" % "1.3.0")
  val discreetAppRate     = aar("fr.nicolaspomepuy" % "discreetapprate" % "1.0.5")
  val easyPrefs           = aar("com.pixplicity.easyprefs" % "library" % "1.3")
  val materialMenu        = aar("com.balysv.materialmenu" % "material-menu-toolbar" % "1.5.0")
  val circularProgressBtn = aar("com.github.dmytrodanylyk.circular-progress-button" % "library" % "1.1.2")
  val expandableBtnMenu   = aar("co.lemonlabs" % "expandable-button-menu" % "1.0.0")
  val revealColorView     = aar("com.github.markushi" % "android-ui" % "1.2")
  val helloChart          = aar("com.github.lecho" % "hellocharts-library" % "1.5.4")
  val recyclerViewAnimator = aar("jp.wasabeef" % "recyclerview-animators" % "1.1.+")

  val funAdapter   = "com.github.amigold.fundapter2" % "library" % "1.01"
  val crouton      = "de.keyboardsurfer.android.widget" % "crouton" % "1.8.5"
  val jsoup        = "org.jsoup" % "jsoup" % "1.7.3"
  val acra         = "ch.acra" % "acra" % "4.5.0"
  val nscalaTime   = "com.github.nscala-time" %% "nscala-time" % "1.6.0"
  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"
  val scaloid      = "org.scaloid" %% "scaloid" % "3.6.1-10"
  val playJson     = "com.typesafe.play" % "play-json_2.11" % "2.4.0-M2"
  val scalajHttp   = "org.scalaj" %% "scalaj-http" % "1.1.0"
  val parse        = "com.parse.bolts" % "bolts-android" % "1.+"


  val googleBilling = Seq(
    aar("com.google.android.gms" % "play-services" % "6.1.+"),
    aar("com.anjlab.android.iab.v3" % "library" % "1.0.+")
  )

  val androidViewAnimations = Seq(
    "com.nineoldandroids" % "library" % "2.4.0",
    aar("com.daimajia.easing" % "library" % "1.0.1"),
    aar("com.daimajia.androidanimations" % "library" % "1.1.3")
  )

  val cardsLib = Seq(
    aar("com.github.gabrielemariotti.cards" % "cardslib-core" % "2.0.1"),
    aar("com.github.gabrielemariotti.cards" % "cardslib-cards" % "2.0.1"),
    aar("com.github.gabrielemariotti.cards" % "cardslib-recyclerview" % "2.0.1"),
    aar("com.nhaarman.listviewanimations" % "lib-core" % "3.1.0")
  )

  val appCompat = Seq(
    aar("com.android.support" % "recyclerview-v7" % "22.+"),
    aar("com.android.support" % "appcompat-v7" % "22.+"),
    "com.android.support" % "palette-v7" % "22.+",
    "com.android.support" % "support-v13" % "22.+",
    "com.google.code.findbugs" % "jsr305" % "3.0.0" // fix Missing dependency 'class javax.annotation.Nullable' for guava lib
  )
}