# Overview

A RESTful web service for exposing a collection of network-capable
environmental monitoring devices as a hierarchical resource.

Because CUERP's real MODBUS-compatible devices have been offline
indefinitely, this service will provide simulated readings.

Implemented in [Scala](http://scala-lang.org) using
[spray](http://spray.io) and [Scalaz](https://github.com/scalaz/scalaz).

# Build & Run

Make sure you have `sbt` version 0.12.3 or later.

To build and start the service:

    $ sbt stage
    $ foreman start

Now you can access the service like so:

    $ curl localhost:5000/devices/1

If the service fails to start because of a port conflict,
then please use [this workaround](http://laufer.tumblr.com/post/80793055563).

# Sample instance and resources

A service instance is available on Heroku and provides the following
sample resources:

  - <a href="http://luc-sensorproxy-spray.herokuapp.com">root</a>
  - [all devices](http://luc-sensorproxy-spray.herokuapp.com/devices)
  - [a specific device](http://luc-sensorproxy-spray.herokuapp.com/devices/00:11:22:33:44:01 )
  - [all settings of this device](http://luc-sensorproxy-spray.herokuapp.com/devices/00:11:22:33:44:01/settings)
  - [a specific setting value of this device](http://luc-sensorproxy-spray.herokuapp.com/devices/00:11:22:33:44:01/settings/unit)
  - [all measurements provided this device](http://luc-sensorproxy-spray.herokuapp.com/devices/00:11:22:33:44:01/measurements)
  - [all no2 readings provided by this device](http://luc-sensorproxy-spray.herokuapp.com/devices/00:11:22:33:44:01/measurements/no2/readings)
  - [current (simulated) no2 reading provided by this device](http://luc-sensorproxy-spray.herokuapp.com/devices/00:11:22:33:44:01/measurements/no2/readings/current)

# References

- [Earlier version implemented in Java using Restlet](http://webpages.cs.luc.edu/~laufer/cuerp)
- [Stasiuk, Läufer, and Thiruvathukal, *Network Technologies used to Aggregate Environmental Data: Research Poster*, GCASR 2013](http://ecommons.luc.edu/cs_facpubs/65/)
- [Kaylor, Läufer, and Thiruvathukal, *REST on Routers? Preliminary Lessons for Language Designers, Framework Architects, and App Developers*, ICSOFT 2011](http://ecommons.luc.edu/cs_facpubs/35/)
