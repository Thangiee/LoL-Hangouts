package com.thangiee.lolhangouts.domain.exception

case class GetMessageException(throwable: Throwable) extends Exception(throwable)

