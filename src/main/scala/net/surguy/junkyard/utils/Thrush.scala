package net.surguy.junkyard.utils

/**
 * The "Thrush" combinator from To Mock a Mockingbird - see
 * http://debasishg.blogspot.com/2009/09/thrush-combinator-in-scala.html
 *
 * @author Inigo Surguy
 * @created Apr 2, 2010 8:39:21 AM
 */

object Thrush {
  implicit def pipelineSyntax[A](a: =>A) = new { def |>[B](f: A => B) = f(a) }
}