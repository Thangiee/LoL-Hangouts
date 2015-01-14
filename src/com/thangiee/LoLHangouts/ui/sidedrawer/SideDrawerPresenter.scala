package com.thangiee.LoLHangouts.ui.sidedrawer

import com.thangiee.LoLHangouts.Presenter
import com.thangiee.LoLHangouts.domain.interactor._
import com.thangiee.LoLHangouts.ui.sidedrawer.DrawerItem._
import com.thangiee.LoLHangouts.ui.sidedrawer.SideDrawerView._
import com.thangiee.LoLHangouts.utils.Events.SwitchScreen
import com.thangiee.LoLHangouts.utils._
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
      }
    }

    info("[*] loading user")
    getUserUseCase.loadUser().map(user => runOnUiThread {
      view.setUserProfileIcon(user.loginName, user.region.id)
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

  def handleDrawerItemClicked(drawer: DrawerItem, sameDrawerSelected: Boolean): Unit = {
    drawer.title match {
      case Settings  => view.showSettings(); return
      case Logout    => view.showLogoutConfirmation(); return
      case RemoveAds => return // todo
      case _         =>
    }

    view.closeDrawer()
    if (!sameDrawerSelected) {
      view.updateDrawer(drawer)
      EventBus.getDefault.post(SwitchScreen(drawer.title))
    }
  }

  def handleLogout(): Unit = {
    info("[*] Logging out")
    logoutUseCaseImpl.logout().map(_ => EventBus.getDefault.post(Events.FinishMainActivity(goToLogin = true)))
  }

  def handleChangeStatusMsg(msg: String) = {
    info(s"[*] setting status msg to: $msg")
    changeStatusUseCase.setStatusMsg(msg)
    view.setStatusMsg(msg)
  }
}
