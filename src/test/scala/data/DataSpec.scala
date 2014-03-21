package edu.luc.etl.ccacw.sensor.data

import org.specs2.mutable.Specification

class DataSpec extends Specification {

  "A simulated MODBUS device" should {

    "produce a simulated reading" in {
      val d = Devices.mk42i(name = "42i", id = "00:11:22:33:44:01", hostname = "localhost", port = 9501)
      d.measurements("no")("current")() should be lessThan(100)
    }
  }
}
