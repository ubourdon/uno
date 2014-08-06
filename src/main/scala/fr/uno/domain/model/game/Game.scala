package fr.uno.domain.model.game

import fr.uno.domain.command.{Command, PlayCard, StartGame}
import fr.uno.domain.event._
import fr.uno.domain.model._
import fr.uno.{NonEmptyState, State}

package object Game {
	val minimumPlayerCount = 3

	/**
	 * f(State, Command) = Events
	 * La logique métier est là
	 */
	def decide(state: State, command: Command): List[Event] = {
		(command match {
			case StartGame(id, playerCount, firstCard) => {
				if(playerCount < minimumPlayerCount) StartGameAborded(id, playerCount, firstCard)
				else GameStarted(id, playerCount, firstCard)
			}

			case PlayCard(id, player, cardPlayed) => {
				val newDirection = changeDirection(state.direction, cardPlayed)
				val newPlayer = nextPlayer(player, state.playerCount, newDirection)

				if(isWrongNextPlayer(state, player)) PlayerPlayedAtWrongTurn(id, player, cardPlayed)
				else if(isInvalidCard(state, cardPlayed)) PlayerPlayedBadCard(id, player, cardPlayed)
				else CardPlayed(id, cardPlayed, newPlayer, newDirection)
			}
		}) :: Nil
	}

	/**
	 * f(State, Event) = State
	 * ne prend pas de décision métier, construit juste l'état courant
	 */
	def apply(currentState: State, event: Event): State = {
		event match {
			case GameStarted(id, playerCount, firstCard) => NonEmptyState(firstCard, 0, playerCount, Clockwise)
			case CardPlayed(id, card, nextPlayer, direction) =>
				NonEmptyState(card,
							  nextPlayer,
							  currentState.playerCount,
							  direction
				)
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