package com.thangiee.lolhangouts.domain.exception

case class DataAccessException(msg: String, errType: DataAccessException.Value) extends Exception(msg)

object DataAccessException extends Enumeration {
  val DataNotFound, GetDataError = Value
}

