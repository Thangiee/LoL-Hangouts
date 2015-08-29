package com.thangiee.lolhangouts.ui.utils.thirdpartylibsugar

import android.view.View
import com.ami.fundapter.extractors.{BooleanExtractor, StringExtractor}
import com.ami.fundapter.interfaces.ItemClickListener

import scala.language.implicitConversions

trait ExtractorSugar {
  implicit def function2StringExtractor[T](f: T ⇒ String): StringExtractor[T] = {
    new StringExtractor[T] {
      override def getStringValue(p1: T, p2: Int): String = f.apply(p1)
    }
  }

  implicit def function2BooleanExtractor[T](f: T ⇒ Boolean): BooleanExtractor[T] = {
    new BooleanExtractor[T] {
      override def getBooleanValue(p1: T, p2: Int): Boolean = f.apply(p1)
    }
  }

  implicit def function2ItemClickListener[T](f: T ⇒  Unit): ItemClickListener[T] = {
    new ItemClickListener[T] {
      override def onClick(p1: T, p2: Int, p3: View): Unit = f.apply(p1)
    }
  }
}

object ExtractorSugar extends ExtractorSugar