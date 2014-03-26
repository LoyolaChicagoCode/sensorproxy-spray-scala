organization  := "edu.luc.etl"

version       := "0.2.0"

scalaVersion  := "2.10.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/"
)

libraryDependencies ++= {
  val scalazV = "7.0.5"
  val akkaV = "2.3.0"
  val sprayV = "1.3.1"
  val sprayJsonV = "1.2.5"
  val specs2V = "2.3.7"
  Seq(
    "org.scalaz"          %%  "scalaz-core"   % scalazV,
    "io.spray"            %   "spray-can"     % sprayV,
    "io.spray"            %   "spray-routing" % sprayV,
    "io.spray"            %   "spray-httpx"   % sprayV,
    "io.spray"            %%  "spray-json"    % sprayJsonV,
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "io.spray"            %   "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % specs2V % "test"
  )
}

seq(Revolver.settings: _*)
