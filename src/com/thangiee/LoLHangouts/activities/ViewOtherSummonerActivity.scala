package com.thangiee.LoLHangouts.activities

import android.content.{Context, Intent}
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import com.pixplicity.easyprefs.library.Prefs
import com.thangiee.LoLHangouts.R

case class ViewOtherSummonerActivity() extends TActivity with UpButton with Ads {
  override lazy val adsLayout : ViewGroup = find[LinearLayout](R.id.linear_layout)
  override      val AD_UNIT_ID: String    = "ca-app-pub-4297755621988601/4576755572"
  override      val layoutId              = R.layout.layout_with_container

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)

    val summonerName = getIntent.getStringExtra("name-key")
    val region = getIntent.getStringExtra("region-key")

    val fragment = null
    getFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()

    if (Prefs.getBoolean("is_ads_enable", true)) setupAds()
  }
}

object ViewOtherSummonerActivity extends TIntent {
  def apply(name: String, region: String)(implicit ctx: Context): Intent = {
    new Intent(ctx, classOf[ViewOtherSummonerActivity]).args("name-key" → name, "region-key" → region)
  }
}
