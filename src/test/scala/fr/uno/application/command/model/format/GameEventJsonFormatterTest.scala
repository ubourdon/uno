package fr.uno.application.command.model.format

import fr.uno.domain.event._
import fr.uno.domain.model._
import org.scalatest.{FunSuite, Matchers}

class GameEventJsonFormatterTest extends FunSuite with Matchers {

	test("Game Started event json serialize/deserialize") {
		import fr.uno.application.command.model.format.GameEventJsonFormatter.{gameEventReader, gameEventWriter}
		import play.api.libs.json.Json

		val event_numericCard = GameStarted(GameId("1"), 3, Card(Red, NumericCardValue(0)))
		Json.toJson(event_numericCard).validate[GameEvent].get shouldBe event_numericCard

		val event_kickbackCard = GameStarted(GameId("1"), 3, Card(Red, KickBackCardValue))
		Json.toJson(event_kickbackCard).validate[GameEvent].get shouldBe event_kickbackCard
	}

	test("Card Played event json serialize/deserialize") {
		import fr.uno.application.command.model.format.GameEventJsonFormatter.{gameEventReader, gameEventWriter}
		import play.api.libs.json.Json

		val event_clockwise = CardPlayed(GameId("1"), Card(Red, NumericCardValue(0)), 0, Clockwise)
		Json.toJson(event_clockwise).validate[GameEvent].get shouldBe event_clockwise

		val event_counterclockwise = CardPlayed(GameId("1"), Card(Red, NumericCardValue(0)), 0, CounterClockwise)
		Json.toJson(event_counterclockwise).validate[GameEvent].get shouldBe event_counterclockwise
	}

	test("StartGame Aborded event json serialize/deserialize") {
		import fr.uno.application.command.model.format.GameEventJsonFormatter.{gameEventReader, gameEventWriter}
		import play.api.libs.json.Json

		val event = StartGameAborded(GameId("1"), 0, Card(Red, NumericCardValue(0)))
		Json.toJson(event).validate[GameEvent].get shouldBe event
	}

	test("Player Played BadCard event json serialize/deserialize") {
		import fr.uno.application.command.model.format.GameEventJsonFormatter.{gameEventReader, gameEventWriter}
		import play.api.libs.json.Json

		val event = PlayerPlayedBadCard(GameId("1"), 0, Card(Red, NumericCardValue(0)))
		Json.toJson(event).validate[GameEvent].get shouldBe event
	}

	test("Player Played AtWrongTurn event json serialize/deserialize") {
		import fr.uno.application.command.model.format.GameEventJsonFormatter.{gameEventReader, gameEventWriter}
		import play.api.libs.json.Json

		val event = PlayerPlayedAtWrongTurn(GameId("1"), 0, Card(Red, NumericCardValue(0)))
		Json.toJson(event).validate[GameEvent].get shouldBe event
	}
}