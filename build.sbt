lazy val scala211 = "2.11.12"
lazy val scala212 = "2.12.17"
lazy val supportedScalaVersions = List(scala211, scala212)

name                := "fs-common-utils"
version             := "0.1.4"
scalaVersion        := scala212
crossScalaVersions  := supportedScalaVersions

coverageExcludedPackages := "org.fs.utility.internal.*"

libraryDependencies ++= Seq(
  // Test
  "junit"             %  "junit"      % "4.12"     % "test",
  "org.scalactic"     %% "scalactic"  % "3.2.15"   % "test",
  "org.scalatest"     %% "scalatest"  % "3.2.15"   % "test",
  "org.scalatestplus" %% "junit-4-13" % "3.2.15.0" % "test"
)
