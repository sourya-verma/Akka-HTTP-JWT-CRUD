
val AkkaVersion = "2.6.8"
val AkkaHttpVersion = "10.2.4"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.7",
  "org.json4s" %% "json4s-native" % "3.6.11",
  "com.typesafe.slick" %% "slick" % "3.3.3",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.3.3",
  "com.h2database" % "h2" % "1.4.187" % "test",
  "mysql" % "mysql-connector-java" % "6.0.6",
  "org.scalatest" %% "scalatest" % "3.2.6" % Test,
  "com.jason-goodwin" %% "authentikat-jwt" % "0.4.5",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.8",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.2.4"
)