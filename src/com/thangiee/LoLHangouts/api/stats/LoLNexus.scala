package com.thangiee.LoLHangouts.api.stats

import org.jsoup.nodes.{Document, Element}

import scala.collection.JavaConversions._
import scala.util.Try

class LoLNexus(playerName: String, playerRegion: String) extends LiveGameStats with Parsing {
  override protected val baseServerUrl: String = "http://www.lolnexus.com/"
  override val url: String = baseServerUrl + playerRegion + "/search?name=" + playerName
  override var doc: Document = null

  override def allPlayers: List[LiveGamePlayerStats] = teammates ++ opponents

  override def teammates: List[LiveGamePlayerStats] = {
    for (p <- doc.select("div[class=team-1]").first().select("tbody").select("tr[class]").toList) yield new Player(p)
  }

  override def opponents: List[LiveGamePlayerStats] = {
    for (p <- doc.select("div[class=team-2]").first().select("tbody").select("tr[class]").toList) yield new Player(p)
  }

  private class Player(html: Element) extends LiveGamePlayerStats {
    val tierTitle = List("Bronze", "Silver", "Gold", "Platinum", "Diamond", "Challenger")

    override def previousLeagueTier(): String = parse("span", html.select("td[class=last-season]").first()).getOrElse("???")

    override def series(): Option[Series] = {
      val series = Try(html.select("td[class=current-season]").select("div[class=ranking]").select("ul").first().select("li").toList)

      if (series.isSuccess) {
        val result = for (g <- series.get) yield g.text match {
          case "Win" => 1
          case "Loss" => -1
          case _ => 0
        }
        Some(Series(result))
      } else {
        None
      }
    }

    override def normal5v5: GameModeStats = {
      val w = getNumber[Int](parse("span", html.select("td[class=normal-wins").first).get).getOrElse(0)
      GameModeStats(w, 0, 0, 0, 0, w)
    }

    override def soloQueue: GameModeStats = {
      val w = getNumber[Int](parse("span[class=ranked-wins]", html.select("td[class=ranked-wins-losses").first()).get).getOrElse(0)
      val l = getNumber[Int](parse("span[class=ranked-losses]", html.select("td[class=ranked-wins-losses").first()).get).getOrElse(0)
      val k = getNumber[Double](parse("span[class=kills]", html.select("td[class=champion-kda").first()).get).getOrElse(0.0)
      val d = getNumber[Double](parse("span[class=deaths]", html.select("td[class=champion-kda").first()).get).getOrElse(0.0)
      val a = getNumber[Double](parse("span[class=assists]", html.select("td[class=champion-kda").first()).get).getOrElse(0.0)
      GameModeStats(w, l, k, d, a, w + l)
    }

    override def leaguePoints(): String = parse("b", html.select("td[class=current-season]").first()).getOrElse("0 LP")

    override def name: String = parse("span", html.select("td[class=name]").first()).getOrElse("???")

    override def leagueTier(): String = parse("span", html.select("td[class=current-season]").first()).getOrElse("???").split(" ").head

    override def leagueDivision(): String = parse("span", html.select("td[class=current-season]").first()).getOrElse("???").split(" ").tail.head

    override def chosenChamp: String = {
      parse("span", html.select("td[class=champion]").first()).getOrElse("???").split(" \\(").head
    }
  }
}

