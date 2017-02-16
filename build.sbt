name := "tutorial_cleaning_data"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-lang3" % "3.4",
  "mysql" % "mysql-connector-java" % "5.1.40",
  "com.lihaoyi" %% "pprint" % "0.4.3",
  "com.typesafe.play" %% "anorm" % "2.5.2",
  "com.typesafe.play" %% "play-json" % "2.5.2",
//  "org.netpreserve.commons" % "webarchive-commons" % "1.1.7",
  "org.apache.commons" % "commons-io" % "1.3.2",
//  "org.apache.commons" % "commons-lang" % "1.3.2",
  "org.jsoup" % "jsoup" % "1.10.2",
  "com.syncthemall" % "boilerpipe" % "1.2.2",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0",
//  "edu.stanford.nlp" % "stanford-parser" % "3.6.0",
//  "edu.stanford.nlp" % "stanford-kbp" % "1.0.0"
  "edu.stanford.nlp" % "stanford-corenlp" % "3.6.0" classifier "models"

)
