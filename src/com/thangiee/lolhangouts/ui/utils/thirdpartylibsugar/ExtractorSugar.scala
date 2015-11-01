package com.thangiee.lolhangouts.ui.utils.thirdpartylibsugar

import android.view.View
import com.ami.fundapter.extractors.{BooleanExtractor, StringExtractor}
import com.ami.fundapter.interfaces.ItemClickListener

import scala.language.implicitConversions

trait ExtractorSugar {
  implicit def function2StringExtractor[T](f: T ⇒ String): StringExtractor[T] = {
    (p1: T, p2: Int) => f.apply(p1)
  }

  implicit def function2BooleanExtractor[T](f: T ⇒ Boolean): BooleanExtractor[T] = {
    (p1: T, p2: Int) => f.apply(p1)
  }

  implicit def function2ItemClickListener[T](f: T ⇒  Unit): ItemClickListener[T] = {
    (p1: T, p2: Int, p3: View) => f.apply(p1)
  }
}

object ExtractorSugar extends ExtractorSugar