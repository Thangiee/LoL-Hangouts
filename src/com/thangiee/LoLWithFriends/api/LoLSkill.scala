package com.thangiee.LoLWithFriends.api

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer
import scala.util.Try

/**
 * Class implementation to retrieve statistics for a player from http://www.lolskill.net
 *
 * @param name
 * @param region
 */
class LoLSkill(name: String, region: String) extends LoLStatistics {
  private val baseServerUrl = "http://www.lolskill.net/summoner/"
  private val doc: Try[Document] = Try(Jsoup.connect(baseServerUrl + region + "/" + name).get)

  override def level(): Try[Int] = parse("div[class=realm]").flatMap[Int](getNumber[Int])

  override def win(): Try[Int] = parse("span[class=wins]").flatMap[Int](getNumber[Int])

  override def lose(): Try[Int] = parse("span[class=losses]").flatMap[Int](getNumber[Int])

  override def kda(): Try[String] = parse("p[class=kda]")

  override def leagueName(): Try[String] = parse("p[class=leaguename]")

  override def leaguePoints(): Try[String] = parse("p[class=leaguepoints]")

  override def leagueTier(): Try[String] = parse("p[class=tier]").flatMap[String](s => Try(s.split(" ").head))

  override def leagueDivision(): Try[String] = parse("p[class=tier]").flatMap[String](s => Try(s.split(" ").last))

  override def topChampions(): List[Try[Champion]] = {
    // 0  |    1   |    2     |  3    |4 |5   |  6   |7   |8     |9   |10    | 11 |12   |13    | 14
    //Rank|Champion|SkillScore|Perf.  |G |K   |      |D   |      |A   |      |CS  |     |Gold  |
    //1   |  Yasuo |  2,659   |+10.8% |14|7.9 |(+0.2)|5.6 |(-2.3)|6.6 |(-0.2)|179 |(+10)|11,944|(-137)

    val champRows = doc.get.select("table[id=championsTable]").first().select("tr").tail.toList
    for (row <- champRows.map(_.text().split(" "))) yield Try(
      Champion(
        row(1), // name
        "http://www.mobafire.com/images/champion/icon/" + row(1) + ".png", // icon url
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
    )
  }

  override def matchHistory(): List[Try[Match]] = {
    val matches = ListBuffer[Try[Match]]()
    val matchRows = doc.get.select("table[id=matchHistory]").first().select("tr[class^=match]").toList

    for (row <- matchRows) {
      //    0   |1 |    2 | 3 |   4   |5  |  6    |7  |  8  |   9   |  10     | 11  | 12
      // Perf.  |K |      | D |       |A  |       |CS |     |Gold   |         |green| pink
      // +85.8% |5 |(+2.8)| 5 |(-1.5) |28 |(+14.0)|52 |(+26)|15,893 |(+6,631) | 1   | 1
      val statsElements = row.select("td[class=stats]").select("tr").text().split(" ").toList

      matches.append(Try(
        Match(
          row.select("td[class=champion tooltip]").attr("data-championid").toInt,
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
      ))
    }

    matches.toList
  }

  private def parse(pattern: String): Try[String] = {
    Try(doc.get.select(pattern).first().text())
  }

  private case class NumberOp[T](op: String => T)
  private implicit val getInt = NumberOp[Int](_.toInt)
  private implicit val getDouble = NumberOp[Double](_.toDouble)

  private def getNumber[T: NumberOp](s: String): Try[T] = {
    val token = "[-0-9,/+/.]+".r
    Try(implicitly[NumberOp[T]].op(token.findFirstIn(s).get.replace(",", "")))
  }
}
