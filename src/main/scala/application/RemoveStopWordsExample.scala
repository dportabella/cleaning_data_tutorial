package application

import anorm._
import application.Utils._

object RemoveStopWordsExample extends App {
  implicit val conn = getDbConnection(dbUrl = args(0))
  val names: List[String] =
    SQL("select person_name from TLS906_PERSON limit 100000").as(SqlParser.str(1).*)

  printList("first ten names", names.take(10))


  // show char frequency
  val charFreq: List[FreqCount[Char]] =
    names.flatMap(_.toList).countFreq.sortBy(_.value)
  printList("freq of chars", charFreq.map(c => c.value + "\t" + escapeUnicode(c.value) + "\t" + c.count))


  // show token frequency
  val tokenFreq: List[FreqCount[String]] =
    names.flatMap(_.split(" ")).countFreq
  printList("top tokens", tokenFreq.take(100))


  // stop words: limited, co., corporatiopn,gmbh.
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




+++ freq of chars
 	\u0020	184426
!	\u0021	3
#	\u0023	1
&	\u0026	1460
'	\u0027	387
(	\u0028	403
...
0	\u0030	548
1	\u0031	606
2	\u0032	371
3	\u0033	283
...
A	\u0041	54007
B	\u0042	14581
C	\u0043	28474
D	\u0044	19962
...
 	\u00A0	58
¡	\u00A1	109
¢	\u00A2	2
¥	\u00A5	17
±	\u00B1	28
‰	\u2030	63
...


this shows that the database has been imported with an incorrect encoding. Example EPFL names from EPFLPatentsProject.scala:
- Ecole Polytechnique Federale de Lausanne (EPFL)
- Ã‰cole Polytechnique FÃ©dÃ©rale de Lausanne (EPFL)
- Ecole Polytechnique FÃ©dÃ©rale de Lausanne (EPFL)
- â€¢COLE POLYTECHNIQUE FUDURALE DE LAUSANNE
*/


