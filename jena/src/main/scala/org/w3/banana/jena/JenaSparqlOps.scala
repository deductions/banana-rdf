package org.w3.banana.jena

import org.apache.jena.graph.{ Node => JenaNode }
import org.apache.jena.query.{ QueryFactory, Query => JenaQuery }
import org.apache.jena.rdf.model.RDFNode
import org.apache.jena.update.UpdateFactory
import org.apache.jena.query.QueryType

import org.w3.banana.SparqlOps.withPrefixes
import org.w3.banana._

import scala.collection.JavaConverters._
import scala.util._
import org.apache.jena.query.Syntax

class JenaSparqlOps(implicit jenaUtil: JenaUtil) extends SparqlOps[Jena] {

  def parseSelect(query: String, prefixes: Seq[Prefix[Jena]]): Try[Jena#SelectQuery] = Try {
    val parsedQuery = QueryFactory.create(withPrefixes(query, prefixes), Syntax.syntaxARQ )
    assert(parsedQuery.isSelectType)
    parsedQuery
  }

  def parseConstruct(query: String, prefixes: Seq[Prefix[Jena]]): Try[Jena#ConstructQuery] = Try {
    val parsedQuery = QueryFactory.create(withPrefixes(query, prefixes), Syntax.syntaxARQ)
    assert(parsedQuery.isConstructType)
    parsedQuery
  }

  def parseAsk(query: String, prefixes: Seq[Prefix[Jena]]): Try[Jena#AskQuery] = Try {
    val parsedQuery = QueryFactory.create(withPrefixes(query, prefixes))
    assert(parsedQuery.isAskType)
    parsedQuery
  }

  def parseUpdate(query: String, prefixes: Seq[Prefix[Jena]]): Try[Jena#UpdateQuery] = Try {
    UpdateFactory.create(withPrefixes(query, prefixes))
  }

  def parseQuery(query: String, prefixes: Seq[Prefix[Jena]]): Try[Jena#Query] = Try {
    QueryFactory.create(withPrefixes(query, prefixes))
  }

  def fold[T](
    query: Jena#Query)(
      select: Jena#SelectQuery => T,
      construct: Jena#ConstructQuery => T,
      ask: Jena#AskQuery => T) =
    // DESCRIBE, TODO?
    query.queryType match {
      case QueryType.SELECT => select(query)
      case QueryType.CONSTRUCT => construct(query)
      case QueryType.ASK => ask(query)
      case _ => throw new NotImplementedError("todo: match not exhaustive. It would fail on the following inputs: CONSTRUCT_JSON, CONSTRUCT_QUADS, DESCRIBE, UNKNOWN ")
    }

  def getNode(solution: Jena#Solution, v: String): Try[Jena#Node] = {
    val node: RDFNode = solution.get(v)
    if (node == null)
      Failure(VarNotFound(s"var $v not found in QuerySolution $solution"))
    else
      Success(jenaUtil.toNode(node))
  }

  def varnames(solution: Jena#Solution): Set[String] = solution.varNames.asScala.toSet

  def solutionIterator(solutions: Jena#Solutions): Iterator[Jena#Solution] =
    solutions.asScala

}
