package com.thangiee.lolhangouts.data.utils

import com.thangiee.lolhangouts.data.exception.DataAccessException
import com.thangiee.lolhangouts.data.exception.DataAccessException.{DataNotFound, GetDataError}
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements

import scala.util.{Failure, Success, Try}

trait Parser extends AnyRef with TagUtil {
  def fetchDocument(url: String): Try[Document] = {
    // do multiple attempts to get the document(aka html stuff)
    for (attempt ← 1 to 5) {
      info(s"[*] Attempt $attempt |Connecting to: $url")
      val response = Jsoup.connect(url).timeout(5000).execute()
      response.statusCode() match {
        case 200 =>
          if (response.body().contains("Summoner Not Found")) {
            info("[-] Summoner info not available")
            return Failure(DataAccessException("[-] Summoner info not available", DataNotFound))
          }
          else
            return Success(response.parse())
        case _   =>
          if (attempt == 5) Thread.sleep(200) // wait a bit and retry
      }
    }

    info("[-] Server is busy/unavailable")
    Failure(DataAccessException("[-] Server is busy/unavailable", GetDataError))
  }

  def getNumber[T: NumberOp](s: String): Option[T] = {
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

object Parser extends Parser
