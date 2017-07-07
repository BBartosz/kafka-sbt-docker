import sbt.Keys.libraryDependencies

name := "KafkaDockerExample"

version := "1.0"

scalaVersion := "2.11.8"

def dockerSettings(debugPort: Option[Int]= None) = Seq(
  assemblyMergeStrategy in assembly := {
    case r if r.startsWith("reference.conf") => MergeStrategy.concat
    case PathList("META-INF", m) if m.equalsIgnoreCase("MANIFEST.MF") => MergeStrategy.discard
    case x => MergeStrategy.first
  },

  dockerfile in docker := {
    // The assembly task generates a fat JAR file
    val artifact: File = assembly.value
    val artifactTargetPath = s"/app/${artifact.name}"

    new Dockerfile {
      from("java")
      add(artifact, artifactTargetPath)
      debugPort match {
        case Some(port) => entryPoint("java", "-jar","-Xdebug", s"-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=$port", artifactTargetPath)
        case None => entryPoint("java", "-jar","-Xdebug", artifactTargetPath)
      }
    }
  },
  imageNames in docker := Seq(
    // Sets the latest tag
    ImageName(s"${name.value}:latest"),

    // Sets a name with a tag that contains the project version
    ImageName(
      namespace = Some(organization.value),
      repository = name.value,
      tag = Some("v" + version.value)
    )
  )
)


lazy val exampleProducer = (project in file("producer"))
  .enablePlugins(sbtdocker.DockerPlugin)
  .settings(
    libraryDependencies += "org.apache.kafka" % "kafka_2.11" % "0.10.0.0",
    libraryDependencies += "io.monix" %% "monix-execution" % "2.3.0",
    dockerSettings()
  )

lazy val exampleConsumerNew = (project in file("consumerNew"))
  .enablePlugins(sbtdocker.DockerPlugin)
  .settings(
    libraryDependencies += "org.apache.kafka" % "kafka_2.11" % "0.10.0.0",
    dockerSettings()
  )

lazy val exampleConsumerOld = (project in file("consumerOld"))
  .enablePlugins(sbtdocker.DockerPlugin)
  .settings(
    libraryDependencies += "org.apache.kafka" % "kafka_2.11" % "0.9.0.0",
    dockerSettings(Some(5005))
  )

