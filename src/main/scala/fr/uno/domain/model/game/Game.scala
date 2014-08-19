package fr.uno.domain.model.game

import fr.uno.domain.command.{PlayCard, StartGame, Command}
import fr.uno.domain.event._
import fr.uno.domain.model._

package object Game {
	val MINIMUM_PLAYER_COUNT = 3
	val FIRST_PLAYER = 0

	/**
	 * f(State, Command) = Events
	 * La logique métier est là
	 */
	def decide(state: State, command: Command): List[GameEvent] = {
		(command match {
			case StartGame(id, playerCount, firstCard) => {
				if(playerCount < MINIMUM_PLAYER_COUNT) StartGameAborded(id, playerCount, firstCard)
				else GameStarted(id, playerCount, firstCard)
			}

			case PlayCard(id, player, cardPlayed) => {
				val newDirection = changeDirection(state.direction, cardPlayed)
				val newPlayer = nextPlayer(player, state.playerCount, newDirection)

				if(isWrongNextPlayer(state, player)) PlayerPlayedAtWrongTurn(id, player, cardPlayed)
				else if(isInvalidCard(state, cardPlayed)) PlayerPlayedBadCard(id, player, cardPlayed)
				else CardPlayed(id, cardPlayed, newPlayer, newDirection) // TODO emit DirectionChanged(newDirection)
			}
		}) :: Nil
	}

	/**
	 * f(State, Event) = State
	 * ne prend pas de décision métier, construit juste l'état courant
	 */
	def apply(currentState: State, event: GameEvent): State = {
		event match {
			case GameStarted(id, playerCount, firstCard) => NonEmptyState(firstCard, FIRST_PLAYER, playerCount, Clockwise)
			case CardPlayed(id, card, nextPlayer, direction) =>
				NonEmptyState(card, nextPlayer, currentState.playerCount, direction)
		}
	}

	private def isWrongNextPlayer(state: State, player: Int): Boolean = player != state.nextPlayer

	private def isInvalidCard(state: State, cardPlayed: Card): Boolean =
		state.topCard.value != cardPlayed.value && state.topCard.color != cardPlayed.color

	private def nextPlayer(player: Player, playerCount: PlayerCount, direction: Direction) =
		(direction on player) % playerCount

	private def changeDirection(direction: Direction, card: Card): Direction = card.value match {
		case KickBackCardValue => direction.reverse
		case _ => direction
	}
}