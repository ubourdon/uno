package fr.uno.application.command.eventstore

import java.net.InetSocketAddress

import _root_.utils.AsyncUtils._
import akka.actor.ActorSystem
import eventstore._
import fr.uno.domain.event.{CardPlayed, GameEvent, GameStarted}
import fr.uno.domain.model._
import fr.uno.infrastructure.eventstore.Eventstore
import fr.uno.infrastructure.eventstore.connection.EventstoreConnection
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite, Matchers}

import scala.util.Try
import scalaz.Scalaz._

/**
 * need to start eventstore to run tests
 * TODO fix it
 * */
class EventstoreTest extends FunSuite with Matchers with BeforeAndAfter with BeforeAndAfterAll {

	//val stream = EventStream("my-stream")
	implicit val settings = Settings(new InetSocketAddress("localhost", 1113))
	implicit val testConn = EsConnection(ActorSystem("test-eventstore"), settings)

	trait TestConnection extends EventstoreConnection { val connection = testConn }
	object TestEventstore extends Eventstore with TestConnection
	
	override def beforeAll = {
		// TODO start eventstore
	}

	override def afterAll = {
		// TODO close eventstore
	}

	before {
		Try { testConn.future(DeleteStream(stream1)) |> sync() }
		Try { testConn.future(DeleteStream(stream2)) |> sync() }
	}

	val (event, stream1) = {
		val event1 = GameStarted(GameId("1"), 3, Card(Red, NumericCardValue(0)))
		val event2 = CardPlayed(GameId("1"), Card(Red, NumericCardValue(1)),1)
		val streamId = EventStream.Id(s"game-${event1.gameId.id}")
		(event1, streamId)
	}

	val (events, stream2) = {
		val event1 = GameStarted(GameId("2"), 3, Card(Red, NumericCardValue(0)))
		val event2 = CardPlayed(GameId("2"), Card(Red, NumericCardValue(1)),1)
		val streamId = EventStream.Id(s"game-${event1.gameId.id}")
		(event1 :: event2 :: Nil, streamId)
	}

	test("store events in stream") {
		import fr.uno.application.command.model.format.GameEventJsonFormatter.{gameEventReader, gameEventWriter}

		TestEventstore.write(stream1, event) |> sync() // stream Id = aggregatRoot name + id instance agreggatRoot
		(TestEventstore.read[GameEvent](stream1) |> sync()).map(_.get) shouldBe event :: Nil
	}

	test("store events lists") {
		import fr.uno.application.command.model.format.GameEventJsonFormatter.{gameEventReader, gameEventWriter}

		TestEventstore.write(stream2, events) |> sync()
		(TestEventstore.read[GameEvent](stream2) |> sync()).map(_.get) shouldBe events
	}

	ignore("stream not found case"){}
}