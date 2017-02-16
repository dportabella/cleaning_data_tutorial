package application

import java.io.StringWriter
import java.sql.Connection
import java.util

import scala.collection.JavaConversions._
import edu.stanford.nlp.ling.CoreAnnotations.{OriginalTextAnnotation, ValueAnnotation}
import edu.stanford.nlp.ling.{CoreAnnotations, CoreLabel}
import edu.stanford.nlp.pipeline.{Annotation, StanfordCoreNLP}
import edu.stanford.nlp.trees.{LabeledScoredTreeNode, Tree, TreeCoreAnnotations, WordStemmer}
import edu.stanford.nlp.util.PropertiesUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.text.translate.UnicodeEscaper

object Utils {
  def getDbConnection(dbUrl: String): Connection = {
    Class.forName("com.mysql.jdbc.Driver").newInstance()
    java.sql.DriverManager.getConnection(dbUrl)
  }

  case class FreqCount[T](count: Int, value: T) {
    override def toString: String = count + "\t" + value
  }

  implicit class RichList[A](list: List[A]) {
    def countFreq: List[FreqCount[A]] =
      list.groupBy(identity).map { case (value, values) => FreqCount(values.length, value) }.toList.sortBy(-_.count)
  }

  def escapeUnicode(text: Char): String = {
    val escaper = new UnicodeEscaper()
    val out = new StringWriter()
    escaper.translate(text, out)
    out.toString
  }

  implicit class RichString(text: String) {
    def stemText: String = Utils.stemText(text)

    def stripAccents: String = StringUtils.stripAccents(text)

    def context(index: Int, leftContextLength: Int = 20, rightContextLength: Int = 20): String =
      text.substring((index - leftContextLength).max(0), (index + rightContextLength + 1).min(text.length - 1))
  }


  def stemText(text: String): String = {
    val pipeline = new StanfordCoreNLP(PropertiesUtils.asProperties(
      "annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, natlog",
      "ssplit.isOneSentence", "true",
      "tokenize.language", "en"))

    val document = new Annotation(text)
    pipeline.annotate(document)

    def labelText(label: CoreLabel): String = {
      val v = label.getString(classOf[ValueAnnotation])
      if (v.startsWith("-")) label.getString(classOf[OriginalTextAnnotation])
      else v
    }

    val sentences = document.get(classOf[CoreAnnotations.SentencesAnnotation])

    sentences.map { sentence =>
      val tree: Tree = sentence.get(classOf[TreeCoreAnnotations.TreeAnnotation])
      val s = new WordStemmer
      s.visitTree(tree)
      tree
        .getLeaves().asInstanceOf[util.ArrayList[LabeledScoredTreeNode]]
        .map(n => labelText(n.label().asInstanceOf[CoreLabel]))
        .mkString(start= "", sep=" ", end = ".")
    }
      .mkString(" ")
  }
}
