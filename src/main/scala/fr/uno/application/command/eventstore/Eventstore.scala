package fr.uno.application.command.eventstore

import eventstore._
import fr.uno.application.command.model.format.GameEventJsonFormatter
import fr.uno.domain.event.{UnoEvent, GameStarted, GameEvent}
import play.api.libs.json.{Writes, JsResult, Json}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// generalise
object Eventstore {
	// TODO comment être sur que les évènement sont retournés dans le bon ordre (temporel)
	def read(stream: EventStream.Id)(implicit connection: EsConnection): Future[Seq[JsResult[GameEvent]]] = {
		import GameEventJsonFormatter.gameEventReader

		connection.future(ReadStreamEvents(stream)).map { readStreamEventsCompleted =>
			readStreamEventsCompleted.events.map { event =>
				Json.parse(event.data.data.value.decodeString("UTF-8")).validate[GameEvent]
			}
		}
	}

	def write(event: GameEvent)(implicit connection: EsConnection) = {
		import GameEventJsonFormatter.gameEventWriter

		val aggregatePrefix = "game-"
		val streamId = EventStream(aggregatePrefix + event.gameId.id)

		val data = EventData.Json(eventType = event.toString, data = Json.toJson(event).toString)

		connection.future(WriteEvents(streamId, data :: Nil))
	}

	// TODO maintenir l'ordre (qui est temporel ?)
	// TODO l'insertion est-elle transactionnelle ?
	def write[T](streamId: EventStream.Id, events: T*)(implicit connection: EsConnection, writer: Writes[T]) = {
		val data = events.map { event => EventData.Json(eventType = "jesaispas", data = Json.toJson(event).toString) }
		connection.future(WriteEvents(streamId, data.toList))
	}
}