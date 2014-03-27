package edu.luc.etl.ccacw.sensor.service

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._

class SensorServiceSpec extends Specification with Specs2RouteTest with SensorService {
  def actorRefFactory = system
  
  "SensorService" should {

    "return a greeting for GET requests to the root path" in {
      Get() ~> myRoute ~> check {
        responseAs[String] must contain("Sensor Proxy")
      }
    }

    "return the list of all devices" in {
      Get("/devices") ~> myRoute ~> check {
        responseAs[String] must contain("00:11:22:33:44:01")
      }
    }

    "find a device by id" in {
      Get("/devices/00:11:22:33:44:01") ~> myRoute ~> check {
        responseAs[String] must contain("00:11:22:33:44:01")
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> sealRoute(myRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }
  }
}
