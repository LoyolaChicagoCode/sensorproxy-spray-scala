package edu.luc.etl.ccacw.sensor.service

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import DefaultJsonProtocol._
import edu.luc.etl.ccacw.sensor.model.SimulatedModbusDevice
import edu.luc.etl.ccacw.sensor.data.Devices

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


object SensorServiceJsonProcotol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val simulatedModbusDeviceFormat = jsonFormat6(SimulatedModbusDevice.apply)
}

// this trait defines our service behavior independently from the service actor
trait SensorService extends HttpService {

  import SensorServiceJsonProcotol._

  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Say hello to <i>spray-routing</i> on <i>spray-can</i>!</h1>
              </body>
            </html>
          }
        }
      }
    } ~
    path("devices" / "1") {
      get {
        complete {
          Devices.mk42i(name = "42i", id = "00:11:22:33:44:01", hostname = "localhost", port = 9501)(SimulatedModbusDevice.apply)
        }
      }
    }
}
