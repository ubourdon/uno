package fr.uno.domain.model.game

import fr.uno.domain.model._

sealed trait State {
	def nextPlayer: Player
	def topCard: Card
	def playerCount: PlayerCount
	def direction: Direction
}

case object EmptyState extends State {
	override def topCard = throw new IllegalStateException("empty state can't have top card fucka !")
	override def nextPlayer = throw new IllegalStateException("empty state can't have last player fucka !")
	override def playerCount = throw new IllegalStateException("empty state can't have player count fucka !")
	override def direction: Direction = ???
}

case class NonEmptyState(topCard: Card,
                         nextPlayer: Player,
                         playerCount: Player,
	                     direction: Direction) extends State