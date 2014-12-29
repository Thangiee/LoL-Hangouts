package com.thangiee.LoLHangouts.domain.utils

case class Listener[T]() {
  private var listeners: List[T => Unit] = Nil

  def addListener(listener: T => Unit) {
    listeners ::= listener
  }

  def notify(ev: T) = for (l <- listeners) l(ev)
}
