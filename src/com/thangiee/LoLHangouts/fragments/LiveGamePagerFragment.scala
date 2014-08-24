package com.thangiee.LoLHangouts.fragments

import android.app.{Fragment, FragmentManager}
import android.os.Bundle
import android.support.v13.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view._
import android.webkit.{WebViewClient, JavascriptInterface, WebView}
import com.astuetz.PagerSlidingTabStrip
import com.devspark.progressfragment.ProgressFragment
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.api.stats.LoLNexus
import de.keyboardsurfer.android.widget.crouton.Style
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class LiveGamePagerFragment extends ProgressFragment with TFragment {
  private lazy val tabs = find[PagerSlidingTabStrip](R.id.tabs)
  private lazy val pager = find[ViewPager](R.id.pager)
  private lazy val adapter = new MyPagerAdapter(getFragmentManager)
  private lazy val name = getArguments.getString("name-key")
  private lazy val region = getArguments.getString("region-key")
  private var browser: WebView = _
  private var liveGame: LoLNexus = _

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    view = inflater.inflate(R.layout.view_pager, null)
    setHasOptionsMenu(true)
    inflater.inflate(R.layout.progress_container, container, false)
  }

  override def onActivityCreated(savedInstanceState: Bundle): Unit = {
    super.onActivityCreated(savedInstanceState)
    setContentView(view)
    setupBrowser()
    loadData()
  }

  override def onStop(): Unit = {
    cleanUpBrowser()
    super.onStop()
  }

  override def onCreateOptionsMenu(menu: Menu, inflater: MenuInflater): Unit = {
    menu.clear()
    inflater.inflate(R.menu.overflow, menu)
    inflater.inflate(R.menu.refresh, menu)
    super.onCreateOptionsMenu(menu, inflater)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    if (item.getItemId == R.id.menu_refresh) {
      setupBrowser()
      loadData()
      return true
    }
    super.onOptionsItemSelected(item)
  }

  private def loadData(): Unit = {
    setContentShown(false) // show loading bar
    try {
      browser.loadUrl("http://www.lolnexus.com/" + region + "/search?name=" + name)
      info("[*] loading url")
    } catch {
      case e: Exception ⇒
        warn("[!] Failed to get live game because: " + e.getMessage)
        R.string.connection_error_short.r2String.makeCrouton(Style.ALERT)
        setEmptyText(R.string.connection_error_long)
        setContentEmpty(true) // show error msg
        setContentShown(true) // hide loading bar
    }
  }

  private def setupBrowser(): Unit = {
    browser = new WebView(appCtx)
    browser.getSettings.setJavaScriptEnabled(true)
    browser.addJavascriptInterface(new MyJavaScriptInterface, "HTMLOUT")
    browser.setWebViewClient(new WebViewClient {
      override def onPageFinished(view: WebView, url: String): Unit = {
        browser.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
        info("[*] Finish loading")
      }
    })
  }

  private def cleanUpBrowser(): Unit ={
    if (browser != null) {
      // clean up/free memory
      browser.clearCache(true)
      browser.removeAllViews()
      browser.destroy()
    }
  }

  class MyJavaScriptInterface {
    @JavascriptInterface
    def processHTML(html: String): Unit = { // this is called after onPageFinished
      info("[*] processing HTML" + html.length)
      liveGame = new LoLNexus(name, region) {
        override protected def fetchDocument: Document = Jsoup.parse(html)
      }

      runOnUiThread {
        pager.setAdapter(adapter)
        tabs.setViewPager(pager)

        // error checking
        if (html.contains("Region Disabled")) {
          warn("[!] Region disabled")
          R.string.connection_error_short.r2String.makeCrouton()
          setEmptyText(R.string.server_down)
          setContentEmpty(true) // show error msg
          pager.removeAllViews()
        } else if (html.contains("not currently in a game")) {
          info("[-] Player not in game")
          setEmptyText(name + " is not currently in a game.")
          setContentEmpty(true)
          pager.removeAllViews()
        } else if (html.contains("still in champion select")) {
          info("[-] Still in champion select")
          setEmptyText(name + " is still in a champion selection. Try again in a bit.")
          setContentEmpty(true)
          pager.removeAllViews()
        } else {  // no error (that was checked)
          setContentEmpty(false) // hide error msg if currently showing
          info("[+] Got live game successfully")
        }
        cleanUpBrowser()
        setContentShown(true) // hide loading bar
      }
    }
  }

  class MyPagerAdapter(fm: FragmentManager) extends FragmentStatePagerAdapter(fm) {
    private val titles = List("Your Team", "Opponents")

    override def getPageTitle(position: Int): CharSequence = titles(position)

    override def getItem(position: Int): Fragment = {
      titles(position) match {
        case "Your Team" ⇒ LiveGameTeamFragment.newInstance(liveGame.blueTeam, 1, region)
        case "Opponents" ⇒ LiveGameTeamFragment.newInstance(liveGame.purpleTeam, 2, region)
      }
    }

    override def getCount: Int = titles.size
  }
}

object LiveGamePagerFragment {
  def newInstance(summonerName: String, region: String): LiveGamePagerFragment = {
    val bundle = new Bundle()
    bundle.putString("name-key", summonerName)
    bundle.putString("region-key", region)
    val frag = new LiveGamePagerFragment
    frag.setArguments(bundle)
    frag
  }
}