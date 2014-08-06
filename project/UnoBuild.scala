import Dependencies._
import sbt.Keys._
import sbt._

object UnoBuild extends Build {

	lazy val root = Project(id = "uno", base = file("."), settings = Project.defaultSettings ++ Seq(
		name := "uno",
		version := "0.1-SNAPSHOT",
		scalaVersion := "2.11.1",

		libraryDependencies ++= scalaLibs ++ javaLibs ++ tests
	))
}

object Dependencies {
	val scalaLibs = Seq(
		"org.scalaz"                %% "scalaz-core"            % "7.0.6"
	)

	val javaLibs = Seq(
		"joda-time"                 % "joda-time"               % "2.3",
		"org.joda"                  % "joda-convert"            % "1.2"
	)

	val tests = Seq(
		"org.scalatest"             %% "scalatest"              % "2.2.0"       % "test"
	)
}
