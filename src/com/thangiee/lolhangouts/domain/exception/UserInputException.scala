package com.thangiee.lolhangouts.domain.exception

case class UserInputException(msg: String, errType: UserInputException.Value) extends Exception(msg)

object UserInputException extends Enumeration {
  val EmptyUsername, EmptyPassword, EmptyMessage = Value
}
