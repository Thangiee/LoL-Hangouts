package com.thangiee.LoLWithFriends.fragments

import android.content.Intent
import android.os.Bundle
import android.view._
import android.widget._
import com.thangiee.LoLWithFriends.R
import com.thangiee.LoLWithFriends.activities.{MainActivity, ViewOtherSummonerActivity}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

trait SummonerSearch extends SFragment with SearchView.OnQueryTextListener {
  private var searchView: SearchView = _
  private var regionSpinner: Spinner = _

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

    // setup summoner search view
    val menuItems = menu.findItem(R.id.menu_search)
    searchView = menuItems.getActionView.asInstanceOf[SearchView]
    searchView.setQueryHint("Summoner")
    searchView.setOnQueryTextListener(this)

    // change default SearchView's search button icon
    val searchImgId = getResources.getIdentifier("android:id/search_button", null, null)
    searchView.findViewById(searchImgId).asInstanceOf[ImageView].setImageResource(R.drawable.ic_action_search)

    super.onCreateOptionsMenu(menu, inflater)
  }

  override def onQueryTextSubmit(query: String): Boolean = {
    val regions = R.array.regions.r2StringArray
    val region = regions(regionSpinner.getSelectedItemPosition) // get select region from spinner

    // check if the summoner name exists
    val respond = Future[String] {
      val url = "https://acs.leagueoflegends.com/v1/players?name=" + query.replace(" ", "") + "&region=" + region
      io.Source.fromURL(url).mkString
    }

    respond onComplete {
      case Success(s) ⇒ startActivity(new Intent(ctx, classOf[ViewOtherSummonerActivity]).putExtra("name-key", query).putExtra("region-key", region))
      case Failure(e) ⇒ ("Could not find " + query + " in " + region).makeCrouton()
    }

    false
  }

  override def onQueryTextChange(newText: String): Boolean = {
    false
  }
}
