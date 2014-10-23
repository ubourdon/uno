package fr.uno.application.command

import java.net.InetSocketAddress

import _root_.eventstore._
import akka.actor.ActorSystem
import _root_.utils.AsyncUtils._
import eventstore._
import fr.uno.domain.command.StartGame
import fr.uno.domain.model.{NumericCardValue, Red, Card, GameId}
import org.scalatest.{Matchers, FunSuite}
import CommandHandlerGame.CommandHandler
import fr.uno.utils.ops.ThrushOps

import scala.concurrent.Future

// TODO Integration test - do test in memory
class CommandHandlerGameTest extends FunSuite with Matchers {
	/*val settings = Settings(new InetSocketAddress("192.168.1.118", 1113))
	implicit val connection = EsConnection(ActorSystem("coucou"), settings)*/

	ignore("when send StartGame to CommandHandlerGame, should store GameStarted event") {
	        // TODO use IOMonad to perform test without mocks

				/*StartGame(GameId("coco"), 3, Card(Red, NumericCardValue(0))) |> CommandHandler

		sync(readEvent("game-coco")).event.data.data shouldBe ???*/
	}

	def readEvent(streamId: String)(implicit connection: EsConnection): Future[ReadEventCompleted] =
		connection.future(ReadEvent(EventStream.Id(streamId)))
}