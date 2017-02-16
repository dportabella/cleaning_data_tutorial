package application

import java.util

import edu.stanford.nlp.dcoref._
import edu.stanford.nlp.ling.CoreAnnotations.OriginalTextAnnotation
import edu.stanford.nlp.ling._
import edu.stanford.nlp.pipeline._
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations
import edu.stanford.nlp.trees._
import edu.stanford.nlp.util._

import scala.collection.JavaConversions._

// adapted from http://stanfordnlp.github.io/CoreNLP/api.html

object StanfordNLPExample {
  def main(args: Array[String]) {
    val text = "The settlement of Iceland (Icelandic: Landnámsöld) is generally believed to have begun in the second half of the 9th century, when Norse settlers migrated across the North Atlantic."
//    val url = "https://en.wikipedia.org/wiki/Settlement_of_Iceland"
//    val text = Jsoup.parse(new java.net.URL(url), 0).text()

    val pipeline = new StanfordCoreNLP(PropertiesUtils.asProperties(
      "annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, natlog",
      "ssplit.isOneSentence", "true",
      "tokenize.language", "en"))

    val document = new Annotation(text)
    pipeline.annotate(document)

    for (sentence <- document.get(classOf[CoreAnnotations.SentencesAnnotation])) {
      println("setence: " + sentence)
      println("tokens: " + sentence.get(classOf[CoreAnnotations.TokensAnnotation]))

      for (token <- sentence.get(classOf[CoreAnnotations.TokensAnnotation])) {
        val word = token.get(classOf[CoreAnnotations.TextAnnotation])
        val pos = token.get(classOf[CoreAnnotations.PartOfSpeechAnnotation])
        val ne = token.get(classOf[CoreAnnotations.NamedEntityTagAnnotation])
        println("token: " + word + "/" + pos + "/" + ne)
      }

      val tree: Tree = sentence.get(classOf[TreeCoreAnnotations.TreeAnnotation])
      println("tree: " + tree)
      println("tree leaves: " + tree.getLeaves().mkString(" "))
      println("tree leaves: " + originalText(tree).mkString(" "))

      val s = new WordStemmer; s.visitTree(tree)
      println("stemmed tree: " + tree)
      println("stemmed setence: " + sentence)
      println("tree leaves: " + originalText(tree).mkString(" "))

      val dependencies = sentence.get(classOf[SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation])
      println("SemanticGraph: " + dependencies)
    }

    val graph = document.get(classOf[CorefCoreAnnotations.CorefChainAnnotation])
    println("CorefChain: " + graph)
  }

  def originalText(tree: Tree): List[String] = {
      tree
        .getLeaves().asInstanceOf[util.ArrayList[LabeledScoredTreeNode]]
        .map(_.label().asInstanceOf[CoreLabel].getString(classOf[OriginalTextAnnotation]))
        .toList
  }
}

