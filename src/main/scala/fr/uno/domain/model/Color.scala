package fr.uno.domain.model

sealed trait Color

case object Red extends Color
case object Blue extends Color
case object Green extends Color
case object Yellow extends Color

sealed trait CardValue

case class NumericCardValue(value: Int) extends CardValue { require(value <= 9 && value >= 0) }
case object KickBackCardValue extends CardValue


case class Card(color: Color, value: CardValue)

case class GameId(id: String)