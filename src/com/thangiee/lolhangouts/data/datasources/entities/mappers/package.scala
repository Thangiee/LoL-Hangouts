package com.thangiee.lolhangouts.data.datasources.entities

import java.text.{DecimalFormatSymbols, DecimalFormat}
import java.util.Locale

package object mappers {

  implicit class Rounding(number: Double) {
    def roundTo(DecimalPlace: Int): Double = {
      if (number.isNaN) return 0.0

      // need to use Locale.US otherwise this throw NumberFormatException: Invalid double
      // on phones that are set on a language that use comma to denote decimal
      new DecimalFormat("###." + ("#" * DecimalPlace), new DecimalFormatSymbols(Locale.US)).format(number).toDouble
    }
  }
}
