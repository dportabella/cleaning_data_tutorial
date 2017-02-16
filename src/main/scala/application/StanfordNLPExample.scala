package application

import edu.stanford.nlp.ling.CoreAnnotations.{NamedEntityTagAnnotation, PartOfSpeechAnnotation, TextAnnotation, TokensAnnotation}
import edu.stanford.nlp.ling._
import edu.stanford.nlp.pipeline._
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation
import edu.stanford.nlp.trees._
import edu.stanford.nlp.util._

import scala.collection.JavaConversions._

// adapted from http://stanfordnlp.github.io/CoreNLP/api.html

object StanfordNLPExample {
  def main(args: Array[String]) {
    val text = "The settlement of Iceland (Icelandic: Landnámsöld) is generally believed to have begun in the second half of the 9th century, when Norse settlers migrated across the North Atlantic."
//    val url = "https://en.wikipedia.org/wiki/Settlement_of_Iceland"
//    val text = Jsoup.parse(new java.net.URL(url), 0).text()

    // we don't need all this annotators for this example... :P
    val pipeline = new StanfordCoreNLP(PropertiesUtils.asProperties(
      "annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, natlog",
      "ssplit.isOneSentence", "true",
      "tokenize.language", "en"))

    val document = new Annotation(text)

    pipeline.annotate(document)

    for (sentence: CoreMap <- document.get(classOf[CoreAnnotations.SentencesAnnotation]))
      showSentence(sentence)
  }

  def showSentence(sentence: CoreMap) {
    println("+++ sentence: " + sentence)
    println("+++ tokens: " + sentence.get(classOf[TokensAnnotation]))

    for (token <- sentence.get(classOf[TokensAnnotation]))
      showToken(token)

    showSentenceTree(sentence)

    showSentenceSyntaticDependencies(sentence)
  }

  // given a token, it shows:
  // - text
  // - Part Of Speech tag (noun, verb, determinant...)
  // - named entity tag: CITY, COUNTRY, DATE, LOCATION, NATIONALITY, NUMBER, ORGANIZATION, PERSON, TITLE, DURATION...
  //   see https://github.com/stanfordnlp/CoreNLP/blob/3e83622752070ce3117391ba9b6f26a05a60d520/src/edu/stanford/nlp/ie/KBPRelationExtractor.java
  def showToken(token: CoreLabel) {
    val word = token.get(classOf[TextAnnotation])
    val pos = token.get(classOf[PartOfSpeechAnnotation])
    val ne = token.get(classOf[NamedEntityTagAnnotation])
    println("token: " + word + "/" + pos + "/" + ne)
  }

  def showSentenceTree(sentence: CoreMap) {
    println("+++ sentenceTree")
    val sentenceTree: Tree = sentence.get(classOf[TreeAnnotation])
    println(sentenceTree)
    sentenceTree.pennPrint()
  }

  def showSentenceSyntaticDependencies(sentence: CoreMap) {
    println("+++ syntantic dependencies of a sentence")
    val dependencies = sentence.get(classOf[SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation])
    println(dependencies)
  }
}

/*
+++ sentence: The settlement of Iceland (Icelandic: Landnámsöld) is generally believed to have begun in the second half of the 9th century, when Norse settlers migrated across the North Atlantic.


+++ tokens: [The-1, settlement-2, of-3, Iceland-4, -LRB--5, Icelandic-6, :-7, Landnámsöld-8, -RRB--9, is-10, generally-11, believed-12, to-13, have-14, begun-15, in-16, the-17, second-18, half-19, of-20, the-21, 9th-22, century-23, ,-24, when-25, Norse-26, settlers-27, migrated-28, across-29, the-30, North-31, Atlantic-32, .-33]
token: The/DT/O
token: settlement/NN/O
token: of/IN/O
token: Iceland/NNP/LOCATION
token: -LRB-/-LRB-/O
token: Icelandic/NNP/MISC
token: :/:/O
token: Landnámsöld/NNP/O
token: -RRB-/-RRB-/O
token: is/VBZ/O
token: generally/RB/O
token: believed/VBN/O
token: to/TO/O
token: have/VB/O
token: begun/VBN/O
token: in/IN/O
token: the/DT/DATE
token: second/JJ/DATE
token: half/NN/DATE
token: of/IN/DATE
token: the/DT/DATE
token: 9th/JJ/DATE
token: century/NN/DATE
token: ,/,/O
token: when/WRB/O
token: Norse/NNP/MISC
token: settlers/NNS/O
token: migrated/VBD/O
token: across/IN/O
token: the/DT/O
token: North/NNP/LOCATION
token: Atlantic/NNP/LOCATION
token: ././O


+++ sentenceTree
(ROOT (S (NP (NP (NP (DT The) (NN settlement)) (PP (IN of) (NP (NNP Iceland)))) (PRN (-LRB- -LRB-) (NP (NP (NNP Icelandic)) (: :) (NP (NNP Landnámsöld))) (-RRB- -RRB-))) (VP (VBZ is) (ADVP (RB generally)) (VP (VBN believed) (S (VP (TO to) (VP (VB have) (VP (VBN begun) (PP (IN in) (NP (NP (DT the) (JJ second) (NN half)) (PP (IN of) (NP (NP (DT the) (JJ 9th) (NN century)) (, ,) (SBAR (WHADVP (WRB when)) (S (NP (NNP Norse) (NNS settlers)) (VP (VBD migrated) (PP (IN across) (NP (DT the) (NNP North) (NNP Atlantic)))))))))))))))) (. .)))
(ROOT
  (S
    (NP
      (NP
        (NP (DT The) (NN settlement))
        (PP (IN of)
          (NP (NNP Iceland))))
      (PRN (-LRB- -LRB-)
        (NP
          (NP (NNP Icelandic))
          (: :)
          (NP (NNP Landnámsöld)))
        (-RRB- -RRB-)))
    (VP (VBZ is)
      (ADVP (RB generally))
      (VP (VBN believed)
        (S
          (VP (TO to)
            (VP (VB have)
              (VP (VBN begun)
                (PP (IN in)
                  (NP
                    (NP (DT the) (JJ second) (NN half))
                    (PP (IN of)
                      (NP
                        (NP (DT the) (JJ 9th) (NN century))
                        (, ,)
                        (SBAR
                          (WHADVP (WRB when))
                          (S
                            (NP (NNP Norse) (NNS settlers))
                            (VP (VBD migrated)
                              (PP (IN across)
                                (NP (DT the) (NNP North) (NNP Atlantic))))))))))))))))
    (. .)))

+++ syntantic dependencies of a sentence
-> believed/VBN (root)
  -> settlement/NN (nsubjpass)
    -> The/DT (det)
    -> Iceland/NNP (nmod:of)
      -> of/IN (case)
    -> Icelandic/NNP (dep)
      -> -LRB-/-LRB- (punct)
      -> :/: (punct)
      -> Landnámsöld/NNP (dep)
      -> -RRB-/-RRB- (punct)
  -> is/VBZ (auxpass)
  -> generally/RB (advmod)
  -> begun/VBN (xcomp)
    -> to/TO (mark)
    -> have/VB (aux)
    -> half/NN (nmod:in)
      -> in/IN (case)
      -> the/DT (det)
      -> second/JJ (amod)
      -> century/NN (nmod:of)
        -> of/IN (case)
        -> the/DT (det)
        -> 9th/JJ (amod)
        -> ,/, (punct)
        -> migrated/VBD (acl:relcl)
          -> when/WRB (advmod)
          -> settlers/NNS (nsubj)
            -> Norse/NNP (compound)
          -> Atlantic/NNP (nmod:across)
            -> across/IN (case)
            -> the/DT (det)
            -> North/NNP (compound)
  -> ./. (punct)
*/