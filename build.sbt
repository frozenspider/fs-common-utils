name         := "fs-common-utils"
version      := "0.1.3"
scalaVersion := "2.12.3"
crossScalaVersions := Seq("2.11.11", "2.12.3")

coverageExcludedPackages := "org.fs.utility.internal.*"

libraryDependencies ++= Seq(
  // Test
  "junit"         %  "junit"     % "4.12"  % "test",
  "org.scalactic" %% "scalactic" % "3.0.4" % "test",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test"
)
