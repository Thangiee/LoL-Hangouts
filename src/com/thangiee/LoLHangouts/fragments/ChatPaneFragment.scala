package com.thangiee.LoLHangouts.fragments

import java.util.Date

import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.os.{Bundle, SystemClock}
import android.view._
import android.widget.EditText
import com.dd.CircularProgressButton
import com.gitonway.lee.niftynotification.lib.{Configuration, Effects, NiftyNotificationView}
import com.pixplicity.easyprefs.library.Prefs
import com.ruenzuo.messageslistview.adapters.MessageAdapter
import com.ruenzuo.messageslistview.models
import com.ruenzuo.messageslistview.models.MessageType._
import com.ruenzuo.messageslistview.widget.MessagesListView
import com.squareup.picasso.Picasso
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.api.core.{Friend, LoLChat}
import com.thangiee.LoLHangouts.utils.Events.{ReceivedMessage, ShowNiftyNotification}
import com.thangiee.LoLHangouts.utils.{DB, Events, SummonerUtils}
import de.greenrobot.event.EventBus
import de.keyboardsurfer.android.widget.crouton.{Crouton, Style}
import org.scaloid.common.AlertDialogBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.collection.JavaConversions._

class ChatPaneFragment extends TFragment {
  private lazy val sendButton = find[CircularProgressButton](R.id.btn_send_msg)
  private lazy val msgField = find[EditText](R.id.et_msg_field)
  private lazy val friendName = getArguments.getString("name-key")
  private lazy val messageAdapter = new MessageAdapter(ctx, 0)
  private lazy val messageListView = find[MessagesListView](R.id.lsv_chat)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    setHasOptionsMenu(true)
    EventBus.getDefault.register(this)
    view = inflater.inflate(R.layout.chat_pane, container, false)

    sendButton.setOnClickListener((v: View) ⇒ sendMessage())
    sendButton.setIndeterminateProgressMode(true)
    msgField.setHint("send to " + friendName)

    messageAdapter.setRegion(Prefs.getString("region-key", ""))
    messageAdapter.setSenderName(appCtx.currentUser)
    messageAdapter.setRecipientName(friendName)

    messageListView.setAdapter(messageAdapter)
    messageListView.setBackgroundColor(R.color.my_dark_blue.r2Color)

    view
  }

  override def onStart(): Unit = {
    super.onStart()
    setMessagesRead()
    val messageLog = DB.getMessages(appCtx.currentUser, appCtx.activeFriendChat, R.string.pref_max_msg.pref2Int(20))
    messageAdapter.addAll(messageLog) // add all messages
    messageListView.setSelection(messageAdapter.getCount - 1) // scroll to the bottom (newer messages)
  }

  override def onPause(): Unit = {
    messageAdapter.clear()
    super.onPause()
  }

  override def onDestroy(): Unit = {
    EventBus.getDefault.unregister(this, classOf[ReceivedMessage], classOf[ShowNiftyNotification])
    super.onDestroy()
  }

  def setMessagesRead(): Unit = {
    DB.getUnreadMessages(appCtx.currentUser, appCtx.activeFriendChat).map(m ⇒ m.setRead(true).save())
  }

  private def sendMessage() {
    // don't send if blank
    if (msgField.getText.length() == 0) {
      Crouton.makeText(getActivity, "Can't send empty message", Style.INFO).show()
      return
    }

    sendButton.setProgress(50)
//    sendButton.setEnabled(false) // library bug atm
    Future {
      SystemClock.sleep(750)
      if (LoLChat.sendMessage(LoLChat.getFriendByName(friendName).get, msgField.getText.toString)) {
        // if message sent, then save that message to DB
        val msg = new models.Message.MessageBuilder(MESSAGE_TYPE_SENT).text(msgField.getText.toString)
          .date(new Date()).otherPerson(friendName).thisPerson(appCtx.currentUser).isRead(true).build()
        msg.save() // save to DB
        if (isSoundPreferenceOn) MediaPlayer.create(getActivity, R.raw.alert_pm_sent).start() // play sound

        runOnUiThread {
          messageAdapter.add(msg) // add to adapter to show the message on the chat
          msgField.setText("") // clear the message field
          sendButton.setProgress(100) // success state
        }
        SystemClock.sleep(150)
        runOnUiThread {
          sendButton.setProgress(0) // normal state
          sendButton.setEnabled(true)
        }
      } else {  // message failed to send
        runOnUiThread(sendButton.setProgress(-1)) // error state
        SystemClock.sleep(150)
        runOnUiThread {
          Crouton.makeText(getActivity, "Fail to send message", Style.ALERT).show() // alert the user
          sendButton.setProgress(0) // normal state
        }
//        runOnUiThread(sendButton.setEnabled(true))
      }
    }
  }

  override def onCreateOptionsMenu(menu: Menu, inflater: MenuInflater): Unit = {
    inflater.inflate(R.menu.delete, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case R.id.menu_delete => confirmDeleteAllMsg()
      case _ => return false
    }
    super.onOptionsItemSelected(item)
  }

  def onEventMainThread(event: ReceivedMessage): Unit = {
    info("[*]onEvent: received message from "+event.friend.name)
    messageAdapter.add(event.msg) // add received message to adapter to show the message on the chat

    // check sound preference before playing sound
    if (isSoundPreferenceOn) MediaPlayer.create(getActivity, R.raw.alert_pm_receive).start()
  }

  private def isSoundPreferenceOn: Boolean = R.string.pref_notify_sound.pref2Boolean(default = true)

  private def confirmDeleteAllMsg(): Unit = {
    new AlertDialogBuilder(R.string.dialog_delete_title.r2String, R.string.dialog_delete_message.r2String) {
      positiveButton("Delete", {DB.deleteMessages(appCtx.currentUser, appCtx.activeFriendChat); messageAdapter.clear()})
      negativeButton(android.R.string.cancel.r2String)
    }.show()
  }

  def onEvent(event: ShowNiftyNotification): Unit = {
    val cfg=new Configuration.Builder()
      .setAnimDuration(700)
      .setDispalyDuration(3000)
      .setBackgroundColor("#f0022426")
      .setTextColor("#ffbb33")
      .setTextPadding(4)                      //dp
      .setViewHeight(42)                      //dp
      .setTextLines(2)                        //You had better use setViewHeight and setTextLines together
      .setTextGravity(Gravity.CENTER_VERTICAL)
      .build()

    Future {
      val msg = event.msg
      val senderIcon = Picasso.`with`(ctx).load(SummonerUtils.profileIconUrl(msg.getOtherPerson, appCtx.selectedRegion.id))
        .error(R.drawable.ic_load_unknown).get()

      runOnUiThread {
        NiftyNotificationView.build(getActivity, msg.getOtherPerson + ": " + msg.getText, Effects.thumbSlider, R.id.nifty_view, cfg)
          .setIcon(new BitmapDrawable(getResources, senderIcon))
          // switch to the sender chat if notification is clicked
          .setOnClickListener((v: View) ⇒ LoLChat.getFriendByName(msg.getOtherPerson) match {
            case Some(f) ⇒ EventBus.getDefault.post(Events.FriendCardClicked(f))
            case None ⇒
          })
          .show()
      }
    }
  }
}

object ChatPaneFragment {
  def newInstance(friend: Friend): ChatPaneFragment = {
    val bundle = new Bundle()
    bundle.putString("name-key", friend.name)
    val frag = new ChatPaneFragment
    frag.setArguments(bundle)
    frag
  }
}
