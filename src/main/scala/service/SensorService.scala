package edu.luc.etl.ccacw.sensor
package service

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import model.SimulatedModbusDevice
import data.Devices

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class SensorServiceActor extends Actor with SensorService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

trait SensorServiceJsonProcotol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val simulatedModbusDeviceFormat = jsonFormat6(SimulatedModbusDevice.apply)
}

// this trait defines our service behavior independently from the service actor
trait SensorService extends HttpService with SensorServiceJsonProcotol {

  val network = data.network
  // TODO push cast into mashaler
  lazy val devices = network.flatten flatMap { _.devices } map { _.asInstanceOf[SimulatedModbusDevice] }

  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>CCACW Sensor Proxy</h1>
                <p><a href="devices">devices</a></p>
                <p>More functionality coming soon...</p>
              </body>
            </html>
          }
        } ~
        respondWithMediaType(`application/json`) {
          complete { Seq("devices") }
        }
      }
    } ~
    pathPrefix("devices") {
      get {
        pathEndOrSingleSlash {
          complete { devices }
        } ~
        pathPrefix("(?:[0-9a-fA-F][0-9a-fA-F]:){5}[0-9a-fA-F][0-9a-fA-F]".r) { ident =>
          // TODO try to simplify this logic using monad transformer
          devices find { _.id == ident } map { device =>
            pathEndOrSingleSlash {
              complete { device }
            } ~
            pathPrefix("settings") {
              pathEndOrSingleSlash {
                complete { device.settings.keys }
              } ~
              path(Segment) { setting =>
                device.settings.get(setting) map { complete(_) } getOrElse reject
              }
            } ~
            pathPrefix("measurements") {
              pathEndOrSingleSlash {
                complete { device.measurements.keys }
              } ~
              pathPrefix(Segment) { measurement =>
                pathEndOrSingleSlash {
                  complete { Seq("readings") }
                } ~
                pathPrefix("readings") {
                  pathEndOrSingleSlash {
                    device.measurements.get(measurement) map {
                      readings => complete(readings.keys)
                    } getOrElse reject
                  } ~ path(Segment) { reading =>
                    val r = for {
                      m <- device.measurements.get(measurement)
                      r <- m.get(reading)
                    } yield r
                    r map { r => complete(r().toString) } getOrElse reject
                  }
                }
              }
            }
          } getOrElse reject
        }
      }
    }
}
