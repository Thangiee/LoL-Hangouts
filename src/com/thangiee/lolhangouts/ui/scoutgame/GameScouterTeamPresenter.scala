package com.thangiee.lolhangouts.ui.scoutgame

import com.thangiee.lolhangouts.data.usecases.ScoutGameUseCase
import com.thangiee.lolhangouts.data.usecases.ScoutGameUseCase.{OldAppVersion, InternalError, GameInfoNotFound}
import com.thangiee.lolhangouts.ui.core.Presenter
import com.thangiee.lolhangouts.ui.scoutgame.GameScouterTeamView._
import com.thangiee.lolhangouts.ui.utils._
import org.scalactic.{Bad, Good}
import scala.concurrent.ExecutionContext.Implicits.global

class GameScouterTeamPresenter(view: GameScouterTeamView, scoutGameUseCase: ScoutGameUseCase) extends Presenter {

  def handleScoutingGame(username: String, regionId: String, teamColor: Int): Unit = {

    scoutGameUseCase.scoutGameInfo(username, regionId).onSuccess {
      case Good(scoutReport) => runOnUiThread {
        if (teamColor == BlueTeam) view.initializeViewData(scoutReport.mapName, regionId, BlueTeam, scoutReport.blueTeam)
        else                       view.initializeViewData(scoutReport.mapName, regionId, PurpleTeam, scoutReport.purpleTeam)

        view.hideLoading()
      }
      case Bad(GameInfoNotFound) => runOnUiThread(view.showDataNotFound(username, snackBarAction = reload()))
      case Bad(InternalError)    => runOnUiThread(view.showGetDataError(snackBarAction = reload()))
      case Bad(OldAppVersion)    => runOnUiThread(view.showAppNeedUpdate(snackBarAction = reload()))
    }

    def reload(): Unit = {
      view.showLoading()
      handleScoutingGame(username, regionId, teamColor)
    }
  }
}
