package com.thangiee.lolhangouts.ui.main

import android.os.Bundle
import android.view.{MenuItem, View}
import android.widget.{AdapterView, ListView}
import com.ami.fundapter.{BindDictionary, FunDapter}
import com.thangiee.lolhangouts.R
import com.thangiee.lolhangouts.ui.core.{TActivity, UpButton}
import com.thangiee.lolhangouts.ui.utils._
import de.psdev.licensesdialog.SingleLicenseDialog
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20
import de.psdev.licensesdialog.model.Notice

import scala.collection.JavaConversions._

class AboutActivity extends TActivity with UpButton with AdapterView.OnItemClickListener {
  private lazy val aboutItems = List(
    About("License", "License details"),
    About("App version", R.string.app_version.r2String)
  )

  override val layoutId         = R.layout.act_about_screen
  override val snackBarHolderId = R.id.act_about_screen
  private lazy val listView = find[ListView](android.R.id.list)

  override def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)

    val aboutDictionary = new BindDictionary[About]()
    aboutDictionary.addStringField(R.id.tv_title, (about: About) => about.title)
    aboutDictionary.addStringField(R.id.tv_sub_title, (about: About) => about.subTitle)

    val adapter = new FunDapter[About](this, aboutItems, R.layout.about_item, aboutDictionary)
    listView.setAdapter(adapter)
    listView.setOnItemClickListener(this)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case android.R.id.home ⇒ finish(); true // return to the activity that created this one
      case _ => super.onOptionsItemSelected(item)
    }
  }

  override def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long): Unit = {
    aboutItems(position).title match {
      case "License" => showLicence()
      case _         => // do nothing
    }
  }

  private def showLicence(): Unit = {
    val name = R.string.app_name.r2String
    val url = "https://github.com/Thangiee"
    val copyRight = "Copyright 2014 Thang Le <Thangiee0@gmail.com>"
    val licence = new ApacheSoftwareLicense20
    val notice = new Notice(name, url, copyRight, licence)
    new SingleLicenseDialog(this, notice, false).show()
  }

  private case class About(title: String, subTitle: String)
}
