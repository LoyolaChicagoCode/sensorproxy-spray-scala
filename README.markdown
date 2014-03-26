# Overview

A RESTful web service for exposing a collection of network-capable
environmental monitoring devices as a hierarchical resource.

Because CUERP's real MODBUS-compatible devices are offline indefinitely,
this service provides simulated readings.

Implemented in Scala using spray and Scalaz.

# Build & Run

Make sure you have `sbt` version 0.12.3 or later.

    $ sbt stage
    $ foreman start

    $ curl localhost:5000/devices/1

If the service fails to start because of a port conflict,
then please use [this workaround](http://laufer.tumblr.com/post/80793055563).

# Sample instance

A [sample instance](http://luc-sensorproxy-spray.herokuapp.com) is available on Heroku.

# References

- [Earlier version implemented in Java using Restlet](http://webpages.cs.luc.edu/~laufer/cuerp)
- [Stasiuk, Läufer, and Thiruvathukal, *Network Technologies used to Aggregate Environmental Data: Research Poster*, GCASR 2013](http://ecommons.luc.edu/cs_facpubs/65/)
- [Kaylor, Läufer, and Thiruvathukal, *REST on Routers? Preliminary Lessons for Language Designers, Framework Architects, and App Developers*, ICSOFT 2011](http://ecommons.luc.edu/cs_facpubs/35/)
