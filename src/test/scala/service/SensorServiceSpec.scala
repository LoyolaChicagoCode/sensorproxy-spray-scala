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

    "list all of a device's settings" in {
      Get("/devices/00:11:22:33:44:01/settings") ~> myRoute ~> check {
        responseAs[String] must beEqualTo("""["unit"]""")
      }
    }

    "read a specific device setting" in {
      Get("/devices/00:11:22:33:44:01/settings/unit") ~> myRoute ~> check {
        responseAs[String] must beEqualTo("ppb")
      }
    }

    "list all of a device's measurements" in {
      Get("/devices/00:11:22:33:44:01/measurements") ~> myRoute ~> check {
        responseAs[String] must contain("no2")
      }
    }

    "look at a specific measurement provided by a device" in {
      Get("/devices/00:11:22:33:44:01/measurements/no2") ~> myRoute ~> check {
        responseAs[String] must beEqualTo("""["readings"]""")
      }
    }

    "list all readings for a specific measurement provided by a device" in {
      Get("/devices/00:11:22:33:44:01/measurements/no2/readings") ~> myRoute ~> check {
        responseAs[String] must contain("current")
      }
    }

    "obtain a specific reading for a measurement provided by a device" in {
      Get("/devices/00:11:22:33:44:01/measurements/no2/readings/current") ~> myRoute ~> check {
        responseAs[String].toFloat must be beBetween(0, 100)
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
