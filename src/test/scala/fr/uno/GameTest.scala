package fr.uno

import fr.uno.domain.event._
import fr.uno.domain.command.{PlayCard, Command, StartGame}
import fr.uno.domain.model._
import org.scalatest.{Matchers, FunSuite}

class GameTest extends FunSuite with Matchers {
	val minimumPlayerCount = 3

	val fromScratch = Nil

	val startGame = StartGame(GameId("theId"), minimumPlayerCount, firstCard = Card(Red, NumericCardValue(0)))
	val startedZeroRedCard = GameStarted(GameId("theId"), minimumPlayerCount, firstCard = Card(Red, NumericCardValue(0)))


	test("from scratch, when GameStart, game should be started") {
		val given = fromScratch
		val when = startGame
		val then: List[Event] = List(startedZeroRedCard)

		specify(given, when, then)
	}

	test("from scratch, when GameStart, with player count minus than 'minimumPlayerCount', should StartGameAborded") {
		val given = fromScratch
		val when = StartGame(GameId("theId"), 0, firstCard = Card(Red, NumericCardValue(0)))
		val then: List[Event] = List(StartGameAborded(GameId("theId"), 0, firstCard =  Card(Red, NumericCardValue(0))))

		specify(given, when, then)
	}

	test("StartGame idempotent !") {
		val given = List(startedZeroRedCard)
		val when = startGame
		val then: List[Event] = List(startedZeroRedCard)

		specify(given, when, then)
	}

	test("given GameStarted, when play correct card, card should be Played") {
		val given = List(startedZeroRedCard)
		val when = PlayCard(startedZeroRedCard.gameId, 0, Card(Blue, NumericCardValue(0)))
		val then: List[Event] = List(CardPlayed(startedZeroRedCard.gameId, Card(Blue, NumericCardValue(0)), when.player + 1))

		specify(given, when, then)
	}

	// TODO test by checking error or by expected 'green case' result ????
	test("player can't play uncorrect card [number should match]") {
		val given = startedZeroRedCard :: Nil
		val when = PlayCard(startedZeroRedCard.gameId, 0, Card(Blue, NumericCardValue(1)))
		val then: List[Event] = PlayerPlayedBadCard(when.gameId, 0, Card(Blue, NumericCardValue(1))) :: Nil

		specify(given, when, then)
	}

	test("player can play card with same color & different numeric value") {
		val given = startedZeroRedCard :: Nil
		val when = PlayCard(startedZeroRedCard.gameId, 0, Card(Red, NumericCardValue(1)))
		val then: List[Event] = CardPlayed(when.gameId, Card(Red, NumericCardValue(1)), 1, 1) :: Nil

		specify(given, when, then)
	}

	test("player 0 should play at first") {
		val given = startedZeroRedCard :: Nil
		val when = PlayCard(startedZeroRedCard.gameId, 1, Card(Blue, NumericCardValue(0)))
		val then: List[Event] = PlayerPlayedAtWrongTurn(when.gameId, when.player, when.card) :: Nil

		specify(given, when, then)
	}

	test("[positive case] when play kick back card, then next player become previous player") {
		val given =
			startedZeroRedCard ::
			CardPlayed(startedZeroRedCard.gameId, Card(Red, KickBackCardValue), 2, -1) ::
			Nil
		val when = PlayCard(startedZeroRedCard.gameId, 2, Card(Red, NumericCardValue(0)))
		val then: List[Event] = CardPlayed(startedZeroRedCard.gameId, Card(Red, NumericCardValue(0)), 1, -1) :: Nil

		specify(given, when, then)
	}

	test("[error case] when play kick back card, then next player become previous player") {
		val given =
			startedZeroRedCard ::
			CardPlayed(startedZeroRedCard.gameId, Card(Red, KickBackCardValue), 2, -1) ::
			Nil
		val when = PlayCard(startedZeroRedCard.gameId, 1, Card(Blue, NumericCardValue(0)))
		val then: List[Event] = PlayerPlayedAtWrongTurn(when.gameId, when.player, when.card) :: Nil

		specify(given, when, then)
	}

	test("when play 'kick back' card, the direction changed") {
		val given =
			startedZeroRedCard ::
			CardPlayed(startedZeroRedCard.gameId, Card(Red, KickBackCardValue), 2, -1) ::
			CardPlayed(startedZeroRedCard.gameId, Card(Blue, NumericCardValue(0)), 1, -1) ::
			Nil
		val when = PlayCard(startedZeroRedCard.gameId, 1, Card(Blue, NumericCardValue(0)))
		val then: List[Event] = CardPlayed(startedZeroRedCard.gameId, Card(Blue, NumericCardValue(0)), when.player -1, -1) :: Nil

		specify(given, when, then)
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
	 *
	 */
	def specify(givenEvents: List[Event], command: Command, expectedEvents: List[Event]) = {
		import fr.uno.domain.model.game.Game._

		val currentState = givenEvents.foldLeft(EmptyState: State) { (currentState, event) => apply(currentState, event) }

		//println(s"current state : $currentState")
		decide(currentState, command) shouldBe expectedEvents
	}
}