package com.thangiee.lolhangouts.ui.utils

import android.app.Activity
import android.content.Context
import android.graphics.drawable.{BitmapDrawable, Drawable}
import android.graphics.{Typeface, Bitmap, BitmapFactory}
import android.preference.PreferenceManager
import android.view.View
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import com.ami.fundapter.extractors.{BooleanExtractor, StringExtractor}
import com.ami.fundapter.interfaces.ItemClickListener
import com.nispok.snackbar.Snackbar
import com.nispok.snackbar.listeners.ActionClickListener
import com.thangiee.lolhangouts.R
import de.keyboardsurfer.android.widget.crouton.{Configuration, Crouton, Style}

import scala.language.implicitConversions
import scala.util.Try


trait Implicits extends org.scaloid.common.Implicits with ConversionImplicits with InterfaceImplicits

object Implicits extends Implicits

trait ConversionImplicits {

  implicit class PreferenceConversion(resId: Int)(implicit ctx: Context) {
    private val key = ctx.getResources.getString(resId)
    private val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)

    def pref2Int(default: Int): Int = prefs.getString(key, default.toString).toInt

    def pref2Long(default: Long): Long = prefs.getString(key, default.toString).toLong

    def pref2Float(default: Float): Float = prefs.getString(key, default.toString).toFloat

    def pref2Boolean(default: Boolean = false): Boolean = prefs.getBoolean(key, default)

    def pref2String(default: String): String = prefs.getString(key, default)
  }

  implicit class StringConversion(string: String)(implicit ctx: Context) {
    def makeCrouton(style: Style, duration: Int): Crouton = Crouton.makeText(ctx.asInstanceOf[Activity], string, style, R.id.crouton_holder)
      .setConfiguration(new Configuration.Builder().setDuration(duration).build())

    def croutonWarn(duration: Int = Configuration.DURATION_SHORT): Unit = runOnUiThread(makeCrouton(Style.ALERT, duration).show())

    def croutonConfirm(duration: Int = Configuration.DURATION_SHORT): Unit = runOnUiThread(makeCrouton(Style.CONFIRM, duration).show())

    def croutonInfo(duration: Int = Configuration.DURATION_SHORT): Unit = runOnUiThread(makeCrouton(Style.INFO, duration).show())
  }

  implicit class ResourceConversion(res: Int)(implicit ctx: Context) {
    def makeCrouton(style: Style, duration: Int): Crouton = Crouton.makeText(ctx.asInstanceOf[Activity], res.r2String, style, R.id.crouton_holder)
      .setConfiguration(new Configuration.Builder().setDuration(duration).build())

    def croutonWarn(duration: Int = Configuration.DURATION_SHORT): Unit = runOnUiThread(makeCrouton(Style.ALERT, duration).show())

    def croutonConfirm(duration: Int = Configuration.DURATION_SHORT): Unit = runOnUiThread(makeCrouton(Style.CONFIRM, duration).show())

    def croutonInfo(duration: Int = Configuration.DURATION_SHORT): Unit = runOnUiThread(makeCrouton(Style.INFO, duration).show())
  }

  implicit class DrawableConversion(drawableId: Int)(implicit ctx: Context) {
    def toBitmap: Bitmap = BitmapFactory.decodeResource(ctx.getResources, drawableId)
  }

  implicit class BitMapConversion(bitmap: Bitmap)(implicit ctx: Context) {
    def toDrawable: Drawable = new BitmapDrawable(ctx.getResources, bitmap)
  }


  implicit class ImageAssetConversion(imageFile: ImageFile)(implicit ctx: Context) {
    def toDrawable: Drawable = Try(Drawable.createFromStream(ctx.getAssets.open(imageFile.path), null))
      .getOrElse(ctx.getResources.getDrawable(R.drawable.ic_load_unknown))

    def toBitmap: Bitmap = Try(BitmapFactory.decodeStream(ctx.getAssets.open(imageFile.path)))
      .getOrElse(R.drawable.ic_load_unknown.toBitmap)
  }

  implicit class FontAssetConversion(fontFile: FontFile)(implicit ctx: Context) {
    def toTypeFace: Typeface = Typeface.createFromAsset(ctx.getAssets, fontFile.path)
  }
}

object ConversionImplicits extends ConversionImplicits

trait InterfaceImplicits {

  implicit def func2OnCheckedChangeListener[F](f: (CompoundButton, Boolean) ⇒ F): OnCheckedChangeListener = {
    new OnCheckedChangeListener {
      override def onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean): Unit = f(buttonView, isChecked)
    }
  }

  implicit def func2Runnable[F](f: => F): Runnable = new Runnable() {
    def run() {
      f
    }
  }

  implicit def func2StringExtractor[T](f: T ⇒ String): StringExtractor[T] = {
    new StringExtractor[T] {
      override def getStringValue(p1: T, p2: Int): String = f.apply(p1)
    }
  }

  implicit def func2BooleanExtractor[T](f: T ⇒ Boolean): BooleanExtractor[T] = {
    new BooleanExtractor[T] {
      override def getBooleanValue(p1: T, p2: Int): Boolean = f.apply(p1)
    }
  }

  implicit def func2ItemClickListener[T](f: T ⇒ Unit): ItemClickListener[T] = {
    new ItemClickListener[T] {
      override def onClick(p1: T, p2: Int, p3: View): Unit = f.apply(p1)
    }
  }

  implicit def func2ActionClickListener(f: Snackbar => Unit): ActionClickListener = {
    new ActionClickListener {
      override def onActionClicked(snackbar: Snackbar): Unit = f(snackbar)
    }
  }
}

object InterfaceImplicits extends InterfaceImplicits
