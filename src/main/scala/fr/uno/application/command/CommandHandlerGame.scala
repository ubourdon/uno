package fr.uno.application.command

import _root_.eventstore.EventStream
import fr.uno.domain.command.Command
import fr.uno.domain.event.GameEvent
import fr.uno.domain.model.game.{EmptyState, State}
import fr.uno.infrastructure.eventstore.Eventstore
import concurrent.ExecutionContext.Implicits.global


// The discard pile command handler is the link
// between the command, the event store and the aggregate
// This version loads the aggregate from scratch for each command
// This is usually ok for aggregates with a small number of events
object CommandHandlerGame {

	import fr.uno.application.command.model.format.GameEventJsonFormatter.{gameEventReader, gameEventWriter}

	def CommandHandler(command: Command) = {
		import fr.uno.domain.model.game.Game._

		val streamId = EventStream.Id(s"game-${command.gameId}")

		val fEvents = Eventstore.read[GameEvent](streamId).map { events =>
			events.map { result =>
				result.fold(
					errors => throw new IllegalStateException(s"event recovery fail : $errors"),
					event => event
				)
			}
		}.map { events =>
			events.foldLeft(EmptyState: State) { (currentState, event) => apply(currentState, event) }
		}
		.map { state => decide(state, command) }
		.map { events => Eventstore.write(streamId, events) }
	}
}