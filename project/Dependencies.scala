import sbt._

object Dependencies {
  val akkaVersion           = "2.8.0-M4"
  val circeVersion          = "0.14.3"
  val macWireVersion        = "2.5.8"
  val retryVersion          = "0.3.6"
  val scalaLoggingVersion   = "3.9.5"
  val awsVersion            = "2.20.2"
  val openSearchVersion     = "2.2.0"
  val elastic4sVersion      = "8.5.3"
  val typeSafeConfigVersion = "1.4.2"

  val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
  val config       = "com.typesafe"                % "config"        % typeSafeConfigVersion

  val circeCore    = "io.circe" %% "circe-core"    % circeVersion
  val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  val circeParser  = "io.circe" %% "circe-parser"  % circeVersion

  val awsOpenSearch    = "software.amazon.awssdk" % "opensearch"             % awsVersion
  val openSearch       = "org.opensearch.client"  % "opensearch-java"        % openSearchVersion
  val openSearchClient = "org.opensearch.client"  % "opensearch-rest-client" % openSearchVersion

  val dependencies: Seq[ModuleID] = Seq(
    Dependencies.config,
    Dependencies.scalaLogging,
    Dependencies.circeCore,
    Dependencies.circeGeneric,
    Dependencies.circeParser,
    Dependencies.awsOpenSearch,
    Dependencies.openSearch,
    Dependencies.openSearchClient
  )

}
