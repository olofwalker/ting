val dottyVersion = "3.0.0-RC3"

enablePlugins(GraalVMNativeImagePlugin)

graalVMNativeImageOptions ++= Seq(
  "-H:ReflectionConfigurationFiles=" + baseDirectory.value / "graal" / "reflect-config.json",
  "-H:ResourceConfigurationFiles=" + baseDirectory.value / "graal" / "resource-config.json",
  "--initialize-at-build-time",
  "--initialize-at-run-time=" +
    "com.typesafe.config.impl.ConfigImpl$EnvVariablesHolder," +
    "com.typesafe.config.impl.ConfigImpl$SystemPropertiesHolder",
  "--no-fallback",
  "--static",
  "--allow-incomplete-classpath",
  "--report-unsupported-elements-at-runtime"
)

lazy val root = project
  .in(file("."))
  .settings(
    name := "ting",
    version := "0.1.0",

    libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
    libraryDependencies += "net.jcazevedo" % "moultingyaml_2.13" % "0.4.1",
    libraryDependencies += "com.lihaoyi" % "os-lib_2.13" % "0.3.0",
    // To make the default compiler and REPL use Dotty
    scalaVersion := dottyVersion,
  )
