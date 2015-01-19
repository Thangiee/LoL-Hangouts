package com.thangiee.LoLHangouts.ui.friendchat

import java.util.Date

import com.thangiee.LoLHangouts.Presenter
import com.thangiee.LoLHangouts.domain.entities.{Friend, Message}
import com.thangiee.LoLHangouts.domain.exception.{SendMessageException, UserInputException}
import com.thangiee.LoLHangouts.domain.interactor._
import com.thangiee.LoLHangouts.utils.Events.IncomingMessage
import com.thangiee.LoLHangouts.utils._
import de.greenrobot.event.EventBus

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class ChatPresenter(view: ChatView, deleteMsgUseCase: DeleteMsgUseCase,
                    getMessageUseCase: GetMsgUseCase, sendMsgUseCase: SendMsgUseCase,
                    getUserUseCase: GetUserUseCase, getFriendsUseCase: GetFriendsUseCase) extends Presenter {

  val loadUser = getUserUseCase.loadUser()

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
        info("[*] Attempting to send message")
        sendMsgUseCase.sendMessage(m) onComplete {
          case Success(_) => onSentMsgSuccess(m)
          case Failure(e) => showFailure(e)
        }
      case None    =>
        view.showErrorMsg("Can't send message to nobody.")
        view.showSendFail()
        Future{ Thread.sleep(750); runOnUiThread(view.hideProgress()) }
    }
  }

  def handleFriendChange(friend: Friend): Unit = {
    view.clearMessages()
    view.setHint(s"Send to ${friend.name}")
    view.setFriendIcon(friend.name, friend.regionId)
    loadUser onSuccess { case user =>
      runOnUiThread(view.setUserIcon(user.inGameName, user.region.id))
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
        view.showMessages(messages) //todo: set msg limit?
        view.hideLoading()
      })
    }.getOrElse(warn("[!] friend is not set for ChatView"))
  }

  private def onSentMsgSuccess(m: Message): Unit = {
    runOnUiThread(view.showSentSuccess())
    Thread.sleep(750)
    runOnUiThread {
      view.playMsgSentSound()
      view.showMessages(List(m))
      view.clearMessageInput()
      view.hideProgress()
    }
  }

  private def showFailure(e: Throwable): Unit = {
    runOnUiThread(view.showSendFail())
    Thread.sleep(750)
    runOnUiThread(view.hideProgress())

    e match {
      case e@(_: UserInputException | _: SendMessageException) =>
        runOnUiThread(view.showErrorMsg(e.getMessage))
    }
  }

  def onEvent(event: IncomingMessage): Unit = runOnUiThread {
    view.getFriend.map { f =>
      // make sure incoming message is for the current chat
      if (f.name == event.from.name) {
        view.showMessages(List(event.msg))
        view.playMsgReceiveSound()
      }
    }
  }
}
