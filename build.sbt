scalaVersion := "2.13.0"

libraryDependencies ++= Seq(
    "com.lihaoyi" %% "fastparse" % "2.1.3",
    "org.specs2" %% "specs2-core" % "4.6.0" % "test",
)

unmanagedJars in Compile += {
    baseDirectory.value / "unmanaged" / s"scalaz3_${scalaBinaryVersion.value}-4.7.1.jar"
}

// z3solver blows up when mkSolver is called multiple times within the same process :(
fork in Test := true
