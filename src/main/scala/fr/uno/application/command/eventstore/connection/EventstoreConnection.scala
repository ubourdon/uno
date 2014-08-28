package fr.uno.application.command.eventstore.connection

import eventstore.EsConnection

trait EventstoreConnection {
	def connection: EsConnection
}