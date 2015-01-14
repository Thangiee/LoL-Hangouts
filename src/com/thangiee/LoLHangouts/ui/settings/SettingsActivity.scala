package com.thangiee.LoLHangouts.ui.settings

import android.os.Bundle
import com.balysv.materialmenu.MaterialMenuDrawable
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.activities.TActivity
import com.thangiee.LoLHangouts.utils._

class SettingsActivity extends TActivity {
  override def layoutId(): Int = R.layout.act_settings_screen

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    navIcon.setIconState(MaterialMenuDrawable.IconState.ARROW)
    toolbar.setNavigationOnClickListener(finish())
    getFragmentManager.beginTransaction().replace(R.id.content, new PreferenceSettings()).commit()
  }
}
