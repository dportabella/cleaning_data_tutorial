package application

import application.Utils._
import org.jsoup.Jsoup

object TextCleanExample extends App {

  val url = "https://en.wikipedia.org/wiki/Settlement_of_Iceland"
//  val url = "https://fr.wikipedia.org/wiki/Colonisation_de_l%27Islande"
//  val text = Jsoup.parse(new java.net.URL(url), 0).text().take(300)

//  val text = "Page d'aide sur l'homonymie Cet article concerne le peuplement de l'Islande durant l'âge des Vikings. Pour la conquête progressive de l'Islande par le roi de Norvège au xiiie\u00A0siècle, voir Âge des Sturlungar."
//  val text = "The settlement of Iceland (Icelandic: Landnámsöld) is generally believed to have begun in the second half of the 9th century, when Norse settlers migrated across the North Atlantic."
//  val text = "Additionally, Iceland is only about 450\u00A0kilometres from the Faroes which had been visited by Irish monks in the 6th century, and settled by the Norse around 650."

  val text =
    Jsoup.parse(new java.net.URL(url), 0).text()
    .stemText                                                        // StanfordNLP library: stem words analyzing the whole sentence (instead of word by word stemmer)
    .stripAccents                                                    // Apache Commons library
    .replaceAll("[\\h\\s\\v]+", " ")                                 // convert all types of spaces (tab, new line (\r, \n), non-breaking space... to a normal space)
    .replaceAll("[^\\p{IsAlphabetic}]", " ").replaceAll(" +", " ")   // take only the letters (remove digit and punctuations marks)
    .toLowerCase

  println(text)

  // count frequency of tokens
  val tokenFreq: List[FreqCount[String]] =
    text.split(" ").toList.countFreq

  printList("freq of tokens", tokenFreq)


  // count frequency of chars
  val charFreq: List[FreqCount[Char]] =
    text.toList.countFreq.sortBy(_.value)

  printList("freq of chars", charFreq.map(c => c.value + "\t" + escapeUnicode(c.value) + "\t" + c.count))



  // the wiki page contains the text "450 kilometres" with a non-breaking space.
  // if we do not clean the text (one of the two replaceAll above), we get this as one token (instead of two)
  val cases = text.split(" ").toList.filter(_.contains("kilometres"))
  println("cases: " + cases)

  val nonBreakingSpace = "\u00A0"
  val pos = text.indexOf(nonBreakingSpace)
  println(text.context(pos))
  println(text.replaceAll(nonBreakingSpace, "_").context(pos))
}


/*
Unicode: https://en.wikipedia.org/wiki/List_of_Unicode_characters


# freq of tokens before cleaning
128  the
95   of
60   in
58   to
45   and
34   a
24   Iceland
23   was
22   is
22   that
...
6    Iceland.
5    Ingólfur
4    Karlsson,
4    (2016).
4    which
...
1    450 kilometres    << Non-breaking space (\u00A0) https://en.wikipedia.org/wiki/Non-breaking_space
1    "There
1    Stock



# freq of tokens after cleaning
- all special chars (eg non-breaking space), accents and punctuation marks removed
- word stemming examples:
  - 'was', 'is' and 'be' is merged into 'be'
  - 'monks' and 'monk' merged to 'monk'

149  the
96   of
68   be
61   in
59   to
45   and
44   a
44   iceland
...
8    ingolfur
6    karlsson
5    there
4    which
...
1    kilometre
1    stock



+++ freq of chars before cleaning
 	\u0020	2379
"	\u0022	30
&	\u0026	5
'	\u0027	6
(	\u0028	23
)	\u0029	23
,	\u002C	100
-	\u002D	37
.	\u002E	166
/	\u002F	29
0	\u0030	88
1	\u0031	114
2	\u0032	47
3	\u0033	37
4	\u0034	47
5	\u0035	25
6	\u0036	28
7	\u0037	32
8	\u0038	45
9	\u0039	61
:	\u003A	26
;	\u003B	4
=	\u003D	5
?	\u003F	2
A	\u0041	59
B	\u0042	18
C	\u0043	43
D	\u0044	18
E	\u0045	20
F	\u0046	35
G	\u0047	27
H	\u0048	48
I	\u0049	95
J	\u004A	8
K	\u004B	14
L	\u004C	29
M	\u004D	23
N	\u004E	45
O	\u004F	12
P	\u0050	26
R	\u0052	32
S	\u0053	45
T	\u0054	48
U	\u0055	5
V	\u0056	21
W	\u0057	32
Z	\u005A	2
[	\u005B	30
]	\u005D	30
^	\u005E	13
_	\u005F	2
a	\u0061	997
b	\u0062	145
c	\u0063	335
d	\u0064	513
e	\u0065	1425
f	\u0066	255
g	\u0067	221
h	\u0068	474
i	\u0069	876
j	\u006A	25
k	\u006B	133
l	\u006C	582
m	\u006D	280
n	\u006E	944
o	\u006F	777
p	\u0070	156
q	\u0071	4
r	\u0072	760
s	\u0073	750
t	\u0074	1046
u	\u0075	268
v	\u0076	132
w	\u0077	161
x	\u0078	26
y	\u0079	161
z	\u007A	7
 	\u00A0	18      <<< non-breaking space  https://en.wikipedia.org/wiki/Non-breaking_space   http://www.fileformat.info/info/unicode/char/00a0/index.htm
®	\u00AE	1
Á	\u00C1	3
Í	\u00CD	13
Ó	\u00D3	1
Ö	\u00D6	2
Ú	\u00DA	1
Þ	\u00DE	1
à	\u00E0	1
á	\u00E1	31
å	\u00E5	1
æ	\u00E6	1
ç	\u00E7	1
é	\u00E9	2
í	\u00ED	14
ð	\u00F0	14
ñ	\u00F1	1
ó	\u00F3	45
ö	\u00F6	9
ú	\u00FA	8
þ	\u00FE	4
–	\u2013	23
—	\u2014	4
’	\u2019	1
“	\u201C	1
”	\u201D	1


+++ freq of chars after cleaning
 	\u0020	2358
a	\u0061	1075
b	\u0062	215
c	\u0063	379
d	\u0064	437
e	\u0065	1447
f	\u0066	289
g	\u0067	223
h	\u0068	519
i	\u0069	931
j	\u006A	33
k	\u006B	149
l	\u006C	612
m	\u006D	302
n	\u006E	940
o	\u006F	836
p	\u0070	182
q	\u0071	4
r	\u0072	784
s	\u0073	586
t	\u0074	1080
u	\u0075	275
v	\u0076	162
w	\u0077	160
x	\u0078	26
y	\u0079	179
z	\u007A	9
æ	\u00E6	1
ð	\u00F0	14
þ	\u00FE	5

still, to remove the last 3 chars, we can use Unicode Collation Algorithm:
http://www.saxonica.com/documentation9.6/#!extensibility/config-extend/collation/UCA
*/