package fr.uno.application.command.eventstore

import java.net.InetSocketAddress

import _root_.utils.AsyncUtils._
import akka.actor.ActorSystem
import eventstore._
import fr.uno.application.command.eventstore.connection.EventstoreConnection
import fr.uno.application.command.model.format.GameEventJsonFormatter
import fr.uno.domain.event.GameStarted
import fr.uno.domain.model._
import fr.uno.utils.ops._
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfter, FunSuite, Matchers}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Try


/** need to start eventstore to run tests */
class EventstoreTest extends FunSuite with Matchers with BeforeAndAfter with BeforeAndAfterAll {

	val stream = EventStream("my-stream")
	implicit val settings = Settings(new InetSocketAddress("localhost", 1113))
	implicit val testConn = EsConnection(ActorSystem("coucou"), settings)

	trait TestConnection extends EventstoreConnection { val connection = testConn }
	object TestEventstore extends Eventstore with TestConnection
	
	val (event2, stream2) = {
		val event = GameStarted(GameId("2"), 3, Card(Red, NumericCardValue(0)))
		val streamId = EventStream(s"game-${event.gameId.id}")
		(event, streamId)
	}

	override def beforeAll = {
		// TODO start eventstore
	}

	override def afterAll = {
		// TODO close eventstore
	}

	after {
		//Try { testConn.future(DeleteStream(stream)) |> sync() }
		//Try { testConn.future(DeleteStream(EventStream("game-1"))) |> sync() }
		Try { testConn.future(DeleteStream(stream2)) |> sync() }
	}

	ignore("read-write event") {
		testConn.future(WriteEvents(stream, List(EventData("my-event")))) |> sync()

		val readEvent: Future[ReadEventCompleted] = testConn.future(ReadEvent(stream))

		(readEvent |> sync()).event.data.eventType shouldBe "my-event"
	}

	ignore("store Game Event") {
		Eventstore.write(GameStarted(GameId("1"), 3, Card(Red, NumericCardValue(0)))) |> sync()

		(Eventstore.read(EventStream("game-1")) |> sync()).head.get shouldBe GameStarted(GameId("1"), 3, Card(Red, NumericCardValue(0)))
	}

	test("store events in stream") {
		import GameEventJsonFormatter.gameEventWriter

		TestEventstore.write(stream2, event2) |> sync() // stream Id = aggregatRoot name + id instance agreggatRoot
		(Eventstore.read(stream2) |> sync()).map { _.get } shouldBe event2 :: Nil
	}

	//test("stream not found case")
}