package com.thangiee.LoLHangouts.activities

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem

trait UpButton extends Activity {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    getActionBar.setDisplayHomeAsUpEnabled(true)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case android.R.id.home ⇒ this.finish(); true
      case _                 ⇒ super.onOptionsItemSelected(item)
    }
  }
}
