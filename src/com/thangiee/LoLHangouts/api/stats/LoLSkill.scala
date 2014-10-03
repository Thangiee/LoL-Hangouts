package com.thangiee.LoLHangouts.api.stats

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}


class LoLSkill(playerName: String, playerRegion: String) extends ProfilePlayerStats with Parsing {
  val baseServerUrl: String = "http://www.lolskill.net/summoner/"
  val url          : String = baseServerUrl + playerRegion + "/" + playerName
  val summaryPage  : Document = fetchDocument(url + "/summary")
  val championsPage: Document = fetchDocument(url + "/champions")
  val matchesPage  : Document = fetchDocument(url + "/matches")
  override lazy val leagueName    : String         = summaryPage.divId("rankedSolo").p("leaguename", "N/A").text()
  override lazy val level         : Int            = getNumber[Int](summaryPage.div("realm").text()).getOrElse(1)
  override lazy val region        : String         = playerRegion
  override lazy val leaguePoints  : String         = summaryPage.divId("rankedSolo").p("leaguepoints", "N/A").text()
  override lazy val name          : String         = playerName
  override lazy val leagueTier    : String         = summaryPage.divId("rankedSolo").p("tier").text().split(" ").headOption.getOrElse("Unranked")
  override lazy val leagueDivision: String         = summaryPage.divId("rankedSolo").p("tier").text().split(" ").lastOption.getOrElse("")
  override lazy val normal5v5     : GameModeStats  = ???
  override lazy val soloQueue     : GameModeStats  = {
    try {
      val statsTable = summaryPage.divId("stats").table("skinned")
      val g = getNumber[Int](statsTable.tr().get(1).td("right").text()).getOrElse(0)
      val k = getNumber[Double](statsTable.tr().get(2).td("right").text()).getOrElse(0.0) / g
      val d = getNumber[Double](statsTable.tr().get(3).td("right").text()).getOrElse(0.0) / g
      val a = getNumber[Double](statsTable.tr().get(4).td("right").text()).getOrElse(0.0) / g

      GameModeStats(
        getNumber[Int](summaryPage.span("wins").text()).getOrElse(0),
        getNumber[Int](summaryPage.span("losses").text()).getOrElse(0),
        k.roundTo(1),
        d.roundTo(1),
        a.roundTo(1),
        g
      )
    } catch {
      case e: Exception ⇒
        e.printStackTrace()
        GameModeStats(0, 0, 0, 0, 0, 0)
    }
  }
  override lazy val matchHistory  : List[Match]    = {
    matchesPage.tableId("matchHistory").select("tr[class^=match]").map(row ⇒
      Match(
        row.td("champion tooltip").a().attr("href").split("/").last,
        row.td("queueInfo").div("queue").text(),
        row.td("queueInfo").div("outcome").text(),
        row.td("timeInfo tooltip").div("date").text(),
        row.td("timeInfo tooltip").div("duration").text(),
        Stats(
          getNumber[Double](row.td("kda").table().head.td().get(1).text().split(" ").head).getOrElse(0),
          getNumber[Double](row.td("kda").table().head.td().get(3).text().split(" ").head).getOrElse(0),
          getNumber[Double](row.td("kda").table().head.td().get(5).text().split(" ").head).getOrElse(0),
          getNumber[Int](row.td("stats").table().head.td().get(3).text().split(" ").head).getOrElse(0),
          getNumber[Int](row.td("stats").table().head.td().get(5).text().split(" ").head).getOrElse(0)),
        AvgBetterStats(
          getNumber[Double](row.td("stats").table().head.td().get(1).text()).getOrElse(0),
          getNumber[Double](row.td("kda").table().head.td().get(1).text().split(" ").last).getOrElse(0),
          getNumber[Double](row.td("kda").table().head.td().get(3).text().split(" ").last).getOrElse(0),
          getNumber[Double](row.td("kda").table().head.td().get(5).text().split(" ").last).getOrElse(0),
          getNumber[Int](row.td("stats").table().head.td().get(3).text().split(" ").last).getOrElse(0),
          getNumber[Int](row.td("stats").table().head.td().get(5).text().split(" ").last).getOrElse(0))
      )
    ).toList
  }
  override lazy val topChampions  : List[Champion] = {
    try {
      championsPage.tableId("championsTable").tr().tail.map(row =>
        Champion(
          row.td("left").a().head.text(), // champion name
          getNumber[Int](row.td().get(6).text().split(" / ").last).getOrElse(0), // # of games
          Stats(
            getNumber[Double](row.td().get(7).text()).getOrElse(0), // kills
            getNumber[Double](row.td().get(8).text()).getOrElse(0), // deaths
            getNumber[Double](row.td().get(9).text()).getOrElse(0), // assists
            getNumber[Int](row.td().get(10).text()).getOrElse(0), // cs
            getNumber[Int](row.td().get(11).text()).getOrElse(0)), // gold
          AvgBetterStats(
            getNumber[Double](row.td().get(5).text()).getOrElse(0), // performance
            getNumber[Double](row.td().get(7).span("small").text()).getOrElse(0), // kills
            getNumber[Double](row.td().get(8).span("small").text()).getOrElse(0), // deaths
            getNumber[Double](row.td().get(9).span("small").text()).getOrElse(0), // assists
            getNumber[Int](row.td().get(10).span("small").text()).getOrElse(0), //cs
            getNumber[Int](row.td().get(11).span("small").text()).getOrElse(0)) // gold
        )
      ).toList
    } catch {
      case e: UnsupportedOperationException => List[Champion]() // found none
    }
  }

  override protected def fetchDocument(url: String): Document = {
    val ISE = new IllegalStateException("Service is currently unavailable. Please try again later!")

    // do multiple attempts to get the document(aka html stuff)
    for (attempt ← 1 to 5) {
      println("[*] Attempt " + attempt + "|Connecting to: " + url)
      Try(Jsoup.connect(url).timeout(5000).get()) match {
        case Success(respond) ⇒ // got respond from website
          if (!respond.text().contains("currently unavailable")) {
            // website respond with it been busy
            return respond
          } else if (attempt == 5) {
            // all attempts used up
            throw ISE
          } else {
            Thread.sleep(150)
          }
        case Failure(e) ⇒ if (attempt == 5) throw e // if no respond, keep trying
      }
    }
    throw ISE
  }
}
