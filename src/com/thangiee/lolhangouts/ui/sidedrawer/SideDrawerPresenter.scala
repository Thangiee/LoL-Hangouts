package com.thangiee.lolhangouts.ui.sidedrawer

import java.util.concurrent.TimeUnit.SECONDS

import com.thangiee.lolhangouts.data.usecases._
import com.thangiee.lolhangouts.ui.core.Presenter
import com.thangiee.lolhangouts.ui.sidedrawer.DrawerItem._
import com.thangiee.lolhangouts.ui.sidedrawer.SideDrawerView._
import com.thangiee.lolhangouts.ui.utils.Events.SwitchContainer
import com.thangiee.lolhangouts.ui.utils._
import de.greenrobot.event.EventBus

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

class SideDrawerPresenter(view: SideDrawerView, getAppDataUseCase: GetAppDataUseCase,
  changeStatusUseCase: ChangeUserStatusCase, getUserUseCase: GetUserUseCase,
  logoutUseCaseImpl: LogoutUseCase) extends Presenter {

  private lazy val loadAppData = getAppDataUseCase.loadAppData()

  override def initialize(): Unit = {
    super.initialize()

    info("[*] loading app data")
    loadAppData.map { data =>
      if (data.isLoginOffline) runOnUiThread {
        view.switchToOffline()
        view.showIsOfflineMsg()
      } else {
        changeStatusUseCase.appearOnline()
        runOnUiThread(view.switchToOnline())
      }
    }

    info("[*] loading user")
    getUserUseCase.loadUser().onSuccess {
      case Good(user) => runOnUiThread {
        changeStatusUseCase.setStatusMsg(user.statusMsg)
        view.setUserProfileIcon(user.inGameName, user.region.id)
        view.setStatusMsg(user.statusMsg)
        view.setName(user.inGameName)
      }
    }
  }

  def handleChangePresence(mode: Int) = mode match {
    case Online  =>
      changeStatusUseCase.appearOnline()
      view.switchToOnline()
    case Offline =>
      changeStatusUseCase.appearOffline()
      view.switchToOffline()
    case Away    =>
      changeStatusUseCase.appearAway()
      view.switchToAway()
  }

  def handleDrawerItemClicked(drawer: DrawerItem, position: Int): Unit = {
    // cases were the current container does not need to be switch after an item in the drawer is clicked
    drawer.title match {
      case Settings       => view.showSettings(); return
      case Logout         => view.showLogoutConfirmation(); return
      case RemoveAds      => view.showRemoveAdsConfirmation(); return
      case Chat | Profile =>
        if (Await.result(loadAppData.map(_.isGuestMode), Duration.apply(10, SECONDS))) { // check if guest mode
          view.showFeatureRestricted()
          return
        }
      case _              => // do nothing; go to the code below
    }

    view.closeDrawer()
    // don't reload if the current drawer item is already selected.
    // i.e. don't reload the chat screen if the user is currently at the chat screen.
    if (!drawer.isSelected) {
      view.updateDrawer(position)
      EventBus.getDefault.post(SwitchContainer(drawer.title))
    }
  }

  def handleLogout(): Unit = {
    EventBus.getDefault.post(Events.Logout())
    view.navigateToLoginScreen()
  }

  def handleChangeStatusMsg(msg: String) = {
    info(s"[*] setting status msg to: $msg")
    changeStatusUseCase.setStatusMsg(msg)
    view.setStatusMsg(msg)
  }
}
