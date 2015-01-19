package com.thangiee.LoLHangouts.activities

import android.content.{Context, Intent}
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.ui.profile.ProfileContainer

case class ViewProfileActivity() extends TActivity with UpButton with Ads {
  override lazy val adsLayout : ViewGroup = find[LinearLayout](R.id.linear_layout)
  override      val AD_UNIT_ID: String    = "ca-app-pub-4297755621988601/4576755572"
  override      val layoutId              = R.layout.act_with_container

  lazy val contentContainer = find[LinearLayout](R.id.content_container)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    val summonerName = getIntent.getStringExtra("name-key")
    val regionId = getIntent.getStringExtra("region-key")

    contentContainer.addView(new ProfileContainer(summonerName, regionId).getView)

//    if (Prefs.getBoolean("is_ads_enable", true)) setupAds()
  }
}

object ViewProfileActivity extends TIntent {
  def apply(name: String, regionId: String)(implicit ctx: Context): Intent = {
    new Intent(ctx, classOf[ViewProfileActivity]).args("name-key" → name, "region-key" → regionId)
  }
}
