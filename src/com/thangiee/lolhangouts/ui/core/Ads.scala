package com.thangiee.lolhangouts.ui.core

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams._
import com.google.android.gms.ads.{AdRequest, AdSize, AdView}

trait Ads extends TActivity {
  private lazy val adView = new AdView(ctx)

  def adUnitId: String
  def adLayout: ViewGroup

  def setupAds(): Unit = { 
    adView.setAdSize(AdSize.SMART_BANNER)
    adView.setAdUnitId(adUnitId)

    val params = new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
    adLayout.addView(adView, params)

    val adRequest = new AdRequest.Builder()
      .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
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
