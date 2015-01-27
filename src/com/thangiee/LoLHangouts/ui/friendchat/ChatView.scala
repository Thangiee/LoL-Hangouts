package com.thangiee.LoLHangouts.ui.friendchat

import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.os.SystemClock
import android.view.View
import android.widget.{EditText, FrameLayout}
import com.afollestad.materialdialogs.MaterialDialog.Builder
import com.dd.CircularProgressButton
import com.thangiee.LoLHangouts.data.repository._
import com.thangiee.LoLHangouts.domain.entities.{Friend, Message}
import com.thangiee.LoLHangouts.domain.interactor._
import com.thangiee.LoLHangouts.ui.core.CustomView
import com.thangiee.LoLHangouts.utils._
import com.thangiee.LoLHangouts.R
import fr.castorflex.android.circularprogressbar.CircularProgressBar

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ChatView(implicit ctx: Context) extends FrameLayout(ctx) with CustomView {
  lazy val sendButton      = find[CircularProgressButton](R.id.btn_send_msg)
  lazy val msgField        = find[EditText](R.id.et_msg_field)
  lazy val messageAdapter  = new MessageAdapter(ctx, 0)
  lazy val messageListView = find[MessagesListView](R.id.lsv_chat)
  lazy val loadingWheel   = find[CircularProgressBar](R.id.circular_loader)
  
  private  var friend: Option[Friend] = None
  override val presenter = new ChatPresenter(this, DeleteMsgUseCaseImpl(), GetMsgUseCaseImpl(), SendMsgUseCaseImpl(),
                                                   GetUserUseCaseImpl(), GetFriendsUseCaseImpl())

  override def onAttached(): Unit = {
    super.onAttached()
    addView(layoutInflater.inflate(R.layout.chat_view, this, false))

    sendButton.onClick(presenter.handleMessageSend(msgField.txt2str))
    sendButton.setIndeterminateProgressMode(true)

    messageListView.setAdapter(messageAdapter)
    messageListView.setBackgroundColor(Color.TRANSPARENT)
    messageListView.setCacheColorHint(Color.TRANSPARENT)
  }

  def setFriend(friend: Friend) = {
    this.friend = Some(friend)
    if (isAttachedToWindow) presenter.handleFriendChange(friend)
  }

  def getFriend: Option[Friend] = friend

  def setHint(hint: String): Unit = msgField.setHint(hint)

  def showProgress(): Unit = sendButton.setProgress(50)

  def hideProgress(): Unit = sendButton.setProgress(0)

  def showLoading(): Unit = {
    loadingWheel.restart()
    loadingWheel.visibility = View.VISIBLE
    messageListView.visibility = View.INVISIBLE
  }

  def hideLoading(): Unit = {
    loadingWheel.fadeOutUp(duration = 1000)
    messageListView.fadeInDown(duration = 750, delay = 1000)
  }

  def showSentSuccess(): Unit = sendButton.setProgress(100)

  def showSendFail(): Unit = sendButton.setProgress(-1)

  def showErrorMsg(msg: String): Unit = msg.croutonWarn()

  def clearMessageInput(): Unit = msgField.setText("")

  def clearMessages(): Unit = messageAdapter.clear()

  def showDeletingMessages(): Unit = {
    messageListView.fadeOutUp()
    Future {
      SystemClock.sleep(1000)
      runOnUiThread {
        messageAdapter.clear()
        messageListView.fadeInDown(duration = 1) // reset list visibility
      }
    }
  }

  def showMessages(messages: List[Message]) = {
    messageAdapter.addAll(messages)
    messageListView.setSelection(messageAdapter.getCount - 1) // scroll to the bottom (newer messages)
  }

  def showDeleteMessageDialog(): Unit = {
    new Builder(ctx)
      .title(R.string.dialog_delete_title)
      .content(R.string.dialog_delete_message)
      .positiveText("Delete")
      .negativeText("Cancel")
      .onPositive((dialog) => presenter.handleDeleteMessages())
      .show()
  }

  def setUserIcon(username: String, regionId: String): Unit = {
    SummonerUtils.getProfileIcon(username, regionId, 55).map { icon =>
      runOnUiThread(messageAdapter.setSenderDrawable(icon))
      runOnUiThread(messageAdapter.notifyDataSetChanged())
    }
  }

  def setFriendIcon(friendName: String, regionId: String): Unit = {
    SummonerUtils.getProfileIcon(friendName, regionId, 55).map { icon =>
      runOnUiThread(messageAdapter.setRecipientDrawable(icon))
      runOnUiThread(messageAdapter.notifyDataSetChanged())
    }
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
