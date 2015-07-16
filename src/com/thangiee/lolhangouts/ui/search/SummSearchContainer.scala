package com.thangiee.lolhangouts.ui.search

import android.content.Context
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.view._
import android.widget._
import com.balysv.materialmenu.MaterialMenuDrawable
import com.skocken.efficientadapter.lib.adapter.AbsViewHolderAdapter.OnItemClickListener
import com.skocken.efficientadapter.lib.adapter.{AbsViewHolderAdapter, SimpleAdapter}
import com.thangiee.lolchat.region.BR
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.data.usecases._
import com.thangiee.lolhangouts.data.usecases.entities.SummSearchHist
import com.thangiee.lolhangouts.ui.core.Container
import com.thangiee.lolhangouts.ui.utils.{Bad, Good, _}

import scala.collection.JavaConversions._
import scala.concurrent.ExecutionContext.Implicits.global

abstract class SummSearchContainer(implicit ctx: Context) extends RelativeLayout(ctx) with Container
  with OnItemClickListener[SummSearchHist] {

  private var regionSpinner : Spinner  = _
  private var searchMenuIcon: MenuItem = _

  private val searchSuggestions = scala.collection.mutable.ArrayBuffer[SummSearchHist]()
  private val suggestionAdapter = new SimpleAdapter[SummSearchHist](R.layout.line_item_summ_search_result,
    classOf[SummSearchHistViewHolder], searchSuggestions)

  private lazy val searchViewContainer = layoutInflater.inflate(R.layout.search_view_container, this, false)
  private lazy val searchView          = searchViewContainer.find[EditText](R.id.search_view)
  private lazy val suggestionsView        = this.find[RecyclerView](R.id.rv_suggestions)

  private val getAppDataUseCase     = GetAppDataUseCaseImpl()
  private val checkSummExistUseCase = CheckSummExistUseCaseImpl()
  private val loadUser              = GetUserUseCaseImpl().loadUser()
  private val loadSummSearchHist    = ManageSearchHistUseCaseImpl().loadSummSearchHist()
  private val regions               = R.array.regions.r2StringArray

  override def getView: View = this

  def displayView: Option[View]

  def onSearchCompleted(query: String, region: String)

  override def onAttachedToWindow(): Unit = {
    super.onAttachedToWindow()
    navIcon.setIconState(MaterialMenuDrawable.IconState.BURGER)
    addView(layoutInflater.inflate(R.layout.summ_search_container, this, false))
    displayView.foreach(view => this.find[FrameLayout](R.id.content).addView(view))

    suggestionsView.setLayoutManager(new LinearLayoutManager(ctx))
    suggestionsView.setHasFixedSize(false)
    suggestionsView.setAdapter(suggestionAdapter.asInstanceOf[RecyclerView.Adapter[SummSearchHistViewHolder]])
    suggestionAdapter.setOnItemClickListener(this)

    toolbar.addView(searchViewContainer)

    searchView.afterTextChanged(updateSuggestions _)
    searchView.onKey((v, actionId, event) =>
      if (event.getAction == KeyEvent.ACTION_DOWN && event.getKeyCode == KeyEvent.KEYCODE_ENTER) {
        val i = regionSpinner.getSelectedItemPosition
        onSearchSubmit(searchView.txt2str, regions(i), isNewSearch = true)
        true
      } else {
        false
      }
    )

    // set the username as default in the search view
    loadUser.onSuccess {
      case Good(user) => runOnUiThread(searchView.setText(user.inGameName))
      case Bad(_)     => // leave it as blank
    }

    val clearSearchBtn = searchViewContainer.find[ImageView](R.id.search_clear)
    clearSearchBtn.onClick(searchView.setText(""))
  }

  override def onCreateOptionsMenu(menuInflater: MenuInflater, menu: Menu): Boolean = {
    menuInflater.inflate(R.menu.search, menu)
    searchMenuIcon = menu.findItem(R.id.menu_search)

    // setup regions spinner
    val regionAdapter = ArrayAdapter.createFromResource(ctx, R.array.regions, R.layout.spinner_item)
    regionAdapter.setDropDownViewResource(R.layout.spinner_drop_down_item)
    regionSpinner = MenuItemCompat.getActionView(menu.findItem(R.id.menu_spinner_regions)).asInstanceOf[Spinner]
    regionSpinner.setAdapter(regionAdapter)

    // set the default spinner selection to the user region
    getAppDataUseCase.loadAppData().onSuccess {
      case data => runOnUiThread {
        regionSpinner.setSelection(regions.indexOf(data.selectedRegion.getOrElse(BR).id.toUpperCase))
        regionSpinner.setVisibility(View.GONE)
      }
    }

    displaySearchView(visible = true)
    true
  }

  override def onNavIconClick(): Boolean = {
    if (navIcon.getIconState == MaterialMenuDrawable.IconState.ARROW) {
      displaySearchView(visible = false)
      true // consume
    } else {
      super.onNavIconClick()
    }
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case R.id.menu_search =>
        displaySearchView(visible = true)
        true
      case _                => super.onOptionsItemSelected(item)
    }
  }

  override def onItemClick(adp: AbsViewHolderAdapter[SummSearchHist], v: View, res: SummSearchHist, p: Int): Unit = {
    delay(mills = 500) {
      onSearchSubmit(res.name, res.regionId, isNewSearch = false)
      searchView.setText(res.name)
    }
  }

  private def onSearchSubmit(query: String, regionId: String, isNewSearch: Boolean): Unit = {
    info(s"[*] query submitted: $query - $regionId")
    inputMethodManager.hideSoftInputFromWindow(getWindowToken, 0) // hide keyboard

    checkSummExistUseCase.checkExists(query, regionId).map { isExists =>
      if (isExists) {
        if (isNewSearch) ManageSearchHistUseCaseImpl().saveSummSearchHist(query, regionId)
        onSearchCompleted(query, regionId)
      } else {
        SnackBar(R.string.err_summ_search.r2String.format(query, regionId)).show()
      }
    }
  }

  private def updateSuggestions(query: Editable): Unit = {
    searchSuggestions.clear()
    loadSummSearchHist.onSuccess {
      case result =>
        searchSuggestions ++= result.filter(_.name.toLowerCase.contains(query.toString.toLowerCase)) // filter names by query
        runOnUiThread(suggestionAdapter.notifyDataSetChanged())
    }
  }

  private def displaySearchView(visible: Boolean): Unit = {
    if (visible) {
      navIcon.setIconState(MaterialMenuDrawable.IconState.ARROW)

      // hide title, spinner, and search icon
      toolbar.setTitle(null)
      searchMenuIcon.setVisible(false)
      regionSpinner.setVisibility(View.GONE)

      // show the search view, suggestions, and other related things
      searchViewContainer.setVisibility(View.VISIBLE)
      suggestionsView.setVisibility(View.VISIBLE)
      searchView.requestFocus()
      searchView.setSelection(searchView.txt2str.length) // move cursor to the end
      inputMethodManager.showSoftInput(searchView, 0) // show keyboard
    } else {
      navIcon.setIconState(MaterialMenuDrawable.IconState.BURGER)

      // show title, spinner, and search icon
      toolbar.setTitle(R.string.app_name)
      searchMenuIcon.setVisible(true)
      regionSpinner.setVisibility(View.VISIBLE)

      // hide search container and suggestions
      searchViewContainer.setVisibility(View.GONE)
      suggestionsView.setVisibility(View.GONE)
      inputMethodManager.hideSoftInputFromWindow(getWindowToken, 0) // hide keyboard
    }
  }
}