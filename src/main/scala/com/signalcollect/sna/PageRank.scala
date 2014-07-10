package com.signalcollect.sna

import com.signalcollect.Graph
import com.signalcollect.GraphBuilder
import com.signalcollect.Vertex
import com.signalcollect.DataGraphVertex
import com.signalcollect.DefaultEdge

object PageRank extends App {
  val graph = GraphBuilder.build
  graph.addVertex(new PageRankVertex(1))
  graph.addVertex(new PageRankVertex(2))
  graph.addVertex(new PageRankVertex(3))
  graph.addEdge(1, new PageRankEdge(2))
  graph.addEdge(2, new PageRankEdge(1))
  graph.addEdge(2, new PageRankEdge(3))
  graph.addEdge(3, new PageRankEdge(2))

  runDegrees(graph)

  val stats = graph.execute
//  println(stats)
  graph.foreachVertex(println(_))

  graph.shutdown
  
  def runDegrees(graph: Graph[Any, Any]) = {
    println("--------------------\n- Single Degrees: -\n--------------------")
    graph.foreachVertex(scanEdges)
//    println("-----------------------------\n- Average Degree of graph: -\n-----------------------------")
  }
  def scanEdges(v: Vertex[Any, _]): Int = {
    println("ID: " + v.id + " Edges: " + v.edgeCount)
    v.edgeCount
  }
}

class PageRankVertex(id: Any, dampingFactor: Double = 0.85) extends DataGraphVertex(id, 1 - dampingFactor) {

  type Signal = Int

  def collect: Double = signals.sum

  override def scoreSignal: Double = {
    lastSignalState match {
      case None => 1
      case Some(oldState) => (state - oldState).abs
    }
  }
}
class PageRankEdge(t: Any) extends DefaultEdge(t) {
  type Source = PageRankVertex
  def signal = source.state * weight / source.sumOfOutWeights
}