package object ops {
	/**
	 * Thrush combinator
	 * Thrush reverses the order of evaluation
	 *
	 * http://debasishg.blogspot.fr/2009/09/thrush-combinator-in-scala.html
	 */
	implicit class ThrushOps[A](self: A) { def |>[B](f: A => B): B = f(self) }
}