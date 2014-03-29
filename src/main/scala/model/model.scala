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

/** A device abstraction with readable settings and measurements. */
trait Device extends Resource with Identifiable {
  def address: InetSocketAddress
  def settings: Settings
  def measurements: Measurements
}

/** A mixin for producing a network address from hostname and port. */
trait HasInetAddress {
  def hostname: String
  def port: Int
  def address = new InetSocketAddress(hostname, port)
}

/** A MODBUS device descriptor with settings and register-based measurements. */
trait ModbusDevice extends Device {
  def deviceSettings: DeviceSettings
  def measurementRegisters: DeviceRegisters
}

// TODO move to package object?
/** Companion object for MODBUS device trait. */
object ModbusDevice {
  /**
   * Constructor type as an implicit argument to the device factory methods.
   * Generic in the result type to make downcasting unnecessary.
   */
  type Ctor[+D] = (String, String, String, Int, DeviceSettings, DeviceRegisters) => D
}

/** A MODBUS device mixin implementation with simulated readings. */
trait SimulatedModbusReadings extends ModbusDevice {
  override def settings = deviceSettings mapValues { _._1 }
  override def measurements = measurementRegisters mapValues {
    _.mapValues(_ => () => 100 * scala.math.random.toFloat)
  }
}

/** A concrete, easy-to-configure MODBUS simulated device implementation. */
case class SimulatedModbusDevice(
  name: String,
  id: String,
  hostname: String,
  port: Int,
  deviceSettings: DeviceSettings,
  measurementRegisters: DeviceRegisters
) extends SimulatedModbusReadings with HasInetAddress
