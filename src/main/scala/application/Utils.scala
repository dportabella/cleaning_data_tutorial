package application

import java.io.StringWriter
import java.sql.Connection

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
}
