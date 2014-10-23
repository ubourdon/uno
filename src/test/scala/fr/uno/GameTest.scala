package fr.uno

import fr.uno.domain.command.{PlayCard, StartGame, Command}
import fr.uno.domain.model.game.{EmptyState, State, Game}
import fr.uno.domain.event._
import fr.uno.domain.model._
import org.scalatest.{Matchers, FunSuite}
import Game.{MINIMUM_PLAYER_COUNT, FIRST_PLAYER}
import scalaz.Scalaz._

class GameTest extends FunSuite with Matchers {
	val FROM_SCRATCH_EVENTS = Nil
	val GAME_ID = GameId("theId")

	val START_GAME = StartGame(GAME_ID, MINIMUM_PLAYER_COUNT, Card(Red, NumericCardValue(0)))
	val GAME_STARTED_ZeroRedCard = GameStarted(GAME_ID, MINIMUM_PLAYER_COUNT, Card(Red, NumericCardValue(0)))

	val NO_PLAYER: PlayerCount = 0
	val PLAYER_1: Player = 1
	val PLAYER_2: Player = 2

	test("from scratch, when GameStart, game should be started") {
		Given(FROM_SCRATCH_EVENTS) |> When(START_GAME) |> Then(GAME_STARTED_ZeroRedCard :: Nil)
	}

	test("from scratch, when GameStart, with player count minus than 'minimumPlayerCount', should StartGameAborded") {
		Given(FROM_SCRATCH_EVENTS) |>
		When(StartGame(GameId("theId"), 0, Card(Red, NumericCardValue(0)))) |>
		Then(StartGameAborded(GameId("theId"), NO_PLAYER, Card(Red, NumericCardValue(0))) :: Nil)
	}

	test("StartGame idempotent !") {
		Given(GAME_STARTED_ZeroRedCard :: Nil) |> When(START_GAME) |> Then(GAME_STARTED_ZeroRedCard :: Nil)
	}

	test("given GameStarted, when play correct card, card should be Played") {
		Given { GAME_STARTED_ZeroRedCard :: Nil } |>
		When { PlayCard(GAME_ID, FIRST_PLAYER, Card(Blue, NumericCardValue(0))) } |>
		Then { CardPlayed(GAME_ID, Card(Blue, NumericCardValue(0)), FIRST_PLAYER +1) :: Nil }
	}

	// TODO test by checking error or by expected 'green case' result ????
	test("player can't play uncorrect card [number should match]") {
		Given { GAME_STARTED_ZeroRedCard :: Nil } |>
		When { PlayCard(GAME_ID, FIRST_PLAYER, Card(Blue, NumericCardValue(1))) } |>
		Then { PlayerPlayedBadCard(GAME_ID, NO_PLAYER, Card(Blue, NumericCardValue(1))) :: Nil }
	}

	test("player can play card with same color & different numeric value") {
		Given { GAME_STARTED_ZeroRedCard :: Nil } |>
		When { PlayCard(GAME_ID, FIRST_PLAYER, Card(Red, NumericCardValue(1))) } |>
		Then { CardPlayed(GAME_ID, Card(Red, NumericCardValue(1)), FIRST_PLAYER +1) :: Nil }
	}

	test("player 0 should play at first") {
		Given { GAME_STARTED_ZeroRedCard :: Nil } |>
		When { PlayCard(GAME_ID, PLAYER_1, Card(Blue, NumericCardValue(0))) } |>
		Then { PlayerPlayedAtWrongTurn(GAME_ID, PLAYER_1, Card(Blue, NumericCardValue(0))) :: Nil }
	}

	ignore("[positive case] when play kick back card, then next player become previous player") {
		Given {
			GAME_STARTED_ZeroRedCard ::
			CardPlayed(GAME_ID, Card(Red, KickBackCardValue), PLAYER_2) ::
			Nil
		} |>
		When { PlayCard(GAME_ID, PLAYER_2, Card(Red, NumericCardValue(0))) } |>
		Then {
			CardPlayed(GAME_ID, Card(Red, NumericCardValue(0)), PLAYER_2 -1) :: Nil
		}
	}

	test("[positive case] when play kick back card, then card should be played & Direction should be Changed") {
		Given { GAME_STARTED_ZeroRedCard :: Nil} |>
		When { PlayCard(GAME_ID, FIRST_PLAYER, Card(Red, KickBackCardValue)) } |>
		Then {
			CardPlayed(GAME_ID, Card(Red, KickBackCardValue), PLAYER_1) ::
			DirectionChanged(GAME_ID, CounterClockwise) :: Nil
		}
	}

	test("[error case] when play kick back card, then next player become previous player") {
		Given {
			GAME_STARTED_ZeroRedCard ::
			CardPlayed(GAME_ID, Card(Red, KickBackCardValue), 2) ::
			DirectionChanged(GAME_ID, CounterClockwise) ::
			Nil
		} |>
		When { PlayCard(GAME_ID, PLAYER_1, Card(Blue, NumericCardValue(0))) } |>
		Then { PlayerPlayedAtWrongTurn(GAME_ID, PLAYER_1, Card(Blue, NumericCardValue(0))) :: Nil }
	}

	ignore("when play 'kick back' card, the direction changed") {
		Given {
			GAME_STARTED_ZeroRedCard ::
				CardPlayed(GAME_ID, Card(Red, KickBackCardValue), PLAYER_2) ::
				CardPlayed(GAME_ID, Card(Blue, NumericCardValue(0)), PLAYER_1) ::
				Nil
		} |>
		When { PlayCard(GAME_ID, PLAYER_1, Card(Blue, NumericCardValue(0))) } |>
		Then { CardPlayed(GAME_ID, Card(Blue, NumericCardValue(0)), PLAYER_1 -1) :: Nil }
	}

	/*ignore("spike next player") {
		val playerCount = 3

		(0 + 1) % playerCount shouldBe 1
		(1 + 1) % playerCount shouldBe 2
		(2 + 1) % playerCount shouldBe 0

		//(0 - 1) % playerCount shouldBe 2
		(1 - 1) % playerCount shouldBe 0
		(2 - 1) % playerCount shouldBe 1
	}*/

	/**
	 * "framework" de test
	 * on test le triplet (element neutre, decide, apply)
	 */
	def Given(events: List[GameEvent]): List[GameEvent] = events
	def When(command: Command)(events: List[GameEvent]) = (command, events)
	def Then(expected: List[GameEvent])(when: (Command, List[GameEvent])) = specify(when._2, when._1, expected)

	private def specify(givenEvents: List[GameEvent], command: Command, expectedEvents: List[GameEvent]) = {
		import fr.uno.domain.model.game.Game._

		val currentState = givenEvents.foldLeft(EmptyState: State) { (currentState, event) => apply(currentState, event) }

		decide(currentState, command) shouldBe expectedEvents
	}
}