package fr.uno.domain.model

sealed trait CardValue

case class NumericCardValue(value: Int) extends CardValue { require(value <= 9 && value >= 0) } // TODO don't use runtime control
case object KickBackCardValue extends CardValue