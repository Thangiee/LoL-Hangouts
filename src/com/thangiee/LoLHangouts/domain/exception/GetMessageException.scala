package com.thangiee.LoLHangouts.domain.exception

case class GetMessageException(throwable: Throwable) extends Exception(throwable)

