package com.thangiee.lolhangouts.ui.core

import android.content.Context
import android.database.MatrixCursor
import android.support.v4.view.MenuItemCompat
import android.support.v4.widget.SimpleCursorAdapter
import android.support.v7.widget.SearchView
import android.support.v7.widget.SearchView.{OnQueryTextListener, OnSuggestionListener}
import android.view.{Menu, MenuInflater, View}
import android.widget.{ArrayAdapter, FrameLayout, Spinner}
import com.balysv.materialmenu.MaterialMenuDrawable
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.usecases.entities.BR
import com.thangiee.lolhangouts.data.usecases.{GetAppDataUseCaseImpl, CheckSummExistUseCaseImpl, GetFriendsUseCaseImpl, GetUserUseCaseImpl}
import com.thangiee.lolhangouts.ui.utils._

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global

abstract class SearchContainer(layoutId: Int)(implicit ctx: Context) extends FrameLayout(ctx)
  with Container with OnQueryTextListener with OnSuggestionListener {

  private var searchView   : SearchView = _
  private var regionSpinner: Spinner    = _
  private val suggestionAdapter = new SimpleCursorAdapter(ctx, android.R.layout.simple_dropdown_item_1line, null,
    Array("name"), Array(android.R.id.text1), 0)

  private val getAppDataUseCase     = GetAppDataUseCaseImpl()
  private val checkSummExistUseCase = CheckSummExistUseCaseImpl()
  private val getUserUseCase        = GetUserUseCaseImpl()
  private val loadFriendList        = GetFriendsUseCaseImpl().loadFriendList()
  private val regions               = R.array.regions.r2StringArray

  override def onAttachedToWindow(): Unit = {
    super.onAttachedToWindow()
    navIcon.setIconState(MaterialMenuDrawable.IconState.BURGER)
  }

  override def onCreateOptionsMenu(menuInflater: MenuInflater, menu: Menu): Boolean = {
    menuInflater.inflate(R.menu.search, menu)

    // setup regions spinner
    val regionAdapter = ArrayAdapter.createFromResource(ctx, R.array.regions, R.layout.spinner_item)
    regionAdapter.setDropDownViewResource(R.layout.spinner_drop_down_item)
    regionSpinner = MenuItemCompat.getActionView(menu.findItem(R.id.menu_spinner_regions)).asInstanceOf[Spinner]
    regionSpinner.setAdapter(regionAdapter)

    // setup search menu item
    val searchItem = menu.findItem(R.id.menu_search)
    searchItem.expandActionView()
    searchView = MenuItemCompat.getActionView(searchItem).asInstanceOf[SearchView]
    searchView.requestFocus()
    searchView.setOnQueryTextListener(this)
    searchView.setSuggestionsAdapter(suggestionAdapter)
    searchView.setOnSuggestionListener(this)
    searchView.setIconified(false)

    getUserUseCase.loadUser().map { user =>
      // set the username as default in the search view
      searchView.setQuery(user.inGameName, false)
    }

    getAppDataUseCase.loadAppData().map(_.selectedRegion.getOrElse(BR)).map { region =>
      // set the default spinner selection to the user region
      regionSpinner.setSelection(regions.indexOf(region.id.toUpperCase))
    }

    true
  }

  override def getView: View = layoutInflater.inflate(layoutId, this, false)

  def onSearchCompleted(query: String, region: String)

  override def onQueryTextSubmit(query: String): Boolean = {
    val i = regionSpinner.getSelectedItemPosition
    info(s"[*] query submitted: $query - ${regions(i)}")
    checkSummExistUseCase.checkExists(query, regions(i)).map { isExists =>
      if (isExists)
        onSearchCompleted(query, regions(i))
      else
        s"Could not find $query in ${regions(i)}".croutonWarn()
    }
    false
  }

  override def onQueryTextChange(query: String): Boolean = {
    val columnNames = Array("_id", "name")
    val cursor = new MatrixCursor(columnNames)

    // populate the cursor with friends name
    loadFriendList.map { friends =>
      val friendMatched = friends.map(_.name).filter(_.toLowerCase.contains(query.toLowerCase)) // filter names that container the query
      (0 to friendMatched.size).zip(friendMatched).foreach(p => cursor.addRow(p.productIterator.toList))
    }

    suggestionAdapter.changeCursor(cursor)
    suggestionAdapter.notifyDataSetChanged()
    false
  }

  override def onSuggestionSelect(i: Int): Boolean = {
    true
  }

  override def onSuggestionClick(i: Int): Boolean = {
    val cursor = suggestionAdapter.getCursor
    if (cursor.moveToPosition(i)) {
      val name = cursor.getString(cursor.getColumnIndex("name"))
      searchView.setQuery(name, true)
    }
    true
  }
}