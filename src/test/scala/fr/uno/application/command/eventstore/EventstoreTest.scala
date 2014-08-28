package fr.uno.application.command.eventstore

import java.net.InetSocketAddress

import _root_.utils.AsyncUtils._
import akka.actor.ActorSystem
import eventstore.EventStream.Id
import eventstore._
import fr.uno.application.command.model.format.GameEventJsonFormatter
import fr.uno.domain.event.GameStarted
import fr.uno.domain.model._
import fr.uno.utils.ops._
import org.scalatest.{BeforeAndAfter, FunSuite, Matchers}

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Try


/** need to start eventstore to run tests */
class EventstoreTest extends FunSuite with Matchers with BeforeAndAfter {

	val stream = EventStream("my-stream")
	implicit val settings = Settings(new InetSocketAddress("localhost", 1113))
	implicit val connection = EsConnection(ActorSystem("coucou"), settings)

	val (event2, stream2) = {
		val event = GameStarted(GameId("2"), 3, Card(Red, NumericCardValue(0)))
		val streamId = EventStream(s"game-${event.gameId.id}")
		(event, streamId)
	}

	after { // TODO delete stream
		Try { connection.future(DeleteStream(stream)) |> sync() }
		Try { connection.future(DeleteStream(EventStream("game-1"))) |> sync() }
		Try { connection.future(DeleteStream(stream2)) |> sync() }
	}

	test("read-write event") {
		connection.future(WriteEvents(stream, List(EventData("my-event")))) |> sync()

		val readEvent: Future[ReadEventCompleted] = connection.future(ReadEvent(stream))

		(readEvent |> sync(3 seconds)).event.data.eventType shouldBe "my-event"
	}

	test("store Game Event") {
		Eventstore.write(GameStarted(GameId("1"), 3, Card(Red, NumericCardValue(0)))) |> sync()

		(Eventstore.read(EventStream("game-1")) |> sync()).head.get shouldBe GameStarted(GameId("1"), 3, Card(Red, NumericCardValue(0)))
	}

	test("store events in stream") {
		import GameEventJsonFormatter.gameEventWriter

		Eventstore.write(stream2, event2) |> sync() // stream Id = aggregatRoot name + id instance agreggatRoot
		(Eventstore.read(stream2) |> sync()).map { _.get } shouldBe event2 :: Nil
	}

	//test("stream not found case")
}