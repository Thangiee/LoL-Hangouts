package com.thangiee.LoLHangouts.api

import java.net.SocketTimeoutException

import android.os.SystemClock
import com.jriot.main.JRiotException
import com.jriot.main.JRiotException._
import com.thangiee.LoLHangouts.MyApplication
import com.thangiee.LoLHangouts.utils.TLogger

import scala.util.{Failure, Success, Try}

object RiotApiCaller extends TLogger {
  def call[T](function: ⇒ T)(implicit app: MyApplication): Option[T] = {

    for (attempt ← 0 until Keys.keys.size) {
      val key = Keys.randomKey
      info("[*] Using Key: " + key)
      app.riot.setApiKey(key)

      Try(function) match {
        case Success(s) ⇒ return Some(s)

        case Failure(e) ⇒ e match {
          case e: JRiotException ⇒ e.getErrorCode match {
            case ERROR_API_KEY_LIMIT ⇒ app.riot.setApiKey(if (attempt == 5) Keys.masterKey else key)
            case ERROR_API_KEY_WRONG ⇒ warn("[!] API key gone bad: " + key)
            case ERROR_BAD_REQUEST   ⇒ throw ISE("Bad Request")
            case ERROR_INTERNAL_SERVER_ERROR ⇒ throw ISE("There is currently a problem with the server")
            case ERROR_DATA_NOT_FOUND ⇒ return None
            case ERROR_SERVICE_UNAVAILABLE ⇒ throw ISE("Service unavailable")
          }
          case e: SocketTimeoutException ⇒ throw new SocketTimeoutException("Connection time out. Try refreshing.")
          case _ ⇒ warn(e.getMessage); SystemClock.sleep(500)
        }
      }
    }
    throw ISE("Service is currently unavailable. Please try again later!")
  }

  private def ISE(msg: String) = new IllegalStateException(msg)
}
