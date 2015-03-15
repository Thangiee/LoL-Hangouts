package com.thangiee.lolhangouts.data

import com.thangiee.lolhangouts.data.datasources.api.CachingApiCaller

package object datasources {
  implicit val cachingApiCaller = CachingApiCaller
}
