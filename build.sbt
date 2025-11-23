ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.17"

ThisBuild / autoScalaLibrary := false

lazy val root = (project in file("."))
  .settings(
    name := "RestCall"
  )


val sparkVersion = "3.4.0"

val sparkDependencies = Seq(

  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
)

val testDependencies = Seq(

  "org.scalatest" %% "scalatest" % "3.0.8" % Test
)

libraryDependencies ++=sparkDependencies ++  testDependencies
