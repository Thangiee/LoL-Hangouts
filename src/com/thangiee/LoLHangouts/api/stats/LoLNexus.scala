package com.thangiee.LoLHangouts.api.stats

import org.jsoup.nodes.{Document, Element}

import scala.collection.JavaConversions._
import scala.util.Try

class LoLNexus(playerName: String, playerRegion: String) extends LiveGameStats with Parsing {
  override protected val baseServerUrl: String = "http://www.lolnexus.com/"
  override val url: String = baseServerUrl + playerRegion + "/search?name=" + playerName
  override val doc: Document = fetchDocument

  override lazy val teammates: List[LiveGamePlayerStats] = {
    for (p <- Try(doc.select("div[class=team-1]").first().select("tbody").select("tr[class]").toList).getOrElse(List())) yield new Player(p)
  }

  override val opponents: List[LiveGamePlayerStats] = {
    for (p <- Try(doc.select("div[class=team-2]").first().select("tbody").select("tr[class]").toList).getOrElse(List())) yield new Player(p)
  }

  override val allPlayers: List[LiveGamePlayerStats] = teammates ++ opponents

  override protected def fetchDocument: Document = ???

  private class Player(html: Element) extends LiveGamePlayerStats {
    val tierTitle = List("Bronze", "Silver", "Gold", "Platinum", "Diamond", "Challenger")

    override val previousLeagueTier: String = parse("span", html.select("td[class=last-season]").first()).getOrElse("???")

    override val series: Option[Series] = {
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

    override val normal5v5: GameModeStats = {
      val w = getNumber[Int](parse("span", html.select("td[class=normal-wins").first).get).getOrElse(0)
      GameModeStats(w, 0, 0, 0, 0, w)
    }

    override val soloQueue: GameModeStats = {
      var temp = parse("td[class=ranked-wins-losses", html).getOrElse("").split("/")
      val w = getNumber[Int](temp.headOption.get).getOrElse(0)
      val l = getNumber[Int](temp.lastOption.get).getOrElse(0)

      temp = parse("td[class=champion-kda", html).getOrElse("").split("/")
      val k = getNumber[Double](temp.headOption.get).getOrElse(0.0)
      val d = getNumber[Double](temp(2)).getOrElse(0.0)
      val a = getNumber[Double](temp.lastOption.get).getOrElse(0.0)

      temp = null
      GameModeStats(w, l, k, d, a, w + l)
    }

    override val leaguePoints: String = parse("b", html.select("td[class=current-season]").first()).getOrElse("0 LP")

    override val name: String = parse("span", html.select("td[class=name]").first()).getOrElse("???")

    override val leagueTier: String = parse("span", html.select("td[class=current-season]").first()).getOrElse("???").split(" ").head

    override val leagueDivision: String = parse("span", html.select("td[class=current-season]").first()).getOrElse("???").split(" ").tail.head

    override val chosenChamp: String = {
      parse("span", html.select("td[class=champion]").first()).getOrElse("???").split(" \\(").head
        .replace("Lee Sin", "lee-sin")  // format cases to download icon URL
        .replace("Miss Fortune", "miss-fortune")
        .replace("Xin Zhao", "xin-zhao")
        .replace("Dr. Mundo", "dr-mundo")
        .replace("Master Yi", "master-yi")
        .replace("Jarvan IV", "jarvan-iv")
        .replace("Twisted Fate", "twisted-fate")
        .replace("Cho'Gath", "chogath")
        .replace("Kha'Zix", "khazix")
        .replace("Kog'Maw", "kogmaw")
    }
  }
}

