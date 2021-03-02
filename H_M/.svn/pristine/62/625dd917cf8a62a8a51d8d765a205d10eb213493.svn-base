name := """HomingServer"""
organization := "com.srit"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
						.enablePlugins(PlayJava, SwaggerPlugin)

scalaVersion := "2.12.8"

//libraryDependencies ++= Dependencies.ALL

libraryDependencies += guice
libraryDependencies += javaCore
//libraryDependencies += javaJdbc
//libraryDependencies += ws
libraryDependencies += javaForms
libraryDependencies += javaWs
libraryDependencies += javaJpa

libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "5.1.25",
  "com.google.code.gson" % "gson" % "2.2.4",
  "commons-io" % "commons-io" % "2.4",
  "org.apache.commons" % "commons-lang3" % "3.10",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.google.guava" % "guava" % "18.0",
  "com.squareup.okhttp3" % "okhttp" % "4.0.0",
  "com.sun.xml.ws" % "jaxws-ri" % "2.3.2",
  "org.hibernate" % "hibernate-core" % "5.4.2.Final",
  "org.apache.commons" % "commons-collections4" % "4.4",
  "org.apache.poi" % "poi" % "3.17",
  "org.apache.poi" % "poi-ooxml" % "3.17",
  "org.apache.poi" % "poi-ooxml-schemas" % "3.17",
  "org.apache.xmlbeans" % "xmlbeans" % "2.6.0",
  "org.webjars" % "swagger-ui" % "3.35.0",
  //"com.google.cloud" % "google-cloud-translate" % "1.95.2"
)




//libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.25"
//libraryDependencies += "com.google.code.gson" % "gson" % "2.2.4"
//libraryDependencies += "commons-io" % "commons-io" % "2.4"
//libraryDependencies += "org.apache.commons" % "commons-lang3" % "3.0"
//libraryDependencies += "net.tanesha.recaptcha4j" % "recaptcha4j" % "0.0.7"

//libraryDependencies += "javax.mail" % "mail" % "1.4.1"
//libraryDependencies += "org.mindrot" % "jbcrypt" % "0.3m"
// https://mvnrepository.com/artifact/com.google.guava/guava
//libraryDependencies += "com.google.guava" % "guava" % "18.0"

// https://mvnrepository.com/artifact/org.neo4j.driver/neo4j-java-driver
//libraryDependencies += "org.neo4j.driver" % "neo4j-java-driver" % "1.6.1"

// https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
//libraryDependencies += "com.squareup.okhttp3" % "okhttp" % "4.0.0"

//https://search.maven.org/search?q=g:com.sun.xml.ws
//libraryDependencies += "com.sun.xml.ws" % "jaxws-ri" % "2.3.2"

// https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
// libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.28.0"

//libraryDependencies += "com.h2database" % "h2" % "1.4.199"

//libraryDependencies += "org.hibernate" % "hibernate-core" % "5.4.2.Final"

// https://mvnrepository.com/artifact/org.apache.commons/commons-collections4
//libraryDependencies += "org.apache.commons" % "commons-collections4" % "4.4"
// https://mvnrepository.com/artifact/org.apache.poi/poi
//libraryDependencies += "org.apache.poi" % "poi" % "3.17"
// https://mvnrepository.com/artifact/org.apache.poi/poi-ooxml
//libraryDependencies += "org.apache.poi" % "poi-ooxml" % "3.17"
// https://mvnrepository.com/artifact/org.apache.poi/poi-ooxml-schemas
//libraryDependencies += "org.apache.poi" % "poi-ooxml-schemas" % "3.17"
// https://mvnrepository.com/artifact/org.apache.xmlbeans/xmlbeans
//libraryDependencies += "org.apache.xmlbeans" % "xmlbeans" % "2.6.0"

//libraryDependencies += "org.webjars" % "swagger-ui" % "3.35.0"

//libraryDependencies += "com.google.cloud" % "google-cloud-translate" % "1.95.2"


resolvers += Resolver.bintrayIvyRepo("sohoffice", "sbt-plugins")

PlayKeys.externalizeResourcesExcludes += baseDirectory.value / "conf" / "META-INF" / "persistence.xml"


EclipseKeys.preTasks := Seq(compile in Compile, compile in Test)
EclipseKeys.projectFlavor := EclipseProjectFlavor.Java           // Java project. Don't expect Scala IDE
EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources)  // Use .class files instead of generated .scala files for views and routes

//To import custom objects in Twirl Templates
TwirlKeys.templateImports += "models.ldap._"
// Packages listed here will be included in swagger definition section.
//swaggerDomainNameSpaces := Seq("io")

updateOptions := updateOptions.value.withCachedResolution(false)

swaggerDomainNameSpaces := Seq("models")
swaggerPrettyJson := true
