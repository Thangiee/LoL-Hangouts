package com.thangiee.LoLHangouts.activities

import android.content.{Context, Intent}
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import com.pixplicity.easyprefs.library.Prefs
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.fragments.ChatPaneFragment

class QuickChatActivity extends TActivity with UpButton with Ads {
  override lazy val layout: ViewGroup = find[LinearLayout](R.id.linear_layout)
  override val AD_UNIT_ID: String    = "ca-app-pub-4297755621988601/3016063176"

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.layout_with_container)
    val friendName = getIntent.getStringExtra("name-key")

    val chatFrag = ChatPaneFragment(friendName)
    getFragmentManager.beginTransaction().replace(R.id.container, chatFrag).commit()

    if (Prefs.getBoolean("is_ads_enable", true)) setupAds()
  }
}

object QuickChatActivity extends TIntent {
  def apply(name: String)(implicit ctx: Context): Intent = {
    new Intent(ctx, classOf[QuickChatActivity]).args("name-key" -> name)
  }
}
