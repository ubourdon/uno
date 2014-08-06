package fr.uno.domain.event

import fr.uno.domain.model._

sealed trait Event

case class GameStarted(gameId: GameId, playerCount: PlayerCount, firstCard: Card) extends Event
case class CardPlayed(gameId: GameId,card: Card, nextPlayer: Player, direction: Direction = 1) extends Event
// TODO enlever default value for direction

case class StartGameAborded(gameId: GameId, playerCount: PlayerCount, firstCard: Card) extends Event
case class PlayerPlayedBadCard(gameId: GameId, player: PlayerCount, cardPlayed: Card) extends Event
case class PlayerPlayedAtWrongTurn(gameId: GameId, player: PlayerCount, cardPlayed: Card) extends Event