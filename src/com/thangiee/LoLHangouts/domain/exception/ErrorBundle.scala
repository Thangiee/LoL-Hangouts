package com.thangiee.LoLHangouts.domain.exception

case class ErrorBundle(message: String, exception: Option[Exception] = None)
