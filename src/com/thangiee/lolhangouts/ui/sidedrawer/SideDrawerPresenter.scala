package com.thangiee.lolhangouts.ui.sidedrawer

import com.thangiee.lolhangouts.domain.interactor._
import com.thangiee.lolhangouts.ui.core.Presenter
import com.thangiee.lolhangouts.ui.sidedrawer.DrawerItem._
import com.thangiee.lolhangouts.ui.sidedrawer.SideDrawerView._
import com.thangiee.lolhangouts.utils.Events.SwitchScreen
import com.thangiee.lolhangouts.utils._
import de.greenrobot.event.EventBus

import scala.concurrent.ExecutionContext.Implicits.global

class SideDrawerPresenter(view: SideDrawerView, getAppDataUseCase: GetAppDataUseCase,
                        changeStatusUseCase: ChangeUserStatusCase, getUserUseCase: GetUserUseCase,
                        logoutUseCaseImpl: LogoutUseCase) extends Presenter {

  override def initialize(): Unit = {
    super.initialize()

    info("[*] loading app data")
    getAppDataUseCase.loadAppData().map { data =>
      if (data.isLoginOffline) runOnUiThread {
        view.switchToOffline()
        view.showIsOfflineMsg()
      } else {
        changeStatusUseCase.appearOnline()
        runOnUiThread(view.switchToOnline())
      }
    }

    info("[*] loading user")
    getUserUseCase.loadUser().map(user => runOnUiThread {
      changeStatusUseCase.setStatusMsg(user.statusMsg)
      view.setUserProfileIcon(user.inGameName, user.region.id)
      view.setStatusMsg(user.statusMsg)
      view.setName(user.inGameName)
    })
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
    drawer.title match {
      case Settings  => view.showSettings(); return
      case Logout    => view.showLogoutConfirmation(); return
      case RemoveAds => view.showRemoveAdsConfirmation(); return
      case _         =>
    }

    view.closeDrawer()
    if (!drawer.isSelected) { // don't reload if same drawer is selected
      view.updateDrawer(position)
      EventBus.getDefault.post(SwitchScreen(drawer.title))
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
