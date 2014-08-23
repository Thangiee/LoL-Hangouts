package com.thangiee.LoLHangouts.fragments

import android.app.{Fragment, FragmentManager}
import android.os.Bundle
import android.support.v13.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view._
import android.webkit.{JavascriptInterface, WebView, WebViewClient}
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
  private lazy val browser = new WebView(appCtx)
  private var liveGame: LoLNexus = _

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    view = inflater.inflate(R.layout.view_pager, null)
    setHasOptionsMenu(true)
    inflater.inflate(R.layout.progress_container, container, false)
  }

  override def onActivityCreated(savedInstanceState: Bundle): Unit = {
    super.onActivityCreated(savedInstanceState)
    setContentView(view)
    browser.getSettings.setJavaScriptEnabled(true)
    browser.addJavascriptInterface(new MyJavaScriptInterface, "HTMLOUT")
    browser.setWebViewClient(new WebViewClient {
      override def onPageFinished(view: WebView, url: String): Unit = {
        browser.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
        info("Finish loading")
        setContentEmpty(false) // hide error msg if currently showing
        setContentShown(true) // hide loading bar
      }
    })
    loadData()
  }

  override def onStop(): Unit = {
    if (browser != null) {
      // clean up/free memory
      browser.clearCache(true)
      browser.removeAllViews()
      browser.destroy()
    }
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
      loadData()
      return true
    }
    super.onOptionsItemSelected(item)
  }

  private def loadData(): Unit = {
    setContentShown(false) // show loading bar
    try {
      browser.loadUrl("http://www.lolnexus.com/" + region + "/search?name=" + "Crumbzz")
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

  class MyJavaScriptInterface {
    @JavascriptInterface
    def processHTML(html: String): Unit = {
      info("[*] processing HTML" + html.length)
      liveGame = new LoLNexus(name, region) {
        override protected def fetchDocument: Document = {
          val js = Jsoup.parse(html)
          js
        }
      }

      runOnUiThread {
        pager.setAdapter(adapter)
        tabs.setViewPager(pager)

        // error checking
        if (html.contains("Region Disabled")) {
          warn("[!] region disabled")
          R.string.connection_error_short.r2String.makeCrouton()
          setEmptyText(R.string.server_down)
          setContentEmpty(true) // show error msg
          pager.removeAllViews()
        } else if (html.contains("not currently in a game")) {
          warn("[!] player not in game")
          setEmptyText(name + " is not currently in a game.")
          setContentEmpty(true)
          pager.removeAllViews()
        }

        // clean up/free memory
        browser.clearCache(true)
        browser.removeAllViews()
        browser.destroy()
      }
      info("[+] Got live game successfully")
    }
  }

  class MyPagerAdapter(fm: FragmentManager) extends FragmentStatePagerAdapter(fm) {
    private val titles = List("Your Team", "Opponents")

    override def getPageTitle(position: Int): CharSequence = titles(position)

    override def getItem(position: Int): Fragment = {
      println("here")
      titles(position) match {
        case "Your Team" ⇒ LiveGameTeamFragment.newInstance(liveGame.teammates, 1)
        case "Opponents" ⇒ LiveGameTeamFragment.newInstance(liveGame.opponents, 2)
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