package org.w3.banana.jena.io

import org.apache.jena.sparql.resultset.OutputFormatter
import org.apache.jena.riot.resultset.ResultSetLang
import org.apache.jena.riot.Lang

import org.w3.banana.io._

/**
 * typeclass for serialising special
 */
trait JenaAnswerOutput[T] {
  def format: Lang
}

object JenaAnswerOutput {

  implicit val Json: JenaAnswerOutput[SparqlAnswerJson] =
    new JenaAnswerOutput[SparqlAnswerJson] {
      def format = ResultSetLang.RS_JSON
    }

  implicit val XML: JenaAnswerOutput[SparqlAnswerXml] =
    new JenaAnswerOutput[SparqlAnswerXml] {
      def format = ResultSetLang.RS_XML
    }

}

