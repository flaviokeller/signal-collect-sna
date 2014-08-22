package com.signalcollect.sna.parser

import scala.io.Source
import com.signalcollect.DefaultEdge
import com.signalcollect.GraphBuilder
import com.signalcollect.Vertex
import com.signalcollect.sna.PathTestEdge
import com.signalcollect.sna.PathTestVertex
import com.signalcollect.sna.metrics.DegreeEdge
import com.signalcollect.sna.metrics.DegreeVertex
import com.signalcollect.sna.metrics.PageRankEdge
import com.signalcollect.sna.metrics.PageRankVertex
import com.signalcollect.sna.gephiconnectors.SNAClassNames
import java.io.BufferedReader
import scala.collection.mutable.ArrayBuffer
import java.net.URL
import java.io.InputStreamReader
import java.nio.file.Files

object ParserImplementor {

  def getGraph(fileName: String, className: SNAClassNames): com.signalcollect.Graph[Any, Any] = {
    val parser = new GmlParser
    val parsedGraphs: List[Graph] = parser.parse(Source.fromFile(fileName)) //Kann auch ein File-Objekt sein
    val graph = GraphBuilder.build
    println("built new graph for parser with class " + className.name())
    parsedGraphs foreach {
      case g: UndirectedGraph =>
        g.nodes.foreach({ n: Node =>
          graph.addVertex(createVertex(n.id, className))
        })
        g.edges.foreach({ e: Edge =>
          graph.addEdge(e.source, createEdge(e.target, className))
          g match {
            case ug: UndirectedGraph => //add edges (Gegenrichtung, falls undirected graph)
          }
        })
    }
    //    graph.shutdown
    graph
  }

  def createVertex(id: Int, vertexClass: SNAClassNames): Vertex[Any, _] = {
    vertexClass match {
      case SNAClassNames.DEGREE => new DegreeVertex(id)
      case SNAClassNames.PAGERANK => new PageRankVertex(id)
      case SNAClassNames.PATH => new PathTestVertex(id)
    }
  }
  def createEdge(targetId: Int, edgeClass: SNAClassNames): DefaultEdge[_] = {
    edgeClass match {
      case SNAClassNames.DEGREE => new DegreeEdge(targetId)
      case SNAClassNames.PAGERANK => new PageRankEdge(targetId)
      case SNAClassNames.PATH => new PathTestEdge(targetId)
    }
  }

}