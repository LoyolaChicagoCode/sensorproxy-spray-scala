package edu.luc.etl.ccacw.sensor

import scalaz.Tree
import scalaz.syntax.tree._
import model._

package object data {

  implicit val ctor = SimulatedModbusDevice

  lazy val networkNavigable = network.loc.cojoin.toTree

  val network: Tree[Location] =
    Location("root").node(
      Location("luc").node(
        Location("lsc").node(
          Location("cuneo",
            Devices.mk42i(name = "42i", id = "00:11:22:33:44:01", hostname = "localhost", port = 9501),
            Devices.mk49i(name = "49i", id = "00:11:22:33:44:02", hostname = "localhost", port = 9502)
          ).leaf,
          Location("damen",
            Devices.mk49i(name = "49i", id = "00:11:22:33:44:03", hostname = "localhost", port = 9503)
          ).leaf
        ),
        Location("wtc").node(
          Location("baumhart").node(
            Location("rooftop",
              Devices.mk42i(name = "42i", id = "00:11:22:33:44:04", hostname = "localhost", port = 9504),
              Devices.mk49i(name = "49i", id = "00:11:22:33:44:05", hostname = "localhost", port = 9505)
            ).leaf,
            Location("basement",
              Devices.mk49i(name = "49i", id = "00:11:22:33:44:06", hostname = "localhost", port = 9506)
            ).leaf
          )
        ),
        Location("malibu",
          Devices.mk49i(name = "49i", id = "00:11:22:33:44:07", hostname = "localhost", port = 9507)
        ).leaf
      )
    )
}