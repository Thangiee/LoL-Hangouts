package com.thangiee.LoLWithFriends.activities

import android.os.Bundle
import com.thangiee.LoLWithFriends.R
import com.thangiee.LoLWithFriends.fragments.ChatScreenFragment
import net.simonvt.menudrawer.MenuDrawer.Type
import net.simonvt.menudrawer.{MenuDrawer, Position}
import org.scaloid.common.SActivity

class MainActivity extends SActivity {

  protected override def onCreate(b: Bundle): Unit = {
    super.onCreate(b)
    setContentView(R.layout.main_screen)

    val sideDrawer = MenuDrawer.attach(this, Type.OVERLAY, Position.LEFT)
    sideDrawer.setContentView(R.layout.main_screen)
    sideDrawer.setMenuView(R.layout.side_menu)

    getFragmentManager.beginTransaction().add(R.id.screen_container, new ChatScreenFragment).commit()
  }
}
