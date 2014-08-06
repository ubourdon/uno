package fr.uno

import fr.uno.domain.model.Card

sealed trait State {
	def nextPlayer: Int
	def topCard: Card
	def playerCount: Int
	def direction: Int
}

case object EmptyState extends State {
	override def topCard: Card = throw new IllegalStateException("empty state can't have top card fucka !")
	override def nextPlayer: Int = throw new IllegalStateException("empty state can't have last player fucka !")
	override def playerCount: Int = throw new IllegalStateException("empty state can't have player count fucka !")
	override def direction: Int = ???
}

case class NonEmptyState(topCard: Card,
                         nextPlayer: Int,
                         playerCount: Int,
	                     direction: Int = 1) extends State