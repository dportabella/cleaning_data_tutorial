package application

import anorm._
import application.Utils._

object Test extends App {
  println("START")
  implicit val conn = getDbConnection(dbUrl = args(0))
  val names = SQL"select person_name from TLS906_PERSON limit 1000000".as(SqlParser.str(1).*)
//  implicit val conn = getDbConnection(dbUrl = "jdbc:mysql://127.0.0.1/test?user=root&password=&useSSL=false")
//  val names = SQL"select content from GenericAttributes where attribute='Background'".as(SqlParser.str(1).*)

//  val nameFreq = names.flatMap(_.split(" ")).countFreq
//  nameFreq.take(1000).foreach(println)


  //  val table = new LookupTranslator()
  val charFreq = names.flatMap(_.toList).countFreq.sortBy(_.value)
//  charFreq.take(1000).foreach(c => println(c.value + "\t" + StringEscapeUtils.escapeJava(c.value) + "\t" + c.count))
  charFreq.take(1000).foreach(c => println(c.value + "\t" + escapeUnicode(c.value) + "\t" + c.count))

  println("END")
}
