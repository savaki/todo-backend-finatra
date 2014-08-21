import AssemblyKeys._ // put this at the top of the file

assemblySettings

name := "todo-backend-finatra"

version := "1.0"

scalaVersion := "2.10.4"

resolvers += "Twitter Repository" at "http://maven.twttr.com/"

libraryDependencies ++= Seq(
  "com.twitter" %% "finatra" % "1.5.3",
  "org.specs2" %% "specs2" % "2.4.1" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test"
)

// alias for heroku
addCommandAlias("stage", "assembly")

