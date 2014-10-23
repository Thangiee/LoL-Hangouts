package com.thangiee.LoLHangouts.activities

import android.content.Intent

trait TIntent {

  implicit class IntentOp(i: Intent) {
    def args(arguments: (String, Any)*): Intent = {
      for ((k, v) ← arguments) {
        v match {
          case v: String        => i.putExtra(k, v)
          case v: Int           => i.putExtra(k, v)
          case v: Double        => i.putExtra(k, v)
          case v: Float         => i.putExtra(k, v)
          case v: Boolean       => i.putExtra(k, v)
          case v: Serializable  => i.putExtra(k, v)
        }
      }

      i
    }
  }

}
