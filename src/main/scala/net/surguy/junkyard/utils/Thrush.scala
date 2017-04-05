package net.surguy.junkyard.utils

import scala.language.implicitConversions

/**
 * The "Thrush" combinator from To Mock a Mockingbird - see
 * http://debasishg.blogspot.com/2009/09/thrush-combinator-in-scala.html
 */
object Thrush {
  implicit def pipelineSyntax[A](a: =>A) = new { def |>[B](f: A => B) = f(a) }
}