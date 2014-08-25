package com.thangiee.LoLHangouts.fragments

import android.app.Activity
import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import com.devspark.progressfragment.ProgressFragment
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.api.LoLChat
import com.thangiee.LoLHangouts.utils.Events.{RefreshFriendList, RefreshSummonerCard}
import com.thangiee.LoLHangouts.views.{SummonerBaseCard, SummonerOffCard, SummonerOnCard}
import de.greenrobot.event.EventBus
import it.gmariotti.cardslib.library.internal.CardArrayAdapter
import it.gmariotti.cardslib.library.view.CardListView

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FriendListFragment extends ProgressFragment with TFragment {
  private val cards = scala.collection.mutable.ArrayBuffer[SummonerBaseCard]()
  private var cardArrayAdapter: CardArrayAdapter = _

  override def onAttach(activity: Activity): Unit = {
    super.onAttach(activity)
    cardArrayAdapter = new CardArrayAdapter(activity, cards)
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    EventBus.getDefault.register(this)
    view = inflater.inflate(R.layout.friend_list_pane, null)
    inflater.inflate(R.layout.progress_container, container, false)
  }

  override def onResume(): Unit = {
    super.onResume()
    setContentView(view)
    setContentShown(false)  // show loading bar
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
  }

  override def onDestroy(): Unit = {
    EventBus.getDefault.unregister(this, classOf[RefreshFriendList], classOf[RefreshSummonerCard])
    super.onDestroy()
  }

  def findCardByName(name: String): Option[SummonerBaseCard] = {
    for (i <- 0 until cardArrayAdapter.getCount) {
      val baseCard = cardArrayAdapter.getItem(i).asInstanceOf[SummonerBaseCard] // get the card view
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
        cards.++=(for (f <- friendsOn) yield new SummonerOnCard(getActivity, f)) // online friends first
        cards.++=(for (f <- friendsOff) yield new SummonerOffCard(getActivity, f))
      }
    }
  }

  def onEvent(event: RefreshFriendList): Unit = {
    info("[*]onEvent: request to refresh friend list")
    refreshFriendList()
  }

  def onEventMainThread(event: RefreshSummonerCard): Unit = {
    info("[*]onEvent: request to refresh " + event.summoner.name + "summoner card")
    findCardByName(event.summoner.name) match {
      case Some(card) => info("[+]Found card"); card.refreshCard()
      case None => warn("[-]No card found")
    }
    cardArrayAdapter.notifyDataSetChanged()
  }
}
