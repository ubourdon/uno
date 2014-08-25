package fr.uno.application.command.eventstore

import java.net.InetSocketAddress

import _root_.utils.AsyncUtils._
import akka.actor.ActorSystem
import eventstore._
import fr.uno.domain.event.GameStarted
import fr.uno.domain.model._
import fr.uno.utils.ops._
import org.scalatest.{BeforeAndAfter, FunSuite, Matchers}

import scala.concurrent.Future
import scala.concurrent.duration._


/** need to start eventstore to run tests */
class EventstoreTest extends FunSuite with Matchers with BeforeAndAfter {

	val stream = EventStream("my-stream")
	implicit val settings = Settings(new InetSocketAddress("localhost", 1113))
	implicit val connection = EsConnection(ActorSystem("coucou"), settings)

	after { // TODO delete stream
		//try { connection.future(DeleteStream(stream)) |> sync() }
		//try { connection.future(DeleteStream(EventStream("game-1"))) |> sync() }
	}

	test("read-write event") {
		connection.future(WriteEvents(stream, List(EventData("my-event")))) |> sync()

		val readEvent: Future[ReadEventCompleted] = connection.future(ReadEvent(stream))

		(readEvent |> sync(3 seconds)).event.data.eventType shouldBe "my-event"
	}

	test("store Game Event") {
		Eventstore.write(GameStarted(GameId("1"), 3, Card(Red, NumericCardValue(0)))) |> sync()

		(Eventstore.read(EventStream("game-1")) |> sync()).get shouldBe GameStarted(GameId("1"), 3, Card(Red, NumericCardValue(0)))
	}

	//test("stream not found case")
}