package edu.luc.etl.ccacw.sensor
package data

import model.ModbusDevice

object Devices {

  def mk42i[D](name: String, id: String, hostname: String, port: Int)(implicit ctor: ModbusDevice.Ctor[D]): D = ctor(
    name, id, hostname, port,
    Map(
      "unit" -> Pair("ppb", "ug/m3")
    ),
    Map(
      "no"  -> Map("current" -> 0, "min" -> 10, "max" -> 20),
      "no2" -> Map("current" -> 2, "min" -> 12, "max" -> 22),
      "nox" -> Map("current" -> 4, "min" -> 14, "max" -> 24)
    )
  )

  def mk49i[D](name: String, id: String, hostname: String, port: Int)(implicit ctor: ModbusDevice.Ctor[D]): D = ctor(
    name, id, hostname, port,
    Map(
      "unit" -> Pair("ppb", "ug/m3")
    ),
    Map(
      "o3"  -> Map("current" -> 0, "min" -> 10, "max" -> 20)
    )
  )
}
