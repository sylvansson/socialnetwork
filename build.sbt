name := "socialnetwork"

version := "0.3"

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "com.github.finagle" %% "finch-core" % "0.31.0",
  "com.github.finagle" %% "finch-circe" % "0.31.0",
  "com.pauldijou" %% "jwt-circe" % "4.2.0",
  "io.circe" %% "circe-generic" % "0.13.0",
  "io.getquill" %% "quill-jdbc" % "3.5.0",
  "org.postgresql" % "postgresql" % "42.2.10",
)
