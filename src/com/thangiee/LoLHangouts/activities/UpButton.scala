package com.thangiee.LoLHangouts.activities

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.view.MenuItem

trait UpButton extends Activity {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    getActionBar.setDisplayHomeAsUpEnabled(true)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case android.R.id.home ⇒ NavUtils.navigateUpFromSameTask(this); true
      case _                 ⇒ super.onOptionsItemSelected(item)
    }
  }
}
