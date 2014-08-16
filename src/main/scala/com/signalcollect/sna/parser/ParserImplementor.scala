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

object ParserImplementor {
  val graphFile = "/power.gml"
  //  val daGraph = getGraph
  def getGraph(fileName: String, className: SNAClassNames /*vertexLabels: MutableList[String]*/ ): com.signalcollect.Graph[Any, Any] = {
//    println(className)
    val parser = new GmlParser
    val parsedGraphs: List[Graph] = parser.parse(Source.fromFile(getResourcePath(fileName)).mkString) //Kann auch ein File-Objekt sein
//    println(parsedGraphs)
//    println(fileName.getClass())
    val graph = GraphBuilder.build
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

  def getResourcePath(resourcePath: String): String = {
    this.getClass().getResource(resourcePath).getPath()
  }
  //TODO: create enum or constant for matching
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