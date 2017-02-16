package application

import anorm._
import application.Utils._

object RemoveStopWordsExample extends App {
  implicit val conn = getDbConnection(dbUrl = args(0))
  val names: List[String] =
    SQL("select person_name from TLS906_PERSON limit 100000").as(SqlParser.str(1).*)

  printList("first ten names", names.take(10))


  // show token frequency
  val tokenFreq: List[FreqCount[String]] =
    names.flatMap(_.split(" ")).countFreq
  printList("top tokens", tokenFreq.take(100))


  // example stop words: limited, co., corporatiopn,gmbh.
  // regular expression ignores case: (?i) and take into account only entire words: \b
  val stopWords = "(?i)\\b(Limited|Co\\.|Corporation|Ltd\\.|GmbH)\\b"

  def stopWordFound(text: String): Boolean =
    stopWords.r.findAllIn(text).nonEmpty

  // show names which contain at least one stop word
  val someNames = names.filter(stopWordFound).take(100)
  printList("some companies", someNames)



  def removeStopWords(text: String): String =
    text.replaceAll(stopWords, "")

  // show those names with stop words removed
  println("+++ some companies, stop words removed")
  someNames.foreach(name =>
    println(s"'$name'  --> '${removeStopWords(name)}'")
  )
}


/*
+++ first ten names
Nokia Corporation
Lipponen, Markku
Laitinen, Timo
Aho, Ari
Knuutila, Jarno
NOKIA MOBILE PHONES LTD.
Medical Research Council
MEDIMMUNE LIMITED
Griffiths, Andrew David
Hoogenboom, Hendricus Renerus Jacobus Mattheus


+++ top tokens
4763	c/o
2893	Ltd.
2213	Inc.
2172	Co.,
1903	Corporation
1817	GmbH
1499	Dr.
1277	&
1194	J.
1154	A.
1097	Michael
1004	Limited
...



+++ some companies
Nokia Corporation
MEDIMMUNE LIMITED
CAMBRIDGE ANTIBODY TECHNOLOGY LIMITED
Cambridge Antibody Technology Limited
Philips Intellectual Property & Standards GmbH
Philips Corporate Intellectual Property GmbH
Marioff Corporation Oy
Saltigo GmbH
IVT - Industrie Vertrieb Technik GmbH & Co. KG
...



+++ some companies, stop words removed
'Nokia Corporation'  --> 'Nokia '
'MEDIMMUNE LIMITED'  --> 'MEDIMMUNE '
'CAMBRIDGE ANTIBODY TECHNOLOGY LIMITED'  --> 'CAMBRIDGE ANTIBODY TECHNOLOGY '
'Cambridge Antibody Technology Limited'  --> 'Cambridge Antibody Technology '
'Philips Intellectual Property & Standards GmbH'  --> 'Philips Intellectual Property & Standards '
'Philips Corporate Intellectual Property GmbH'  --> 'Philips Corporate Intellectual Property '
'Marioff Corporation Oy'  --> 'Marioff  Oy'
'Saltigo GmbH'  --> 'Saltigo '
'IVT - Industrie Vertrieb Technik GmbH & Co. KG'  --> 'IVT - Industrie Vertrieb Technik  & Co. KG'
...
*/


