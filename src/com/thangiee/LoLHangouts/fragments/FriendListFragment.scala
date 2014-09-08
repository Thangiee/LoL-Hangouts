package com.thangiee.LoLHangouts.fragments

import android.app.Activity
import android.os.{Bundle, Handler}
import android.view.{LayoutInflater, View, ViewGroup}
import com.devspark.progressfragment.ProgressFragment
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.api.core.LoLChat
import com.thangiee.LoLHangouts.utils.Events.{RefreshFriendCard, RefreshFriendList}
import com.thangiee.LoLHangouts.views.{FriendBaseCard, FriendOffCard, FriendOnCard}
import de.greenrobot.event.EventBus
import de.keyboardsurfer.android.widget.crouton.{Crouton, Configuration, Style}
import it.gmariotti.cardslib.library.internal.CardArrayAdapter
import it.gmariotti.cardslib.library.view.CardListView
import org.jivesoftware.smack.packet.Presence

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FriendListFragment extends ProgressFragment with TFragment {
  private val cards = scala.collection.mutable.ArrayBuffer[FriendBaseCard]()
  private var cardArrayAdapter: CardArrayAdapter = _
  private var handler: Handler = _
  private lazy val autoRefreshTask = new Runnable {
    override def run(): Unit = {
      info("[*] Auto refresh friend list")
      refreshFriendList()
      handler.postDelayed(this, 60 * 1000)
    }
  }

  override def onAttach(activity: Activity): Unit = {
    super.onAttach(activity)
    cardArrayAdapter = new CardArrayAdapter(activity, cards)
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    EventBus.getDefault.register(this)
    view = inflater.inflate(R.layout.friend_list_pane, null)
    handler = new Handler()
    inflater.inflate(R.layout.progress_container, container, false)
  }

  override def onResume(): Unit = {
    super.onResume()
    setContentView(view)
    setContentShown(false) // show loading bar
    val listView = find[CardListView](R.id.list_summoner_card)

    populateFriendCardList.onComplete { _ ⇒
      runOnUiThread {
        cardArrayAdapter.setNotifyOnChange(false)
        cardArrayAdapter.setInnerViewTypeCount(2) // important with different inner layout
        val animationAdapter = new AlphaInAnimationAdapter(cardArrayAdapter)
        animationAdapter.setAbsListView(listView)
        listView.setExternalAdapter(animationAdapter, cardArrayAdapter)
        setContentShown(true) // hide loading bar and show content
      }
    }
    // automatically refresh friend list every 1 minute
    handler.postDelayed(autoRefreshTask, 60 * 1000)
  }

  override def onStart(): Unit = {
    super.onStart()
    // show warning when in offline mode
    if (LoLChat.presenceType() == Presence.Type.unavailable) {
      val customStyle = new Style.Builder().setBackgroundColor(R.color.offline_warning).build()
      Crouton.makeText(ctx.asInstanceOf[Activity], R.string.offline_mode_warning.r2String, customStyle)
        .setConfiguration(new Configuration.Builder().setDuration(Configuration.DURATION_LONG).build()).show()
    }
  }

  override def onPause(): Unit = {
    handler.removeCallbacks(autoRefreshTask)
    super.onPause()
  }

  override def onDestroy(): Unit = {
    EventBus.getDefault.unregister(this, classOf[RefreshFriendList], classOf[RefreshFriendCard])
    super.onDestroy()
  }

  def findCardByName(name: String): Option[FriendBaseCard] = {
    for (i <- 0 until cardArrayAdapter.getCount) {
      val baseCard = cardArrayAdapter.getItem(i).asInstanceOf[FriendBaseCard] // get the card view
      if (baseCard.cardName == name) return Some(baseCard)
    }
    None
  }

  private def refreshFriendList(): Unit = {
    populateFriendCardList.onComplete(_ ⇒ runOnUiThread(cardArrayAdapter.notifyDataSetChanged()))
  }

  private def populateFriendCardList: Future[Unit] = {
    Future[Unit] {
      val (friendsOn, friendsOff) = (LoLChat.onlineFriends, LoLChat.offlineFriends)
      runOnUiThread {
        cards.clear()
        cards.++=(for (f <- friendsOn) yield new FriendOnCard(getActivity, f)) // online friends first
        cards.++=(for (f <- friendsOff) yield new FriendOffCard(getActivity, f))
      }
    }
  }

  def onEvent(event: RefreshFriendList): Unit = {
    info("[*]onEvent: request to refresh friend list")
    refreshFriendList()
  }

  def onEventMainThread(event: RefreshFriendCard): Unit = {
    info("[*]onEvent: request to refresh " + event.friend.name + "friend card")
    findCardByName(event.friend.name) match {
      case Some(card) => info("[+]Found card"); card.refreshCard()
      case None => warn("[-]No card found")
    }
    cardArrayAdapter.notifyDataSetChanged()
  }
}
