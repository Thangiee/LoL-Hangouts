package com.thangiee.LoLWithFriends.fragments

import android.app.Fragment
import android.os.Bundle
import android.view.{LayoutInflater, View, ViewGroup}
import com.thangiee.LoLWithFriends.R
import com.thangiee.LoLWithFriends.api.LoLChat
import com.thangiee.LoLWithFriends.utils.Events.RefreshFriendList
import com.thangiee.LoLWithFriends.views.SummonerCard
import de.greenrobot.event.EventBus
import it.gmariotti.cardslib.library.internal.{Card, CardArrayAdapter}
import it.gmariotti.cardslib.library.view.CardListView

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FriendListFragment extends Fragment {
  val cards = scala.collection.mutable.ArrayBuffer[Card]()
  lazy val cardArrayAdapter = new CardArrayAdapter(getActivity, cards)

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    super.onCreateView(inflater, container, savedInstanceState)
    EventBus.getDefault.registerSticky(this)
    val view = inflater.inflate(R.layout.friend_list_pane, container, false)
    val listView = view.findViewById(R.id.list_summoner_card).asInstanceOf[CardListView]

    Thread.sleep(200)
    cards ++= getOrderedFriendCardList
    cardArrayAdapter.setNotifyOnChange(false)
    listView.setAdapter(cardArrayAdapter)

    view
  }

  override def onDestroy(): Unit = {
    super.onDestroy()
  }

  def refreshFriendList() {
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
  private def getOrderedFriendCardList: scala.collection.mutable.ArrayBuffer[Card] = {
    val cards = scala.collection.mutable.ArrayBuffer[Card]()
    cards.++=(for (f <- LoLChat.onlineFriends) yield new SummonerCard(getActivity, f))
    cards.++=(for (f <- LoLChat.offlineFriends) yield new SummonerCard(getActivity, f))
  }

  def onEvent(event: RefreshFriendList): Unit = refreshFriendList()
}
