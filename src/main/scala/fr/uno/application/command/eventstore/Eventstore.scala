package fr.uno.application.command.eventstore

import eventstore._
import fr.uno.application.command.model.format.GameEventJsonFormatter
import fr.uno.domain.event.GameEvent
import play.api.libs.json.{JsResult, Json}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Eventstore {
	def read(stream: EventStream.Id)(implicit connection: EsConnection): Future[JsResult[GameEvent]] = {
		import GameEventJsonFormatter.gameEventReader

		connection.future(ReadEvent(stream)).map { readEventCompleted =>
			val jsonString = readEventCompleted.event.data.data.value.decodeString("UTF-8")
			Json.parse(jsonString).validate[GameEvent]
		}
	}

	def write(event: GameEvent)(implicit connection: EsConnection) = {
		val aggregatePrefix = "game-"
		val streamId = EventStream(aggregatePrefix + event.gameId.id)

		val data = EventData.Json(eventType = "eventType", data = """"{"data":"the data"}"""")

		connection.future(WriteEvents(streamId, data :: Nil))
	}
}