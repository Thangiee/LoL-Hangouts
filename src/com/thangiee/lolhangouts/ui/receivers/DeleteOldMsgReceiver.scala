package com.thangiee.lolhangouts.ui.receivers

import android.content.{BroadcastReceiver, Context, Intent}
import com.github.nscala_time.time.Imports._
import com.thangiee.lolhangouts.data.datasources.sqlite.DB
import com.thangiee.lolhangouts.ui.utils.Logger._
import org.scaloid.common.TagUtil

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

class DeleteOldMsgReceiver extends BroadcastReceiver with TagUtil{
  override def onReceive(context: Context, intent: Intent): Unit = {
    val millis = intent.getLongExtra(DeleteOldMsgReceiver.TIME_KEY, 0)
    val then = DateTime.now - millis

    Future {
      info("[*] Deleting old messages before: " + then)
      Try(DB.getAllMessages.filter(_.date.before(then.date)).foreach(_.delete()))
    }
  }
}

object DeleteOldMsgReceiver {
  lazy val TIME_KEY = "time_key"
}
