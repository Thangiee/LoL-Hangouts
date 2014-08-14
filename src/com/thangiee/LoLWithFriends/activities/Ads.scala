package com.thangiee.LoLWithFriends.activities

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams._
import com.google.android.gms.ads.{AdRequest, AdSize, AdView}
import org.scaloid.common.SActivity

trait Ads extends SActivity {
  private lazy val adView = new AdView(ctx)
  private val AD_UNIT_ID = "ca-app-pub-4297755621988601/1349577574"
  val layout: ViewGroup

 def setupAds(params: ViewGroup.LayoutParams = new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)): Unit = {
    adView.setAdSize(AdSize.BANNER)
    adView.setAdUnitId(AD_UNIT_ID)

   layout.addView(adView, params)

    val adRequest = new AdRequest.Builder()
      .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
      .addTestDevice("6D8906D4C1230F9D8AA43AFD76C75B9D")
      .build()

    adView.loadAd(adRequest)
  }

  override def onResume(): Unit = {
    super.onResume()
    if (adView != null) adView.resume()
  }

  override def onPause(): Unit = {
    if (adView != null) adView.pause()
    super.onPause()
  }

  override def onDestroy(): Unit = {
    if (adView != null) adView.destroy()
    super.onDestroy()
  }
}
