package com.thangiee.LoLHangouts.activities

import android.os.Bundle
import com.balysv.materialmenu.MaterialMenuDrawable
import com.thangiee.LoLHangouts.utils._

trait UpButton extends TActivity {
  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    navIcon.setIconState(MaterialMenuDrawable.IconState.ARROW)
    toolbar.setNavigationOnClickListener(finish())
  }
}
