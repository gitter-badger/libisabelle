lazy val standardSettings = Seq(
  organization := "cs.tum.edu.isabelle",
  scalaVersion := "2.11.6",
  crossScalaVersions := Seq("2.10.5", "2.11.6"),
  javacOptions += "-Xlint:unchecked"
)

lazy val warningSettings = Seq(
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-unchecked",
    "-Xlint",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Xfatal-warnings"
  )
)

lazy val noPublishSettings = Seq(
  publish := (),
  publishLocal := (),
  publishArtifact := false
)


lazy val root = project.in(file("."))
  .settings(standardSettings)
  .settings(noPublishSettings)
  .aggregate(pideCore, setup, libisabelle, examples)

lazy val pideCore = project.in(file("pide-core"))
  .settings(moduleName := "pide-core")
  .settings(standardSettings)
  .settings(
    libraryDependencies ++= {
      if (scalaVersion.value startsWith "2.10")
        Seq()
      else
        Seq(
          "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.2"
        )
    }
  )

lazy val setup = project.in(file("setup"))
  .settings(standardSettings)
  .settings(warningSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.apache.commons" % "commons-compress" % "1.9",
      "org.apache.commons" % "commons-lang3" % "3.3.2",
      "com.github.fge" % "java7-fs-more" % "0.2.0",
      "com.google.code.findbugs" % "jsr305" % "1.3.+" % "compile"
    )
  )

lazy val libisabelle = project
  .dependsOn(pideCore)
  .dependsOn(setup)
  .settings(standardSettings)
  .settings(warningSettings)
  .settings(
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2-core" % "3.6.3" % "test",
      "org.specs2" %% "specs2-scalacheck" % "3.6.3" % "test",
      "org.scalacheck" %% "scalacheck" % "1.12.2" % "test"
    )
  )

lazy val examples = project
  .dependsOn(libisabelle)
  .settings(standardSettings)
  .settings(warningSettings)

lazy val full = project
  .dependsOn(examples)
  .settings(moduleName := "libisabelle-full")
  .settings(standardSettings)
  .settings(
    assemblyJarName in assembly := s"${moduleName.value}.jar"
  )
