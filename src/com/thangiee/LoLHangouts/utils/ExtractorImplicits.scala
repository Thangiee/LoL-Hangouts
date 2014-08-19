package com.thangiee.LoLHangouts.utils

import com.ami.fundapter.extractors.{BooleanExtractor, StringExtractor}

trait ExtractorImplicits {
  implicit def function2StringExtractor[T](f: T ⇒ String) : StringExtractor[T] = {
    new StringExtractor[T] {
      override def getStringValue(p1: T, p2: Int): String = f.apply(p1)
    }
  }

  implicit def function2BooleanExtractor[T](f: T ⇒ Boolean) : BooleanExtractor[T] = {
    new BooleanExtractor[T] {
      override def getBooleanValue(p1: T, p2: Int): Boolean = f.apply(p1)
    }
  }
}
