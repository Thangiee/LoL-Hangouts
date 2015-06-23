package com.thangiee.lolhangouts.ui.utils.thirdpartylibsugar

import android.content.Context
import android.preference.PreferenceManager

trait PreferenceSugar {

  implicit class PreferenceSugar(resId: Int)(implicit ctx: Context) {
    private val key = ctx.getResources.getString(resId)
    private val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)

    def pref2Int(default: Int): Int = prefs.getString(key, default.toString).toInt
    def pref2Long(default: Long): Long = prefs.getString(key, default.toString).toLong
    def pref2Float(default: Float): Float = prefs.getString(key, default.toString).toFloat
    def pref2Boolean(default: Boolean = false): Boolean = prefs.getBoolean(key, default)
    def pref2String(default: String): String = prefs.getString(key, default)
  }
}

object PreferenceSugar extends PreferenceSugar
