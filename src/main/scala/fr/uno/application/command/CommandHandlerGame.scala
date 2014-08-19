package fr.uno.application.command

import _root_.eventstore.{ReadStreamEvents, EventStream, EsConnection}
import fr.uno.domain.command.Command

// The discard pile command handler is the link
// between the command, the event store and the aggregate
// This version loads the aggregate from scratch for each command
// This is usually ok for aggregates with a small number of events
object CommandHandlerGame { // TODO implement

	def CommandHandler(command: Command)(implicit connection: EsConnection) = ???/*{
		import fr.uno.domain.model.game.Game._

		val storedEvents = readStream(EventStream(s"game-${command.gameId.id}"))

		storedEvents.map { readStreamEvents =>
			val events: List[Event] = readStreamEvents.events.map { e => e.data.data. }


				.foldLeft(EmptyState: State) { (currentState, event) => apply(currentState, event) }
		}
		val currentState =

	}*/

	private def readStream(stream: EventStream.Id)(implicit connection: EsConnection) = connection.future(ReadStreamEvents(stream))

	// build aggregate from eventstore
		// apply
	// Play the command
		// decide
	// store events in eventstore

}