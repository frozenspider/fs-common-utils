name         := "fs-common-utils"
version      := "0.1"
scalaVersion := "2.11.8"
crossScalaVersions := Seq("2.11.8", "2.12.1")

EclipseKeys.withSource := true
EclipseKeys.createSrc  := EclipseCreateSrc.Default

libraryDependencies ++= Seq(
  // Test
  "junit"         %  "junit"     % "4.12"  % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)
