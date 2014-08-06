package fr.uno.domain.event

import fr.uno.domain.model.{GameId, Card}

sealed trait Event

case class GameStarted(gameId: GameId, playerCount: Int, firstCard: Card) extends Event
case class CardPlayed(gameId: GameId,card: Card, nextPlayer: Int, direction: Int = 1) extends Event
// TODO enlever default value for direction

case class StartGameAborded(gameId: GameId, playerCount: Int, firstCard: Card) extends Event
case class PlayerPlayedBadCard(gameId: GameId, player: Int, cardPlayed: Card) extends Event
case class PlayerPlayedAtWrongTurn(gameId: GameId, player: Int, cardPlayed: Card) extends Event