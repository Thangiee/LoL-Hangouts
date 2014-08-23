package com.thangiee.LoLHangouts.activities

import android.os.Bundle
import android.webkit.{JavascriptInterface, WebView, WebViewClient}
import com.thangiee.LoLHangouts.R
import com.thangiee.LoLHangouts.api.stats.LoLNexus
import org.jsoup.Jsoup

class LiveGameActivity extends TActivity {
  var lolNexus: LoLNexus = _

  protected override def onCreate(b: Bundle): Unit = {
    super.onCreate(b)
    val summonerName = getIntent.getStringExtra("name-key")
    val region = getIntent.getStringExtra("region-key")
    lolNexus = new LoLNexus(summonerName, region)
    setContentView(R.layout.live_game_screen)
    val browser = find[WebView](R.id.webView)
    browser.getSettings.setJavaScriptEnabled(true)
    browser.addJavascriptInterface(new MyJavaScriptInterface, "HTMLOUT")

    browser.setWebViewClient(new WebViewClient {
      override def onPageFinished(view: WebView, url: String): Unit =
        browser.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');")
    })

    browser.loadUrl(lolNexus.url)
    println("HERE")
  }

  private def initViews(): Unit = {
    lolNexus.allPlayers.map(p ⇒ println(p.name))
    lolNexus.opponents
  }

  class MyJavaScriptInterface {
    @JavascriptInterface
    def processHTML(html: String): Unit = {
      lolNexus.doc = Jsoup.parse(html)
      println(lolNexus.doc == null)
      initViews()
    }
  }
}
