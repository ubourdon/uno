package utils

import scala.concurrent.{Await, Awaitable}
import concurrent.duration._

object AsyncUtils {
	def sync[T](future: Awaitable[T], duration: Duration = 1 second): T = Await.result(future, duration)
}