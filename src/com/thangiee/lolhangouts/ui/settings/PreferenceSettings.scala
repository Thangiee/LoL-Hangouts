package com.thangiee.lolhangouts.ui.settings

import java.util.concurrent.TimeUnit

import android.app.{AlarmManager, PendingIntent}
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.{Intent, SharedPreferences}
import android.os.{Bundle, SystemClock}
import android.preference.{PreferenceFragment, PreferenceManager}
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.ui.receivers.DeleteOldMsgReceiver
import com.thangiee.lolhangouts.ui.utils._
import org.scaloid.common.TagUtil

class PreferenceSettings extends PreferenceFragment with OnSharedPreferenceChangeListener with TagUtil {
  implicit private lazy val ctx = getActivity.getBaseContext

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    addPreferencesFromResource(R.xml.pref_settings)
  }

  override def onResume(): Unit = {
    super.onResume()
    PreferenceManager.getDefaultSharedPreferences(ctx).registerOnSharedPreferenceChangeListener(this)
  }

  override def onPause(): Unit = {
    super.onPause()
    PreferenceManager.getDefaultSharedPreferences(ctx).unregisterOnSharedPreferenceChangeListener(this)
  }

  override def onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String): Unit = {
    val HistoryKey = R.string.pref_history.r2String

    key match {
      case HistoryKey => onHistoryChanged(sharedPreferences.getString(key, "3 days"))
      case _          => info(s"[+] setting change $key:${sharedPreferences.getBoolean(key, false)}")
    }
  }

  private def onHistoryChanged(value: String): Unit = {
    val i = new Intent(ctx, classOf[DeleteOldMsgReceiver])
    lazy val p = PendingIntent.getBroadcast(ctx, 0, i, 0)
    var millis = TimeUnit.DAYS.toMillis(3)

    // get the milliseconds to be used to calculate which message to delete
    value match {
      case "1 day"  => millis = TimeUnit.DAYS.toMillis(1)
      case "3 days" => millis = TimeUnit.DAYS.toMillis(3)
      case "7 days" => millis = TimeUnit.DAYS.toMillis(7)
      case "never"  => alarmManager.cancel(p); info("[*] Preference-History changed to: never"); return
      case _        => warn("[!] No match for Preference-History. Setting value to 3 days.")
    }

    i.putExtra(DeleteOldMsgReceiver.TIME_KEY, millis)

    // check to delete old message base on the millis every 1 hours
    alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), TimeUnit.HOURS.toMillis(1), p)
    info("[*] Preference-History changed to: " + value)
  }
}
