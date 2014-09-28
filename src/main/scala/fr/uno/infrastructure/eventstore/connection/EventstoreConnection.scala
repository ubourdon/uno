package fr.uno.infrastructure.eventstore.connection

import eventstore.EsConnection

trait EventstoreConnection {
	def connection: EsConnection
}