package edu.tum.cs.isabelle.japi

import scala.collection.JavaConverters._
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

import edu.tum.cs.isabelle._
import edu.tum.cs.isabelle.defaults._
import edu.tum.cs.isabelle.setup.{Configuration, Environment}

import isabelle._

object JSystem {

  def instance(env: Environment, config: Configuration, timeout: Duration): JSystem =
    new JSystem(Await.result(System.instance(env, config), timeout), timeout)

  def instance(env: Environment, config: Configuration): JSystem =
    instance(env, config, Duration.Inf)

}

class JSystem private(system: System, timeout: Duration) {

  def getSystem(): System = system
  def getTimeout(): Duration = timeout

  def withTimeout(newTimeout: Duration): JSystem = new JSystem(system, newTimeout)

  private def await[A](future: Future[A]) =
    Await.result(future, timeout)

  def dispose(): Unit =
    await(system.dispose)

  def invoke[I, O](operation: Operation[I, O], arg: I): O =
    Exn.release(await(system.invoke(operation)(arg)))

}
