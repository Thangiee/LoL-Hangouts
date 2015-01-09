package com.thangiee.LoLHangouts.data.repository.datasources.net.stats

import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

import scala.util.Try

trait Parsing {
  protected def fetchDocument(url: String): Document

  protected def getNumber[T: NumberOp](s: String): Option[T] = {
    val token = "[-0-9,/.]+".r
    Try(implicitly[NumberOp[T]].op(token.findFirstIn(s).getOrElse("0").replace(",", ""))).toOption
  }

  case class NumberOp[T](op: String => T)
  implicit val getInt = NumberOp[Int](_.toInt)
  implicit val getDouble = NumberOp[Double](_.toDouble)

  implicit class HtmlElementOp(html: Element) {
    def p(value: String, default: String = "", index: Int = 0)       : Element = get(s"p[class=$value]", default, index)
    def div(value: String, default: String = "", index: Int = 0)     : Element = get(s"div[class=$value]", default, index)
    def divId(value: String, default: String = "", index: Int = 0)   : Element = get(s"div[id=$value]", default, index)
    def td(value: String, default: String = "", index: Int = 0)      : Element = get(s"td[class=$value]", default, index)
    def table(value: String, default: String = "", index: Int = 0)   : Element = get(s"table[class=$value]", default, index)
    def tableId(value: String, default: String = "", index: Int = 0) : Element = get(s"table[id=$value]", default, index)
    def tr(value: String, default: String = "", index: Int = 0)      : Element = get(s"tr[class=$value]", default, index)
    def span(value: String, default: String = "", index: Int = 0)    : Element = get(s"span[class=$value]", default, index)
    def a(value: String, default: String = "", index: Int = 0)       : Element = get(s"a[class=$value]", default, index)

    def tr(): Elements = html.select("tr")
    def td(): Elements = html.select("td")
    def a(): Elements = html.select("a")
    def table(): Elements = html.select("table")

    private def get(value: String, default: String, index: Int): Element = {
      Try(html.select(value).get(index)).getOrElse(Jsoup.parse(default))
    }
  }
}
