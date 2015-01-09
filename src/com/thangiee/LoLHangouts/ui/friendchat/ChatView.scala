package com.thangiee.LoLHangouts.ui.friendchat

import android.content.Context
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.widget.{EditText, FrameLayout}
import com.dd.CircularProgressButton
import com.thangiee.LoLHangouts.data.repository.MessageRepoImpl
import com.thangiee.LoLHangouts.domain.entities.{Friend, Message}
import com.thangiee.LoLHangouts.domain.interactor.{GetMsgUseCaseImpl, MarkMsgReadUseCaseImp, SendMsgUseCaseImpl}
import com.thangiee.LoLHangouts.utils._
import com.thangiee.LoLHangouts.{CustomView, R}

import scala.collection.JavaConversions._

class ChatView(implicit ctx: Context) extends FrameLayout(ctx) with CustomView {
  lazy val sendButton      = find[CircularProgressButton](R.id.btn_send_msg)
  lazy val msgField        = find[EditText](R.id.et_msg_field)
  lazy val messageAdapter  = new MessageAdapter(ctx, 0)
  lazy val messageListView = find[MessagesListView](R.id.lsv_chat)
  private var friend: Option[Friend] = None

  implicit val messageRepo = MessageRepoImpl()
  override val presenter = new ChatPresenter(this, GetMsgUseCaseImpl(), MarkMsgReadUseCaseImp(), SendMsgUseCaseImpl())

  override def onAttachedToWindow(): Unit = {
    super.onAttachedToWindow()
    addView(layoutInflater.inflate(R.layout.chat_view, null))

    sendButton.onClick(presenter.handleMessageSend(msgField.getText.toString))
    sendButton.setIndeterminateProgressMode(true)

    messageListView.setAdapter(messageAdapter)
    messageListView.setBackgroundColor(R.color.my_dark_blue.r2Color)

    msgField.clearFocus()
  }

  def setFriend(friend: Friend) = {
    this.friend = Some(friend)
    if (isAttachedToWindow) presenter.handleFriendChange(friend)
  }

  def getFriend: Option[Friend] = friend

  def setHint(hint: String): Unit = msgField.setHint(hint)

  def showProgress(): Unit = sendButton.setProgress(50)

  def hideProgress(): Unit = sendButton.setProgress(0)

  def showSentSuccess(): Unit = sendButton.setProgress(100)

  def showSendFail(): Unit = sendButton.setProgress(-1)

  def showErrorMsg(msg: String): Unit = msg.croutonWarn()

  def setTitle(title: String): Unit = {} // todo: implement

  def clearMessageInput(): Unit = msgField.setText("")

  def clearMessages(): Unit = messageAdapter.clear()

  def showMessages(messages: List[Message]) = {
    messageAdapter.addAll(messages)
    messageListView.setSelection(messageAdapter.getCount - 1) // scroll to the bottom (newer messages)
  }

  def setUserIcon(icon: Drawable): Unit = {
    messageAdapter.setSenderDrawable(icon)
  }

  def setFriendIcon(icon: Drawable): Unit ={
    messageAdapter.setRecipientDrawable(icon)
  }

  def playMsgSentSound(): Unit = {
    if (isSoundPreferenceOn)
      MediaPlayer.create(ctx, R.raw.alert_pm_sent).start()
  }

  def playMsgReceiveSound(): Unit = {
    if (isSoundPreferenceOn)
      MediaPlayer.create(ctx, R.raw.alert_pm_receive).start()
  }

  private def isSoundPreferenceOn: Boolean = R.string.pref_notify_sound.pref2Boolean(default = true)
}