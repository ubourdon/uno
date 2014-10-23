package fr.uno.infrastructure.eventstore

import eventstore._
import fr.uno.infrastructure.eventstore.connection.{EventstoreProdConnection, EventstoreConnection}
import play.api.libs.json.{JsResult, Json, Reads, Writes}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Eventstore extends EventstoreConnection {
	// TODO comment être sur que les évènement sont retournés dans le bon ordre (temporel)
	def read[T](streamId: EventStream.Id)(implicit reader: Reads[T]): Future[Seq[JsResult[T]]] = {
		connection.future(ReadStreamEvents(streamId)).map { readStreamEventsCompleted =>
			readStreamEventsCompleted.events.map { event =>
				Json.parse(event.data.data.value.decodeString("UTF-8")).validate[T]
			}
		}
	}

	// TODO maintenir l'ordre (qui est temporel ?)
	// TODO l'insertion est-elle transactionnelle ?
	// TODO eventType ?
	def write[T](streamId: EventStream.Id, events: Seq[T])(implicit writer: Writes[T]): Future[WriteEventsCompleted] = {

		val data = events.map { event =>
			EventData.Json(eventType = event.getClass.toString, data = Json.toJson(event).toString)
		}

		connection.future(WriteEvents(streamId, data.toList))
	}

	def write[T](streamId: EventStream.Id, event: T)(implicit writer: Writes[T]): Future[WriteEventsCompleted] =
		write(streamId, event :: Nil)
}

object Eventstore extends Eventstore with EventstoreProdConnection