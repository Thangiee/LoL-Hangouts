package com.thangiee.lolhangouts.ui.friendchat

import android.content.Context
import android.text.InputType
import android.widget.FrameLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.github.clans.fab.{FloatingActionButton, FloatingActionMenu}
import com.nhaarman.listviewanimations.appearance.simple.SwingLeftInAnimationAdapter
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.usecases.entities.Friend
import com.thangiee.lolhangouts.data.usecases.{GetFriendsUseCaseImpl, GetUserUseCaseImpl, ManageFriendUseCaseImpl}
import com.thangiee.lolhangouts.ui.core.CustomView
import com.thangiee.lolhangouts.ui.utils._
import it.gmariotti.cardslib.library.internal.CardArrayAdapter
import it.gmariotti.cardslib.library.view.CardListView

import scala.collection.JavaConversions._

class FriendListView(implicit ctx: Context) extends FrameLayout(ctx) with CustomView {
  private lazy val cardListView = find[CardListView](R.id.card_list)
  private lazy val fabMenu      = find[FloatingActionMenu](R.id.fab_menu)

  private val cards            = scala.collection.mutable.ArrayBuffer[FriendBaseCard]()
  private val cardArrayAdapter = new CardArrayAdapter(ctx, cards)

  override protected val presenter = new FriendListPresenter(this, GetFriendsUseCaseImpl(), ManageFriendUseCaseImpl(),
    GetUserUseCaseImpl())

  private var _friendGroupToShow = "all"
  def friendGroupToShow: String = _friendGroupToShow
  def friendGroupToShow_=(groupName: String): Unit = _friendGroupToShow = groupName

  override def onAttached(): Unit = {
    super.onAttached()
    addView(layoutInflater.inflate(R.layout.friend_list_view, this, false))
    fabMenu.hideMenuButton(false)
    find[FloatingActionButton](R.id.fab_send_friend_request).onClick(onFABSendFriendRequestClick())
    find[FloatingActionButton](R.id.fab_create_group).onClick(onFABCreateGroupClick())
    find[FloatingActionButton](R.id.fab_move).onClick(onFABMoveClick())

    cardArrayAdapter.setNotifyOnChange(false)
    cardArrayAdapter.setInnerViewTypeCount(2) // important with different inner layout

    val animationAdapter = new SwingLeftInAnimationAdapter(cardArrayAdapter)
    animationAdapter.setAbsListView(cardListView)
    cardListView.setExternalAdapter(animationAdapter, cardArrayAdapter)
  }

  override def onVisible(): Unit = {
    super.onVisible()
    delay(mills = 700) {fabMenu.showMenuButton(true)}
  }

  def initCardList(onFriends: Seq[Friend], offFriends: Seq[Friend]): Unit = {
    cards.clear()
    cards.++=(onFriends.map(f => FriendOnCard(f)))
    cards.++=(offFriends.map(f => FriendOffCard(f)))
    cardArrayAdapter.notifyDataSetChanged()
  }

  def updateCardContent(friend: Friend): Unit = {
    for (i <- 0 until cardArrayAdapter.getCount) {
      val baseCard = cardArrayAdapter.getItem(i).asInstanceOf[FriendBaseCard] // get the card view
      if (baseCard.cardName.toLowerCase == friend.name.toLowerCase) {
        info(s"[+] Found ${friend.name} card")
        baseCard.update(friend)
        cardArrayAdapter.notifyDataSetChanged()
        return
      }
    }
    warn(s"[-] No card found for ${friend.name}")
  }

  private def onFABSendFriendRequestClick(): Unit = {
    new MaterialDialog.Builder(ctx)
      .title("Send Friend Request")
      .inputType(InputType.TYPE_CLASS_TEXT)
      .input("Summoner name", "", (md: MaterialDialog, input: CharSequence) => presenter.sendFriendRequest(input))
      .positiveText("Send")
      .negativeText("Cancel")
      .show()
  }

  private def onFABCreateGroupClick(): Unit = {
    new MaterialDialog.Builder(ctx)
      .title("Create New Friend Group")
      .inputType(InputType.TYPE_CLASS_TEXT)
      .input("New group name", "", (md: MaterialDialog, input: CharSequence) => presenter.createNewGroup(input))
      .positiveText("Create")
      .negativeText("Cancel")
      .show()
  }

  private def onFABMoveClick(): Unit = {
    var selectedGroup = ""
    var selectedFriends = Array.empty[CharSequence]

    new MaterialDialog.Builder(ctx)
      .title("Move Friends to Group")
      .content("Select a group to move friends into.")
      .items(presenter.getGroupsName.toArray)
      .onSingleChoice((_, _, _, selection) => {selectedGroup = selection.toString; next; true})
      .positiveText("Next")
      .negativeText("Cancel")
      .show()

    def next = new MaterialDialog.Builder(ctx)
      .title("Move Friends to Group")
      .content(s"Select friends to move to $selectedGroup group.")
      .onMultiChoice((_, _, selections) =>
        { selectedFriends = selections; presenter.moveFriends(selectedFriends.toSet, selectedGroup); true })
      .items(presenter.getFriends.view.filter(_.groupName != selectedGroup).map(_.name).sorted.toArray[CharSequence])
      .positiveText("Move")
      .negativeText("Cancel")
      .show()
  }

  def showFriendRequestSent(): Unit = SnackBar(R.string.friend_req_sent.r2String).show()

  def showNewGroupCreated(groupName: String): Unit = SnackBar(R.string.group_created.r2String.format(groupName)).show()

  def showFriendsMoved(numOfFriends: Int, groupName: String): Unit =
    SnackBar(R.string.friends_moved_to_group.r2String.format(numOfFriends, groupName)).show()

  def showInvalidNameError(): Unit = SnackBar(R.string.summoner_not_found.r2String).show()

  def showNoConnectionError(): Unit = SnackBar(R.string.no_connection.r2String).show()

  def showUnexpectedError(): Unit = SnackBar(R.string.unexpected_err.r2String).show()
}
