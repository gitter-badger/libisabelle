package edu.tum.cs.isabelle.refined

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

import eu.timepit.refined.api._
import eu.timepit.refined.string._

import shapeless.Witness

import edu.tum.cs.isabelle._
import edu.tum.cs.isabelle.hol._
import edu.tum.cs.isabelle.pure._

case class Satisfying[S](s: S)

object Satisfying {

  val Check = Operation.implicitly[Term, Boolean]("check")

  implicit def satisfyingValidate[T : Embeddable, S <: String](implicit ws: Witness.Aux[S], isa: Isabelle): Validate.Aux[T, Satisfying[S], String] =
    new Validate[T, Satisfying[S]] {
      import isa._

      type R = String
      def showExpr(t: T) = t.toString

      def validate(t: T) = Await.result(
        Expr.ofString[T => Boolean](thy, ws.value) flatMap {
          case None =>
            Future.successful(Failed(s"Parse error: ${ws.value} could not be parsed"))
          case Some(pred) =>
            for {
              term <- Expr.embed(thy, t)
              res <- system.invoke(Check)((term |> pred).term)
            }
            yield {
              if (res.unsafeGet)
                Passed("Proved proposition")
              else
                Failed("Could not prove proposition")
            }
        },
        timeout
      )
    }

}
