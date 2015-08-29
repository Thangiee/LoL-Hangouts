package com.thangiee.lolhangouts.ui.core

import android.view.{Menu, MenuItem}

trait TActivityContainer extends TActivity {
  protected def container: Container

  override def onPrepareOptionsMenu(menu: Menu): Boolean = {
    if (container.onPrepareOptionsMenu(menu)) true
    else super.onPrepareOptionsMenu(menu)
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    if (container.onCreateOptionsMenu(getMenuInflater, menu)) true
    else super.onCreateOptionsMenu(menu)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    if (container.onOptionsItemSelected(item)) true
    else super.onOptionsItemSelected(item)
  }
}
