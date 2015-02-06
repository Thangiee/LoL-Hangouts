package com.thangiee.lolhangouts.ui.livegame

import android.content.{Context, Intent}
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import com.pixplicity.easyprefs.library.Prefs
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.ui.core.{TActivity, TIntent, UpButton, Ads}

class ViewLiveGameStatsActivity extends TActivity with UpButton with Ads {
  override lazy val adsLayout : ViewGroup = find[LinearLayout](R.id.ads_holder)
  override      val AD_UNIT_ID: String    = "ca-app-pub-4297755621988601/7264366775"
  override      val layoutId              = R.layout.act_with_container

  lazy val contentContainer = find[LinearLayout](R.id.content_container)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    val summonerName = getIntent.getStringExtra("name-key")
    val regionId = getIntent.getStringExtra("region-key")

    contentContainer.addView(new LiveGameContainer(summonerName, regionId).getView)

    if (Prefs.getBoolean("is_ads_enable", true)) setupAds()
  }
}

object ViewLiveGameStatsActivity extends TIntent {
  def apply(name: String, regionId: String)(implicit ctx: Context): Intent = {
    new Intent(ctx, classOf[ViewLiveGameStatsActivity]).args("name-key" → name, "region-key" → regionId)
  }
}
