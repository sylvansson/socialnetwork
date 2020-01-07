name := "socialnetwork"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "42.2.8",
  "io.getquill" %% "quill-jdbc" % "3.5.0"
)
