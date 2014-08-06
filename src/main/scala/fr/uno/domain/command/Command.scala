package fr.uno.domain.command

import fr.uno.domain.model.{GameId, Card}

sealed trait Command

case class StartGame(gameId: GameId, playerCount: Int, firstCard: Card) extends Command
case class PlayCard(gameId: GameId, player: Int, card: Card) extends Command