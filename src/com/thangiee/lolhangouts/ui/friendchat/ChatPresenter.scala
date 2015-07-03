package com.thangiee.lolhangouts.ui.friendchat

import java.util.Date

import com.thangiee.lolhangouts.data.usecases.SendMsgUseCase.EmptyMessage
import com.thangiee.lolhangouts.data.usecases.entities.{Friend, Message}
import com.thangiee.lolhangouts.data.usecases._
import com.thangiee.lolhangouts.ui.core.Presenter
import com.thangiee.lolhangouts.ui.utils.Events.IncomingMessage
import com.thangiee.lolhangouts.ui.utils._
import de.greenrobot.event.EventBus

import scala.concurrent.ExecutionContext.Implicits.global

class ChatPresenter(view: ChatView, deleteMsgUseCase: DeleteMsgUseCase,
                    getMessageUseCase: GetMsgUseCase, sendMsgUseCase: SendMsgUseCase,
                    getUserUseCase: GetUserUseCase, getFriendsUseCase: GetFriendsUseCase) extends Presenter {

  private val loadUser = getUserUseCase.loadUser()

  override def initialize(): Unit = {
    super.initialize()
    EventBus.getDefault.register(this)

    view.getFriend match {
      case Some(f) => view.setHint(s"Send to ${f.name}")
      case None    => view.setHint("Send to NOBODY")
    }

    loadAndShowMessages()
  }

  override def shutdown(): Unit = {
    EventBus.getDefault.unregister(this)
    super.shutdown()
  }

  def handleMessageSend(text: String): Unit = {
    view.showProgress()
    view.getFriend match {
      case Some(f) =>
        val m = Message(f.name, text, isSentByUser = true, isRead = true, new Date())

        sendMsgUseCase.sendMessage(m).onSuccess {
          case Good(_) =>
            runOnUiThread(view.showSentSuccess())
            Thread.sleep(750)
            runOnUiThread {
              view.playMsgSentSound()
              view.showMessages(List(m))
              view.clearMessageInput()
              view.hideProgress()
            }
          case Bad(EmptyMessage) => onSentMsgFail(); runOnUiThread(view.showEmptyMsgError())
          case Bad(_)            => onSentMsgFail(); runOnUiThread(view.showSendMsgError())
        }
      case None    =>
        onSentMsgFail()
        runOnUiThread(view.showEmptyMsgError())
    }
  }

  def handleFriendChange(friend: Friend): Unit = {
    view.clearMessages()
    view.setHint(s"Send to ${friend.name}")
    view.setFriendIcon(friend.name, friend.regionId)
    loadUser.onSuccess {
      case Good(user) => runOnUiThread(view.setUserIcon(user.inGameName, user.region.id))
    }

    loadAndShowMessages()
  }

  def handleDeleteMessages(): Unit = {
    view.getFriend.map { f =>
      deleteMsgUseCase.deleteAllMessages(f.name).map(_ => runOnUiThread(view.showDeletingMessages()))
    }
  }

  private def loadAndShowMessages(): Unit = {
    view.getFriend.map { f =>
      info(s"[*] loading at messages from ${f.name}")
      view.showLoading()
      getMessageUseCase.loadMessages(f.name).map(messages => runOnUiThread {
        view.showMessages(messages)
        delay(100) { view.hideLoading() }
      })
    }.getOrElse(info("[-] friend is not set for ChatView"))
  }

  private def onSentMsgFail(): Unit = {
    runOnUiThread(view.showSendFail())
    Thread.sleep(750)
    runOnUiThread(view.hideProgress())
  }

  def onEvent(event: IncomingMessage): Unit = runOnUiThread {
    view.getFriend.foreach { f =>
      // make sure incoming message is for the current chat
      if (f.name == event.from.name) {
        view.showMessages(List(event.msg))
        view.playMsgReceiveSound()
      }
    }
  }
}
