package application

import anorm._
import application.Utils._

object ShowCharFreq extends App {
  implicit val conn = getDbConnection(dbUrl = args(0))
  val names = SQL"select person_name from TLS906_PERSON limit 1000000".as(SqlParser.str(1).*)

  val charFreq = names.flatMap(_.toList).countFreq.sortBy(_.value)

  charFreq.foreach(c => println(c.value + "\t" + escapeUnicode(c.value) + "\t" + c.count))
}



