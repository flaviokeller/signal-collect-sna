/*
 *  @author Philip Stutz
 *  
 *  Copyright 2010 University of Zurich
 *      
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *         http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 */

package com.signalcollect.sna.parser

import java.io.File
import java.io.FileReader
import scala.io.{ BufferedSource, Source }
import scala.util.parsing.combinator.PackratParsers
import scala.util.parsing.combinator.lexical.StdLexical
import scala.util.parsing.combinator.syntactical.StdTokenParsers
import scala.util.parsing.input.Reader
import scala.util.parsing.input.StreamReader

abstract class Graph(nodes: List[Node], edges: List[Edge])
case class DirectedGraph(nodes: List[Node], edges: List[Edge]) extends Graph(nodes, edges)
case class UndirectedGraph(nodes: List[Node], edges: List[Edge]) extends Graph(nodes, edges)
case class Node(id: Int, label: String, value: String)
case class Edge(source: Int, target: Int)

/*
 * @author Philip Stutz
 */
class GmlParser extends StdTokenParsers
  with PackratParsers {
  type Tokens = StdLexical
  val lexical = new StdLexical

  lexical.delimiters ++= List("=", "[", "]", "{", "}", ".", ",", "//", "(", ")", "\n", "\r")
  lexical.reserved ++= List("graph", "node", "edge", "id", "source", "target", "label", "value", "directed", "Creator")

  lazy val gmlFile: Parser[List[Graph]] = {
    opt("Creator" ~ stringLit) ~> rep(graph)
  }

  lazy val graph: Parser[Graph] = {
    "graph" ~ "[" ~> opt("directed" ~> bool) ~ rep(node) ~ rep(edge) <~ "]" ^^ {
      case optDirected ~ nodes ~ edges => {
        val directed = optDirected.getOrElse(false)
        if (directed)
          DirectedGraph(nodes, edges)
        else
          UndirectedGraph(nodes, edges)
      }
    }
  }
  lazy val bool: Parser[Boolean] = {
    numericLit ^^ {
      case "0" => false
      case "1" => true
    }
  }
  lazy val node: Parser[Node] = {
    "node" ~ "[" ~> id ~ opt(label) ~ opt(value) <~ "]" ^^ {
      case id ~ labelOpt ~ valueOpt => {
        val label = labelOpt.getOrElse(id.toString)
        val value = valueOpt.getOrElse("0")
        Node(id, label, value)
      }
    }
  }
  lazy val id: Parser[Int] = {
    "id" ~> numericLit ^^ { _.toInt }
  }
  lazy val label: Parser[String] = {
    "label" ~> (ident | stringLit)
  }
  lazy val value: Parser[String] = {
    "value" ~> (ident | stringLit | numericLit) //TODO find out how to parse decimals!
  }
  lazy val edgePart: Parser[Int] = {
    ("source" | "target") ~> numericLit ^^ { _.toInt }
  }

  lazy val edge: Parser[Edge] = {
    "edge" ~ "[" ~> opt(id) ~ edgePart ~ edgePart ~ opt(label) ~ opt(value) <~ "]" ^^ {
      case idOpt ~ source ~ target ~ labelOpt ~ valueOpt => {
        val id = idOpt.getOrElse(0)
        //        val label = labelOpt.getOrElse("0")
        //        val value = valueOpt.getOrElse("0")
        Edge(source, target)
      }
    }
  }

  def parse(s: String): List[Graph] = {
    parse(new lexical.Scanner(s))
  }

  def parse(f: File): List[Graph] = {
    val javaReader = new FileReader(f)
    //Source.fromFile(...).mkString
    val scalaReader: Reader[Char] = StreamReader(javaReader)
    parse(scalaReader)
  }

  def parse(bs: BufferedSource): List[Graph] = {
    val javaReader = bs.reader
    //Source.fromFile(...).mkString
    val scalaReader: Reader[Char] = StreamReader(javaReader)
    parse(scalaReader)
  }

  def parse(r: Reader[Char]): List[Graph] = {
    val scanner = new lexical.Scanner(r)
    parse(scanner)
  }

  def parse(tokens: lexical.Scanner): List[Graph] = {
    phrase(gmlFile)(tokens) match {
      case Success(g, next) => g
      case NoSuccess(msg, next) => throw new ParseException(msg + "\nNext position: (line: " + next.pos.line + ", column: " + next.pos.column + ")" + "\nNext token: " + next.rest.first)
    }
  }

}

class ParseException(msg: String) extends Exception(msg)