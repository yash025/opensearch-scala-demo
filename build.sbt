name := "opensearch-scala-demo"
maintainer := "yashwanth@42078@gmail.com"

version := "1.0.0"

scalaVersion := "2.13.10"

Compile / run / mainClass := Some("ExampleApp")
Compile / packageBin / mainClass := Some("ExampleApp")

libraryDependencies ++= Dependencies.dependencies
