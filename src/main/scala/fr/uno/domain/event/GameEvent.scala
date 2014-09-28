package fr.uno.domain.event

import fr.uno.domain.model._

sealed trait GameEvent extends UnoEvent {
	def gameId: GameId
}

case class GameStarted(gameId: GameId, playerCount: PlayerCount, firstCard: Card) extends GameEvent
case class CardPlayed(gameId: GameId,card: Card, nextPlayer: Player, direction: Direction) extends GameEvent
//case class DirectionChanged(gameId: GameId, direction: Direction) extends GameEvent

case class StartGameAborded(gameId: GameId, playerCount: PlayerCount, card: Card) extends GameEvent
case class PlayerPlayedBadCard(gameId: GameId, player: PlayerCount, badCardPlayed: Card) extends GameEvent
case class PlayerPlayedAtWrongTurn(gameId: GameId, player: PlayerCount, cardPlayedAtWrongTurn: Card) extends GameEvent