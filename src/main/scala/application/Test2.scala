package application

import java.text.Normalizer

import application.Utils.{escapeUnicode, _}
import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup

import scala.util.matching.Regex
//import edu.stanford.nlp.process.Stemmer
import edu.stanford.nlp.trees.WordStemmer

object Test2 extends App {

//  val url = "https://fr.wikipedia.org/wiki/Colonisation_de_l%27Islande"

  val url = "https://en.wikipedia.org/wiki/Settlement_of_Iceland"

   val text1 = StringUtils.stripAccents(Jsoup.parse(new java.net.URL(url), 0).text())
//  val text = Jsoup.parse(new java.net.URL(url), 0).text()
     .replaceAll("[\\h\\s\\v]+", " ")
     .replaceAll("[^\\p{IsAlphabetic}]", " ").replaceAll(" +", " ").toLowerCase
  // val text = "Page d'aide sur l'homonymie Cet article concerne le peuplement de l'Islande durant l'âge des Vikings. Pour la conquête progressive de l'Islande par le roi de Norvège au xiiie\u00A0siècle, voir Âge des Sturlungar."
//  val text = Jsoup.parse(new java.net.URL(url), 0).text().take(300)

//  println(text)

  val text = Jsoup.parse(new java.net.URL(url), 0).text()


  val nameFreq = text.split(" ").toList.countFreq
  nameFreq.foreach(println)


  val c = text.split(" ").count(_ == "siècle")
  println("aa: " + c)


  def textContext(text: String, index: Int, leftContextLength: Int = 20, rightContextLength: Int = 20): String =
    text.substring((index - leftContextLength).max(0), (index + rightContextLength + 1).min(text.length - 1))

  println(textContext(text, text.indexOf('\u00A0')))
  println(textContext(text.replaceAll("\u00A0", "_"), text.indexOf('\u00A0')))

  val charFreq = text.toList.countFreq.sortBy(_.value)

  charFreq.foreach(c => println(c.value + "\t" + escapeUnicode(c.value) + "\t" + c.count))


  val nonWordCharacterR: Regex = "[^\\p{IsAlphabetic}0-9]".r
//      .replaceAll("[\\h\\s\\v]+", " ")

}
