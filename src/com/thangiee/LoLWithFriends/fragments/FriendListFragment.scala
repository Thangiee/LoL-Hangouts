package com.thangiee.LoLWithFriends.fragments

import android.os.{Bundle, SystemClock}
import android.view.{LayoutInflater, View, ViewGroup}
import com.thangiee.LoLWithFriends.R
import com.thangiee.LoLWithFriends.api.LoLChat
import com.thangiee.LoLWithFriends.utils.Events.{RefreshFriendList, RefreshSummonerCard}
import com.thangiee.LoLWithFriends.views.{SummonerBaseCard, SummonerOffCard, SummonerOnCard}
import de.greenrobot.event.EventBus
import it.gmariotti.cardslib.library.internal.CardArrayAdapter
import it.gmariotti.cardslib.library.view.CardListView

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FriendListFragment extends SFragment {
  private val cards = scala.collection.mutable.ArrayBuffer[SummonerBaseCard]()
  private lazy val cardArrayAdapter = new CardArrayAdapter(getActivity, cards)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    EventBus.getDefault.register(this)
    view = inflater.inflate(R.layout.friend_list_pane, container, false)
    val listView = find[CardListView](R.id.list_summoner_card)

    SystemClock.sleep(100)
    cards ++= getOrderedFriendCardList
    cardArrayAdapter.setNotifyOnChange(false)
    cardArrayAdapter.setInnerViewTypeCount(2) // important with different inner layout
    listView.setAdapter(cardArrayAdapter)

    view
  }

  override def onDestroy(): Unit = {
    EventBus.getDefault.unregister(this, classOf[RefreshFriendList], classOf[RefreshSummonerCard])
    super.onDestroy()
  }

  def findCardByName(name: String): Option[SummonerBaseCard] =  {
    for (i <- 0 until cardArrayAdapter.getCount) {
      val baseCard = cardArrayAdapter.getItem(i).asInstanceOf[SummonerBaseCard]
      if (baseCard.cardName == name) return Some(baseCard)
    }
    None
  }

  private def refreshFriendList() {
    Future {
      cards.clear()
      cards ++= getOrderedFriendCardList
      getActivity.runOnUiThread(new Runnable {
        override def run(): Unit = cardArrayAdapter.notifyDataSetChanged()
      })
    }
  }

  /**
   * get a list of cards with online friend cards ordered first
   *
   * @return  list of cards
   */
  private def getOrderedFriendCardList: scala.collection.mutable.ArrayBuffer[SummonerBaseCard] = {
    val cards = scala.collection.mutable.ArrayBuffer[SummonerBaseCard]()
    cards.++=(for (f <- LoLChat.onlineFriends) yield new SummonerOnCard(getActivity, f))
    cards.++=(for (f <- LoLChat.offlineFriends) yield new SummonerOffCard(getActivity, f))
  }

  def onEvent(event: RefreshFriendList): Unit = {
    info("[*]onEvent: request to refresh friend list")
    refreshFriendList()
  }

  def onEventMainThread(event: RefreshSummonerCard): Unit = {
    info("[*]onEvent: request to refresh "+event.summoner.name+"summoner card")
    findCardByName(event.summoner.name) match {
      case Some(card) => info("[+]Found card"); card.refreshCard()
      case None       => warn("[-]No card found")
    }
    cardArrayAdapter.notifyDataSetChanged()
  }
}
