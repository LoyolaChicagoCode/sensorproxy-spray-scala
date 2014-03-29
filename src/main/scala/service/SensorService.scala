package edu.luc.etl.ccacw.sensor
package service

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import spray.httpx.SprayJsonSupport
import spray.httpx.marshalling.ToResponseMarshaller
import spray.json._
import scalaz.syntax.id._
import model.SimulatedModbusDevice

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
  // TODO push cast into marshaler
  lazy val devices = network.flatten flatMap { _.devices } map { _.asInstanceOf[SimulatedModbusDevice] }

  // TODO move to model?
  // Extra layer between model and routes required to support flat "route-centric" routes.
  // The real KL suspects that JAX-RS would require something similar.
  def getDevice(ident: String) = devices find { _.id == ident.replaceAll("-", ":") }
  def getSettingKeys(ident: String) = getDevice(ident) map { _.settings.keys }
  def getSetting(ident: String, setting: String) = for {
    d <- getDevice(ident)
    s <- d.settings.get(setting)
  } yield s
  def getMeasurementKeys(ident: String) = getDevice(ident) map { _.measurements.keys }
  def getMeasurement(ident: String, measurement: String) = for {
    d <- getDevice(ident)
    m <- d.measurements.get(measurement)
    r = Seq("readings")
  } yield r
  def getReadingKeys(ident: String, measurement: String) = for {
    d <- getDevice(ident)
    m <- d.measurements.get(measurement)
  } yield m.keys
  def getReading(ident: String, measurement: String, reading: String) =  for {
    d <- getDevice(ident)
    m <- d.measurements.get(measurement)
    r <- m.get(reading)
  } yield r().toString

  def completeOrReject[A](result: => Option[A])(implicit marshaller: ToResponseMarshaller[A]): Route =
    result map { complete(_) } getOrElse reject
  val MacAddress = "(?:[0-9a-fA-F]{2}[:-]){5}[0-9a-fA-F]{2}".r
  val /? = Slash.?

  // TODO make sure all routes can produce JSON
  // TODO try to simplify this logic using monad transformer
  // route-centric routes
  val myRoute =
    (pathEndOrSingleSlash & get) {
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
    } ~
    (path("devices" ~ /?) & get) {
      complete { devices }
    } ~
    (path("devices" / MacAddress ~ /?) & get) { ident =>
      completeOrReject { getDevice(ident) }
    } ~
    (path("devices" / MacAddress / "settings" ~ /?) & get) { ident =>
      completeOrReject { getSettingKeys(ident) }
    } ~
    (path("devices" / MacAddress / "settings" / Segment ~ /?) & get) { (ident, setting) =>
      completeOrReject { getSetting(ident, setting) map { Seq(_) } }
    } ~
    (path("devices" / MacAddress / "measurements" ~ /?) & get) { ident =>
      completeOrReject { getMeasurementKeys(ident) }
    } ~
    (path("devices" / MacAddress / "measurements" / Segment ~ /?) & get) { (ident, measurement) =>
      completeOrReject { getMeasurement(ident, measurement) }
    } ~
    (path("devices" / MacAddress / "measurements" / Segment / "readings" ~ /?) & get) { (ident, measurement) =>
      completeOrReject { getReadingKeys(ident, measurement) }
    } ~
    (path("devices" / MacAddress / "measurements" / Segment / "readings" / Segment ~ /?) & get) { (ident, measurement, reading) =>
      completeOrReject { getReading(ident, measurement, reading) map { Seq(_) } }
    }

  // TODO compute all routes from domain model
  // domain-model-centric routes
  val myRoute2 =
    (pathEndOrSingleSlash & get) {
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
    } ~
    (pathPrefix("devices") & get) {
      pathEndOrSingleSlash {
        complete { devices }
      } ~
      pathPrefix(MacAddress) { ident =>
        getDevice(ident) map { device =>
          pathEndOrSingleSlash {
            complete { device }
          } ~
          pathPrefix("settings") {
            pathEndOrSingleSlash {
              complete { device.settings.keys }
            } ~
            path(Segment ~ /?) { setting =>
              completeOrReject { device.settings.get(setting) map { Seq(_) } }
            }
          } ~
          pathPrefix("measurements") {
            pathEndOrSingleSlash {
              complete { device.measurements.keys }
            } ~
            pathPrefix(Segment) { measurement =>
              device.measurements.get(measurement) map { measurement =>
                pathEndOrSingleSlash {
                  complete { Seq("readings") }
                } ~
                pathPrefix("readings") {
                  pathEndOrSingleSlash {
                    complete(measurement.keys)
                  } ~ path(Segment ~ /?) { reading =>
                    completeOrReject { measurement.get(reading) map { r => Seq(r().toString) } }
                  }
                }
              } getOrElse reject
            }
          }
        } getOrElse reject
      }
    }
}
