package edu.tum.cs.isabelle.refined

import java.nio.file.Paths

import scala.concurrent.{Await, ExecutionContext}
import scala.concurrent.duration.Duration

import edu.tum.cs.isabelle._
import edu.tum.cs.isabelle.api._
import edu.tum.cs.isabelle.pure._
import edu.tum.cs.isabelle.setup._

object Isabelle {

  def default(version: Version = Version("2015"), timeout: Duration = Duration.Inf)(implicit ec: ExecutionContext): Isabelle = {
    val config = Configuration(Some(Paths.get(".")), "HOL-Protocol")

    val system = Await.result(
      for {
        setup <- Setup.defaultSetup(version)
        env <- setup.makeEnvironment
        sys <- System.create(env, config)
        _ <- sys.invoke(Operation.UseThys)(List("refined/src/main/isabelle/Refined"))
      } yield sys,
      timeout
    )

    new Isabelle(system, timeout, ec)
  }

}

class Isabelle private(
  val system: System,
  val timeout: Duration,
  ec: ExecutionContext
) {
  val thy = Theory(system, "Main")
  implicit val executionContext: ExecutionContext = ec
}
