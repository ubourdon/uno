package fr.uno.domain.model

sealed trait Direction {
	def reverse: Direction
	def on(player: Player): Player
}

case object Clockwise extends Direction {
	override def reverse = CounterClockwise
	override def on(player: Player) = player +1
}

case object CounterClockwise extends Direction {
	override def reverse = Clockwise
	override def on(player: Player) = player -1
}