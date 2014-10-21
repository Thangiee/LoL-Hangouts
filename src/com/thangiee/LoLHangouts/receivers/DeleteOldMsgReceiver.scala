package com.thangiee.LoLHangouts.receivers

import android.content.{BroadcastReceiver, Context, Intent}
import com.thangiee.LoLHangouts.utils.{Logger, DB}
import com.github.nscala_time.time.Imports._

class DeleteOldMsgReceiver extends BroadcastReceiver with Logger {
  override def onReceive(context: Context, intent: Intent): Unit = {
    val millis = intent.getLongExtra(DeleteOldMsgReceiver.TIME_KEY, 0)
    val then = DateTime.now - millis

    info("[*] Deleting old messages before: " + then)

    new Thread(new Runnable {
      override def run(): Unit = DB.getAllMessages.filter(_.getDate.before(then.date)).map(_.delete())
    }).start()
  }
}

object DeleteOldMsgReceiver {
  val TIME_KEY = "time_key"
}
