package com.thangiee.lolhangouts.ui.core

import android.os.Bundle
import com.balysv.materialmenu.MaterialMenuDrawable
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.ui.utils._

trait UpButton extends TActivity {
  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    overridePendingTransition(R.anim.right_slide_in, R.anim.stay_still)
    navIcon.setIconState(MaterialMenuDrawable.IconState.ARROW)
    toolbar.setNavigationOnClickListener(finish())
  }

  override def finish(): Unit = {
    super.finish()
    overridePendingTransition(0, R.anim.right_slide_out)
  }
}
