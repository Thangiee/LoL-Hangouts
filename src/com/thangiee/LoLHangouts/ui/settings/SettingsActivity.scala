package com.thangiee.LoLHangouts.ui.settings

import android.os.Bundle
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.ui.core.{TActivity, UpButton}

class SettingsActivity extends TActivity with UpButton {
  override def layoutId(): Int = R.layout.act_settings_screen

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    getFragmentManager.beginTransaction().replace(R.id.content, new PreferenceSettings()).commit()
  }
}
