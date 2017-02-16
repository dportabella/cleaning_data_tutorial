package application

import anorm._
import application.Utils._

object PatentNumbersPatterns extends App {
  println("START")
  implicit val conn = getDbConnection(dbUrl = args(0))

  val query = "select patentNumberFull from david.pagePatents"                                      // patent numbers extracted from commoncrawl
//  val query = "select concat(PUBLN_AUTH, '_', PUBLN_NR, '_', PUBLN_KIND) from TLS211_PAT_PUBLN"   // patent numbers from patstat

  val numbers = SQL(query).as(SqlParser.str(1).*)

  def getPattern(patent: String): String =
    patent.replaceAll("[0-9]", "#")                                                                 // replace all digits by '#'

  printList("patterns", numbers.map(getPattern).countFreq)

}

/*
+++ patterns from patstat
5153109   US_#######_A
4038307   US_##########_A#
4038307   US_##########_A#
2886457   US_#######_B#
898968    US_######_A
299486    US_D######_S#
89802     US_#####_A
71751     US_D######_S
23555     US_RE#####_E
13542     US_PP#####_P#
13406     US_#######_A#
8983      US_####_A
8419      US_RE#####_E#
6321      US_####_P
...



+++ patterns from commoncral
1780085   #,###,###
205141    #.###.###
39248     US#######
2751      #,###.###
2136      #.###,###
1164      US#,###,###
672       #'###'###
457       #'###,###
231       #'###.###
20        US#.###.###
12        #.###'###
7         US####,###
5         US#,###.###
3         US#.###,###
2         #,###'###
1         US#,######
*/
