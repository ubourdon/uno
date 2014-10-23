package fr.uno.infrastructure.eventstore.connection

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import eventstore.{EsConnection, Settings}

trait EventstoreProdConnection extends EventstoreConnection {
	val connection = {
		val settings = Settings(new InetSocketAddress("localhost", 1113))
		EsConnection(ActorSystem("uno-eventstore"), settings)
	}
}