name := "primesui-finagle"
version := "1.0"
organization := "fr.janalyse"
scalaVersion := "2.11.7"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

fork := true
javaOptions in run ++= Seq(
 "-Xms2g",
 "-Xmx2g",
 "-Xmn1000m",
// "-XX:GCTimeRatio=50",
// "-XX:SurvivorRatio=4",
 "-XX:+UseConcMarkSweepGC",
 "-XX:+UseParNewGC",
 "-XX:+CMSParallelRemarkEnabled",
 "-XX:+ScavengeBeforeFullGC",
 "-XX:+CMSScavengeBeforeRemark",
 "-XX:+ExplicitGCInvokesConcurrent",
 "-XX:+CMSClassUnloadingEnabled",
 "-XX:+UseCMSInitiatingOccupancyOnly",
 "-XX:CMSInitiatingOccupancyFraction=80",
 "-XX:ParallelGCThreads=4",
 "-XX:+AggressiveOpts",
 "-XX:+OptimizeStringConcat",
 "-XX:+UseFastAccessorMethods",
 "-Dcom.sun.management.jmxremote.port=2555",
 "-Dcom.sun.management.jmxremote.authenticate=false",
 "-Dcom.sun.management.jmxremote.ssl=false",
 "-Djava.net.preferIPv4Stack=true",
 "-Djava.net.preferIPv6Addresses=false",
 "-Dhazelcast.jmx=true"
)


libraryDependencies ++= Seq(
  "fr.janalyse"         %% "primes"                               % "1.2.2-SNAPSHOT",
  "fr.janalyse"         %% "unittools"                            % "0.2.7-SNAPSHOT",
  "fr.janalyse"         %% "janalyse-jmx"                         % "0.7.1",
  "org.squeryl"         %% "squeryl"                              % "0.9.5-7",
  "com.mchange"          % "c3p0"                                 % "0.9.2.1",
  "net.sf.ehcache"       % "ehcache-core"                         % "2.6.11",
  "javax.transaction"    % "jta"                                  % "1.1", // required for ehcache
  "com.hazelcast"         % "hazelcast"                           % "3.6.1", // objects cache alternative
  "mysql"                % "mysql-connector-java"                 % "5.1.36",
  "ch.qos.logback"       % "logback-classic"                      % "1.1.3",
  "org.codehaus.janino"  % "janino"                               % "2.7.8" // Allow logback config file conditionals
)

lazy val versions = new {
  val finatra = "2.1.4"
  val guice = "4.0"
}

libraryDependencies ++= Seq(
  "com.twitter.finatra" %% "finatra-http" % versions.finatra,
  "com.twitter.finatra" %% "finatra-httpclient" % versions.finatra,
  "com.twitter.finatra" %% "finatra-slf4j" % versions.finatra,
  "com.twitter.inject" %% "inject-core" % versions.finatra,

  "com.twitter.finatra" %% "finatra-http" % versions.finatra % "test",
  "com.twitter.finatra" %% "finatra-jackson" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-server" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-app" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-core" % versions.finatra % "test",
  "com.twitter.inject" %% "inject-modules" % versions.finatra % "test",
  "com.google.inject.extensions" % "guice-testlib" % versions.guice % "test",

  "com.twitter.finatra" %% "finatra-http" % versions.finatra % "test" classifier "tests",
  "com.twitter.finatra" %% "finatra-jackson" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-app" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-core" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-modules" % versions.finatra % "test" classifier "tests",
  "com.twitter.inject" %% "inject-server" % versions.finatra % "test" classifier "tests",

  "org.mockito" % "mockito-core" % "1.9.5" % "test",
  "org.scalatest" %% "scalatest" % "2.2.3" % "test",
  "org.specs2" %% "specs2" % "2.3.12" % "test")


resolvers += "JAnalyse Repository" at "http://www.janalyse.fr/repository/"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Twitter Maven" at "https://maven.twttr.com"
)

assemblyMergeStrategy in assembly := {
  case "BUILD" => MergeStrategy.discard
  case other => MergeStrategy.defaultMergeStrategy(other)
}

