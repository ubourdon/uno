package utils

import scala.concurrent.{Await, Awaitable}
import concurrent.duration._

object AsyncUtils {
	def sync[T](duration: Duration = 1 second)(future: Awaitable[T]): T = Await.result(future, duration)
}