package com.thangiee.LoLHangouts.api.stats

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}

import scala.util.{Failure, Success, Try}

trait Parsing {
  protected val baseServerUrl: String
  val url: String
  var doc: Document

  protected def fetchDocument: Document = {
    val ISE = new IllegalStateException("Service is currently unavailable. Please try again later!")

    // do multiple attempts to get the document(aka html stuff)
    for (attempt ← 1 to 5) {
      println("[*] Attempt " + attempt + "|Connecting to: " + url)
      Try(Jsoup.connect(url).timeout(5000).get()) match {
        case Success(respond) ⇒    // got respond from website
          if (!respond.text().contains("currently unavailable")) {  // website respond with it been busy
            return respond
          } else if (attempt == 5) {  // all atempts used up
            throw ISE
          } else {
            Thread.sleep(150)
          }
        case Failure(e) ⇒ if (attempt == 5) throw e // if no respond, keep trying
      }
    }
    throw ISE
  }

  protected def parse(pattern: String): Try[String] = {
    Try(doc.select(pattern).first().text)
  }

  protected def parse(pattern: String, html: Element): Try[String] = {
    Try(html.select(pattern).first().text)
  }

  protected def getNumber[T: NumberOp](s: String): Try[T] = {
    val token = "[-0-9,/.]+".r
    Try(implicitly[NumberOp[T]].op(token.findFirstIn(s).get.replace(",", "")))
  }

  protected case class NumberOp[T](op: String => T)
  protected implicit val getInt = NumberOp[Int](_.toInt)
  protected implicit val getDouble = NumberOp[Double](_.toDouble)
}
