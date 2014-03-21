package edu.luc.etl.ccacw.sensor
package model

import java.net.InetSocketAddress

// TODO factories/validation?

/** A resource with a name. */
trait Resource {
  def name: String
}

/** A resource with a unique ID. */
trait Identifiable {
  def id: String
}

/**
 * A physical location with zero or more devices.
 * These can be arranged in some suitable topology, e.g., a tree.
 */
case class Location(name: String, devices: Device*) extends Resource

/** A device with readable settings and measurements. */
trait Device extends Resource with Identifiable {
  def address: InetSocketAddress
  def settings: Map[String, String]
  def measurements: Map[String, Map[String, () => Float]]
}

/** A MODBUS device abstraction with settings and register-based measurements. */
trait ModbusDevice extends Device {
  def deviceSettings: Map[String, Map[Boolean, String]]
  def measurementRegisters: Map[String, Map[String, Int]]
}

/**
 * Companion object defining a suitable constructor type as an
 * implicit argument for the device factory.
 */
object ModbusDevice {
  type Ctor = (
    String, String, InetSocketAddress,
      Map[String, Map[Boolean, String]],
      Map[String, Map[String, Int]]
    ) => ModbusDevice
}

/** A MODBUS device mixin implementation with simulated readings. */
trait SimulatedModbusReadings extends ModbusDevice {
  def settings = deviceSettings mapValues { _(false) }
  def measurements = measurementRegisters mapValues {
  _.mapValues(_ => () => 100 * scala.math.random.toFloat)
  }
}

/** A concrete, easy-to-configure MODBUS simulated device implementation. */
case class SimulatedModbusDevice(
  name: String,
  id: String,
  address: InetSocketAddress,
  deviceSettings: Map[String, Map[Boolean, String]],
  measurementRegisters: Map[String, Map[String, Int]]
) extends ModbusDevice with SimulatedModbusReadings
