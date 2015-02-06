package com.thangiee.lolhangouts.receivers

import android.content.{BroadcastReceiver, Context, Intent}
import com.thangiee.lolhangouts.data.repository.datasources.sqlite.DB
import com.thangiee.lolhangouts.utils.Logger._
import com.github.nscala_time.time.Imports._
import org.scaloid.common.TagUtil

class DeleteOldMsgReceiver extends BroadcastReceiver with TagUtil{
  override def onReceive(context: Context, intent: Intent): Unit = {
    val millis = intent.getLongExtra(DeleteOldMsgReceiver.TIME_KEY, 0)
    val then = DateTime.now - millis

    info("[*] Deleting old messages before: " + then)

    new Thread(new Runnable {
      override def run(): Unit = DB.getAllMessages.filter(_.date.before(then.date)).map(_.delete())
    }).start()
  }
}

object DeleteOldMsgReceiver {
  val TIME_KEY = "time_key"
}
