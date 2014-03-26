package edu.luc.etl.ccacw.sensor
package service

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import concurrent.duration._
import util.Properties

object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // create and start our service actor
  val service = system.actorOf(Props[SensorServiceActor], "sensorproxy-service")

  implicit val timeout = Timeout(5.seconds)
  val port = Properties.envOrElse("PORT", "8080").toInt
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = "0.0.0.0", port = port)
}
