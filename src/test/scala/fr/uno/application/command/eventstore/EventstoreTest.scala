package fr.uno.application.command.eventstore

import java.net.InetSocketAddress

import _root_.utils.AsyncUtils._
import akka.actor.ActorSystem
import eventstore._
import fr.uno.domain.command.StartGame
import fr.uno.domain.event.GameStarted
import fr.uno.domain.model._
import org.scalatest.{BeforeAndAfter, FunSuite, Matchers}
import play.api.libs.json.{JsString, JsPath, JsSuccess}

import scala.concurrent.Future
import scala.concurrent.duration._

// need to start eventstore to run tests
class EventstoreTest extends FunSuite with Matchers with BeforeAndAfter {

	/*val stream = EventStream("my-stream")
	implicit val settings = Settings(new InetSocketAddress("localhost", 1113))
	implicit val connection = EsConnection(ActorSystem("coucou"), settings)

	before {
		// TODO clean database
	}

	test("spike") {
		import play.api.libs.json.Json
		import fr.uno.application.command.model.format.GameEventJsonFormatter._

		Json.fromJson(Json.toJson(NumericCardValue(0))).get shouldBe NumericCardValue(0)
		Json.fromJson(Json.toJson(KickBackCardValue)).get shouldBe KickBackCardValue



		//Json.toJson(GameStarted(GameId("1"), 3, Card(Red, NumericCardValue(0)))).toString() |> println

		val event = GameStarted(GameId("1"), 3, Card(Red, NumericCardValue(0)))

		val json = """{"gameId":{"id":"1"},"playerCount":3,"firstCard":{"color":"red","value":0}}"""
		//Json.fromJson(json |> Json.parse) shouldBe event
	}

	ignore("read-write event") { // ca marche
		val writeEvents: Future[WriteEventsCompleted] = connection.future(WriteEvents(stream, List(EventData("my-event"))))

		val readEvent: Future[ReadEventCompleted] = connection.future(ReadEvent(stream))

		sync(readEvent, 3 seconds).event.data.eventType shouldBe "my-event"
	}

	ignore("store Game Event") { // ca marche
		Eventstore.write(GameStarted(GameId("1"), 3, Card(Red, NumericCardValue(0))))

		sync(Eventstore.read(EventStream("game-1"))) shouldBe GameStarted(GameId("1"), 3, Card(Red, NumericCardValue(0)))
	}

	ignore("sss") {
		import fr.uno.application.command.CommandHandlerGame._
		import fr.uno.utils.ops.ThrushOps

		StartGame(GameId("coco"), 3, Card(Red, NumericCardValue(0))) |> CommandHandler

	}*/
}