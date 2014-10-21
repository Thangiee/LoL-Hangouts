package com.thangiee.LoLHangouts.fragments

import android.os.Bundle
import android.view.MenuItem.OnActionExpandListener
import android.view._
import android.widget._
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.activities.MainActivity
import com.thangiee.common._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

trait SummonerSearch extends TFragment with SearchView.OnQueryTextListener with OnActionExpandListener {
  private var searchView: SearchView = _
  private var regionSpinner: Spinner = _
  val defaultSearchText: String

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    getActivity.asInstanceOf[MainActivity].sideDrawer.setSlideDrawable(R.drawable.ic_navigation_drawer)
    getActivity.getActionBar.setTitle(R.string.app_name.r2String)
    setHasOptionsMenu(true)
    super.onCreateView(inflater, container, savedInstanceState)
  }

  override def onCreateOptionsMenu(menu: Menu, inflater: MenuInflater): Unit = {
    menu.clear()
    inflater.inflate(R.menu.overflow, menu)
    inflater.inflate(R.menu.search, menu)

    // setup regions spinner
    regionSpinner = menu.findItem(R.id.menu_spinner_regions).getActionView.asInstanceOf[Spinner]
    val regionAdapter = ArrayAdapter.createFromResource(ctx, R.array.regions, R.layout.spinner_item)
    regionAdapter.setDropDownViewResource(R.layout.spinner_item)
    regionSpinner.setAdapter(regionAdapter)

    // setup search menu item
    val menuItems = menu.findItem(R.id.menu_search)
    menuItems.setOnActionExpandListener(this)
    menuItems.expandActionView()  // show the search field rather than the icon
  }

  def onSearchCompleted(searchedQuery: String, region: String)

  override def onQueryTextSubmit(query: String): Boolean = {
    val regions = R.array.regions.r2StringArray
    val region = regions(regionSpinner.getSelectedItemPosition) // get select region from spinner

    // check if the summoner name exists
    val respond = Future[String] {
      val url = "https://acs.leagueoflegends.com/v1/players?name=" + query.replace(" ", "") + "&region=" + region
      io.Source.fromURL(url).mkString
    }

    respond onComplete {
      case Success(s) ⇒ onSearchCompleted(query, region)
      case Failure(e) ⇒ ("Could not find " + query + " in " + region).croutonWarn()
    }
    false
  }

  override def onQueryTextChange(newText: String): Boolean = false

  override def onMenuItemActionExpand(item: MenuItem): Boolean = {
    searchView = item.getActionView.asInstanceOf[SearchView]
    searchView.setQueryHint("Summoner")
    searchView.setOnQueryTextListener(this)

    // change default SearchView's search button icon
    val searchImgId = getResources.getIdentifier("android:id/search_button", null, null)
    searchView.findViewById(searchImgId).asInstanceOf[ImageView].setImageResource(R.drawable.ic_action_search)

    searchView.onActionViewExpanded()
    searchView.setQuery(defaultSearchText, false) // set default text in search field
    true
  }

  override def onMenuItemActionCollapse(item: MenuItem): Boolean = true
}
