package com.thangiee.lolhangouts.domain.exception

case class UseCaseException(msg: String, errType: UseCaseException.Value) extends Exception(msg)

object UseCaseException extends Enumeration {
  val AuthenticationError, ConnectionError, InternalError, MessageSentError = Value
}
