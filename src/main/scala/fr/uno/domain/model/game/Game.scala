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
		command match {
			case StartGame(id, playerCount, firstCard) => {
				(if(playerCount < MINIMUM_PLAYER_COUNT) StartGameAborded(id, playerCount, firstCard)
				else GameStarted(id, playerCount, firstCard)) :: Nil
			}

			case PlayCard(id, player, cardPlayed) => {
				val newPlayer = nextPlayer(player, state)

				if(isWrongNextPlayer(state, player)) PlayerPlayedAtWrongTurn(id, player, cardPlayed) :: Nil
				else if(isInvalidCard(state, cardPlayed)) PlayerPlayedBadCard(id, player, cardPlayed) :: Nil
				else CardPlayed(id, cardPlayed, newPlayer) +: changeDirection(state.direction, cardPlayed, id)
			}
		}
	}

	/**
	 * f(State, Event) = State
	 * ne prend pas de décision métier, construit juste l'état courant
	 */
	def apply(currentState: State, event: GameEvent): State = {
		event match {
			case GameStarted(id, playerCount, firstCard) => NonEmptyState(firstCard, FIRST_PLAYER, playerCount, Clockwise)
			case CardPlayed(id, card, nextPlayer) => NonEmptyState(card, nextPlayer, currentState.playerCount, currentState.direction)
			case DirectionChanged(id, newDirection) =>
				NonEmptyState(currentState.topCard, currentState.nextPlayer, currentState.playerCount, newDirection)
		}
	}

	private def isWrongNextPlayer(state: State, player: Int): Boolean = player != state.nextPlayer

	private def isInvalidCard(state: State, cardPlayed: Card): Boolean =
		state.topCard.value != cardPlayed.value && state.topCard.color != cardPlayed.color

	private def nextPlayer(player: Player, state: State) = (state.direction on player) % state.playerCount

	private def changeDirection(direction: Direction, card: Card, gameId: GameId): List[DirectionChanged] = card.value match {
		case KickBackCardValue => DirectionChanged(gameId, direction.reverse) :: Nil
		case _ => Nil
	}
}