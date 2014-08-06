package com.thangiee.LoLWithFriends.activities

import android.os.Bundle
import android.preference.PreferenceActivity
import com.thangiee.LoLWithFriends.R

class PreferenceSettings extends PreferenceActivity with UpButton {

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    addPreferencesFromResource(R.xml.pref_notification)
  }
}
