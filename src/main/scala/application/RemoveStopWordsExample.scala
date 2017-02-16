package application

import anorm._
import application.Utils._

object RemoveStopWordsExample extends App {
  implicit val conn = getDbConnection(dbUrl = args(0))
  val names = SQL"select person_name from TLS906_PERSON limit 100000".as(SqlParser.str(1).*)

  val charFreq = names.flatMap(_.toList).countFreq.sortBy(_.value)

  charFreq.foreach(c => println(c.value + "\t" + escapeUnicode(c.value) + "\t" + c.count))

  val nameFreq = names.flatMap(_.split(" ")).countFreq
  printList("top tokens", nameFreq.take(100))

  printList("some names containing 'Co.'", names.filter(_.contains("imited")).take(10000))

  val stopWords = "(?i)\\b(Limited|Co\\.|Corporation|Ltd\\.|GmbH)\\b"

  def stopWordFound(text: String): Boolean =
    stopWords.r.findAllIn(text).nonEmpty

  val someNames = names.filter(stopWordFound).take(100)
  printList("some companies", someNames)

  def removeStopWords(text: String): String =
    text.replaceAll(stopWords, "")

  println("+++ some companies, stop words removed")
  someNames.foreach(name =>
    println(s"'$name'  --> '${removeStopWords(name)}'")
  )
}



