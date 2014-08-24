package com.thangiee.LoLHangouts.api.stats

import org.jsoup.nodes.{Document, Element}

import scala.util.Try

trait Parsing {
  protected val baseServerUrl: String
  val url: String
  val doc: Document

  protected def fetchDocument: Document

  protected def parse(pattern: String): Try[String] = {
    Try(doc.select(pattern).first().text)
  }

  protected def parse(pattern: String, html: Element): Try[String] = {
    Try(html.select(pattern).first().text)
  }

  protected def getNumber[T: NumberOp](s: String): Try[T] = {
    val token = "[-0-9,/.]+".r
    Try(implicitly[NumberOp[T]].op(token.findFirstIn(s).getOrElse("0").replace(",", "")))
  }

  protected case class NumberOp[T](op: String => T)
  protected implicit val getInt = NumberOp[Int](_.toInt)
  protected implicit val getDouble = NumberOp[Double](_.toDouble)
}
