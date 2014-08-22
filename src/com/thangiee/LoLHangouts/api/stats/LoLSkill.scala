package com.thangiee.LoLHangouts.api.stats

import org.jsoup.nodes.Document

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.util.Try


class LoLSkill(playerName: String, playerRegion: String) extends ProfilePlayerStats with Parsing {
  override protected val baseServerUrl: String = "http://www.lolskill.net/summoner/"
  override val url: String = baseServerUrl + region + "/" + name
  override var doc: Document = fetchDocument

  override def leagueName(): String = parse("p[class=leaguename]").getOrElse("N/A")

  override def matchHistory(): List[Match] = {
    val matches = ListBuffer[Match]()
    try {
      val matchRows = doc.select("table[id=matchHistory]").first().select("tr[class^=match]").toList

      for (row <- matchRows) {
        //    0   |1 |    2 | 3 |   4   |5  |  6    |7  |  8  |   9   |  10     | 11  | 12
        // Perf.  |K |      | D |       |A  |       |CS |     |Gold   |         |green| pink
        // +85.8% |5 |(+2.8)| 5 |(-1.5) |28 |(+14.0)|52 |(+26)|15,893 |(+6,631) | 1   | 1
        val statsElements = row.select("td[class=stats]").select("tr").text().split(" ").toList

        matches.append(
          Match(
            "/.*?/".r.findFirstIn(row.select("td[class=champion tooltip]").select("a").attr("href")).getOrElse("") // get champion name
              .replace("/", "") // formatting
              // special case
              .replace("leesin", "lee-sin")
              .replace("missfortune", "miss-fortune")
              .replace("xinzhao", "xin-zhao")
              .replace("drmundo", " ddr-mundo")
              .replace("masteryi", "master-yi")
              .replace("jarvaniv", "jarvan-iv")
              .replace("twisted fate", "twisted-fate"),
            row.select("td[class=info]").select("div[class=queue]").text(),
            row.select("td[class=info]").select("div[class=outcome]").text(),
            row.select("td[class=info]").select("div[class=date tooltip]").text(),
            row.select("td[class=info]").select("div[class=duration]").text(),
            Stats(
              getNumber[Double](statsElements(1)).getOrElse(0),
              getNumber[Double](statsElements(3)).getOrElse(0),
              getNumber[Double](statsElements(5)).getOrElse(0),
              getNumber[Int](statsElements(7)).getOrElse(0),
              getNumber[Int](statsElements(9)).getOrElse(0)
            ),
            AvgBetterStats(
              getNumber[Double](statsElements(0)).getOrElse(0),
              getNumber[Double](statsElements(2)).getOrElse(0),
              getNumber[Double](statsElements(4)).getOrElse(0),
              getNumber[Double](statsElements(6)).getOrElse(0),
              getNumber[Int](statsElements(8)).getOrElse(0),
              getNumber[Int](statsElements(10)).getOrElse(0)
            )
          )
        )
      }
    } catch {
      case e: NullPointerException ⇒ return matches.toList // didn't find any match history
    }
    matches.toList
  }

  override def topChampions(): List[Champion] = {
    // 0  |    1   |    2     |  3    |4 |5   |  6   |7   |8     |9   |10    | 11 |12   |13    | 14
    //Rank|Champion|SkillScore|Perf.  |G |K   |      |D   |      |A   |      |CS  |     |Gold  |
    //1   |  Yasuo |  2,659   |+10.8% |14|7.9 |(+0.2)|5.6 |(-2.3)|6.6 |(-0.2)|179 |(+10)|11,944|(-137)
    try {
      val champRows = doc.select("table[id=championsTable]").first().select("tr").tail.toList
      for (row <- champRows.map(_.text()                      // format special name that has space, period, and/or apostrophe
        .replaceFirst("(?<=[a-zA-z])\\.? (?=[a-zA-z])", "-")  // ex. Dr. Mundo -> Dr-Mundo  and Lee Sin -> LeeSin
        .replaceFirst("(?<=[a-zA-z])'(?=[a-zA-z])", "")      // ex. Kog'maw -> Kogmaw
        .split(" ")))
      yield Champion(
        row(1), // name
        "http://www.mobafire.com/images/champion/icon/" + row(1).toLowerCase + ".png", // icon url
        getNumber[Int](row(4)).getOrElse(0), // # of game
        Stats(
          getNumber[Double](row(5)).getOrElse(0), // kills
          getNumber[Double](row(7)).getOrElse(0), // deaths
          getNumber[Double](row(9)).getOrElse(0), // assists
          getNumber[Int](row(11)).getOrElse(0), // cs
          getNumber[Int](row(13)).getOrElse(0) // gold
        ),
        AvgBetterStats(
          getNumber[Double](row(3)).getOrElse(0), // performance
          getNumber[Double](row(6)).getOrElse(0), // kills
          getNumber[Double](row(8)).getOrElse(0), // deaths
          getNumber[Double](row(10)).getOrElse(0), // assists
          getNumber[Int](row(12)).getOrElse(0), //cs
          getNumber[Int](row(14)).getOrElse(0) // gold
        )
      )
    } catch {
      case e: NullPointerException ⇒ List[Champion]() // didn't find any champion
    }
  }

  override def level: Int = parse("div[class=realm]").flatMap[Int](getNumber[Int]).getOrElse(1)

  override def soloQueue: GameModeStats = {
    val statsTable = Try(doc.select("div[id=stats]").first().select("table[Class=skinned]").get(1).select("tr"))
    val g = getNumber[Int](statsTable.get.get(1).select("td[class=right]").first().text()).getOrElse(0)   // # games
    val k = getNumber[Double](statsTable.get.get(2).select("td[class=right]").first().text()).getOrElse(0.0) / g
    val d = getNumber[Double](statsTable.get.get(3).select("td[class=right]").first().text()).getOrElse(0.0) / g
    val a = getNumber[Double](statsTable.get.get(4).select("td[class=right]").first().text()).getOrElse(0.0) / g

    GameModeStats(
      parse("span[class=wins]").flatMap[Int](getNumber[Int]).getOrElse(0),
      parse("span[class=losses]").flatMap[Int](getNumber[Int]).getOrElse(0),
      k.roundTo(1),
      d.roundTo(1),
      a.roundTo(1),
      g
    )
  }

  override def region: String = playerRegion

  override def leaguePoints(): String = parse("p[class=leaguepoints]").getOrElse("N/A")

  override def name: String = playerName

  override def leagueTier(): String = parse("p[class=tier]").flatMap[String](s => Try(s.split(" ").head)).getOrElse("N/A")

  override def leagueDivision(): String = parse("p[class=tier]").flatMap[String](s => Try(s.split(" ").last)).getOrElse("N/A")

  override def normal5v5: GameModeStats = ???
}
