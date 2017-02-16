package application

import anorm._
import application.Utils._

object PatentNumbersPatterns extends App {
  println("START")
  implicit val conn = getDbConnection(dbUrl = args(0))

//  val query = "select patentNumberFull from david.pagePatents"
  val query = "select concat(PUBLN_AUTH, '_', PUBLN_NR, '_', PUBLN_KIND) from TLS211_PAT_PUBLN"
  val numbers = SQL(query).as(SqlParser.str(1).*)

  def getPattern(patent: String): String =
    patent.replaceAll("\\d", "#")

  printList("patterns", numbers.map(getPattern).countFreq)

}

/*
+++ patterns from patstat
5153109   US_#######_A
4038307   US_##########_Ad
2886457   US_#######_Bd
898968    US_######_A
299486    US_D######_Sd
89802     US_#####_A
71751     US_D######_S
23555     US_RE#####_E
13542     US_PP#####_Pd
13406     US_#######_Ad
8983      US_####_A
8419      US_RE#####_Ed
6321      US_####_P
...



+++ patterns from commoncral
1780085   d,ddd,ddd
205141    d.ddd.ddd
39248     USddddddd
2751      d,ddd.ddd
2136      d.ddd,ddd
1164      USd,ddd,ddd
672       d'ddd'ddd
457       d'ddd,ddd
231       d'ddd.ddd
20        USd.ddd.ddd
12        d.ddd'ddd
7         USdddd,ddd
5         USd,ddd.ddd
3         USd.ddd,ddd
2         d,ddd'ddd
1         USd,dddddd
*/
