lazy val root = (project in file(".")).
  settings(
    name := "ProcessLif",
    version := "0.0.1",
    scalaVersion := "2.12.8",
    libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.0" % "test",
    libraryDependencies ++= Seq(
        "ome" % "formats-gpl" % "6.4.0",
        "net.imagej" % "ij" % "1.49m",
        "sc.fiji" % "Extended_Depth_Field" % "2.0.2-SNAPSHOT",
        "sc.fiji" % "imageware" % "2.0.0"
    ),
    resolvers += "OME" at "https://artifacts.openmicroscopy.org/artifactory/maven",
    resolvers += Resolver.mavenLocal,
    mainClass in assembly := Some("com.britt.lif.ProcessLif"),
    updateOptions := updateOptions.value.withLatestSnapshots(false),
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  )
