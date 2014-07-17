package com.signalcollect.sna

import com.signalcollect.DataGraphVertex
import com.signalcollect.DefaultEdge
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.Graph
import com.signalcollect.GraphBuilder
import com.signalcollect.Vertex
import com.signalcollect.configuration.ExecutionMode
import scala.collection.JavaConversions._
import com.signalcollect.SumOfOutWeights
import com.signalcollect.SumOfOutWeights
import sun.awt.MostRecentKeyValue
import com.signalcollect.interfaces.EdgeId
import com.signalcollect.GraphEditor
import com.signalcollect.Edge
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SynchronizedBuffer

object PageRank extends App {
  init
  run

  final private var aId = 'a'
  final private var a = new AveragePageRankVertex(aId)
  final def init() {
    aId = 'a'
    a = new AveragePageRankVertex(aId)
  }
  final def run(): ExecutionResult = {
    val graph = GraphBuilder.build
    //    baseGraph
    //    extendGraph

    graph.addVertex(a)
    graph.addVertex(new PageRankVertex(1))
    graph.addVertex(new PageRankVertex(2))
    graph.addVertex(new PageRankVertex(3))
    graph.addVertex(new PageRankVertex(4))

    graph.addEdge(1, new PageRankEdge(2))
    graph.addEdge(1, new PageRankEdge(3))
    graph.addEdge(2, new PageRankEdge(3))
    //    graph.addEdge(2, new PageRankEdge(4))
    graph.addEdge(3, new PageRankEdge(4))
    def baseGraph() {
      graph.addVertex(new AveragePageRankVertex('a'))
      graph.addVertex(new PageRankVertex(1))
      graph.addVertex(new PageRankVertex(2))
      graph.addVertex(new PageRankVertex(3))
      graph.addVertex(new PageRankVertex(4))
      graph.addVertex(new PageRankVertex(5))
      graph.addEdge(1, new PageRankEdge(4))
      graph.addEdge(1, new PageRankEdge(3))
      graph.addEdge(2, new PageRankEdge(1))
      graph.addEdge(2, new PageRankEdge(3))
      graph.addEdge(4, new PageRankEdge(1))
      graph.addEdge(4, new PageRankEdge(2))
      graph.addEdge(5, new PageRankEdge(2))
      graph.addEdge(5, new PageRankEdge(3))
    }
    def extendGraph() {
      graph.addVertex(new PageRankVertex(6))
      graph.addVertex(new PageRankVertex(7))
      graph.addVertex(new PageRankVertex(8))
      graph.addVertex(new PageRankVertex(9))
      graph.addVertex(new PageRankVertex(10))
      graph.addEdge(5, new PageRankEdge(6))
      graph.addEdge(5, new PageRankEdge(7))
      graph.addEdge(5, new PageRankEdge(8))
      graph.addEdge(5, new PageRankEdge(9))
      graph.addEdge(6, new PageRankEdge(7))
      graph.addEdge(6, new PageRankEdge(10))
      graph.addEdge(7, new PageRankEdge(10))
      graph.addEdge(8, new PageRankEdge(7))
      graph.addEdge(9, new PageRankEdge(6))
      graph.addEdge(10, new PageRankEdge(8))
    }
    average(graph, 'a')
    def average(g: Graph[Any, Any], id: Any) = {
      g.foreachVertex((v: Vertex[Any, _]) => graph.addEdge(v.id, new AveragePageRankEdge(id)))
      g.foreachVertex((v: Vertex[Any, _]) => graph.addEdge(id, new AveragePageRankEdge(v.id)))
    }
    val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
    val stats = graph.execute(execmode)
    graph.awaitIdle
    //    graph.foreachVertex(println(_))
    var s = new ArrayBuffer[Vertex[Any, _]] with SynchronizedBuffer[Vertex[Any, _]]
    graph.foreachVertex(v => s.add(v))
    val vertexMap = filterInteger(s)
    val res = new ExecutionResult(a.state, vertexMap)
    graph.shutdown
    res
    //  println(stats)
  }

  def filterInteger(l: ArrayBuffer[Vertex[Any, _]]): java.util.Map[String, Object] = {
    var vertices = new java.util.HashMap[String, Object]
    for (vertex <- l) {
      vertices.put(vertex.id.toString, vertex.state.toString)
    }
    vertices
  }
}

class PageRankVertex(id: Any, dampingFactor: Double = 0.85) extends DataGraphVertex(id, dampingFactor) {

  type Signal = Any
  type State = Double
  /**
   * The collect function calculates the rank of this vertex based on the rank
   *  received from neighbors and the damping factor.
   */
  def collect: State = {
    val pageRankSignals = mostRecentSignalMap.filter(signal => !signal._1.getClass.toString().contains("Character"))
    var sum = 0.0
    if (pageRankSignals.isEmpty) {
      state
    } else {
      for (signal <- pageRankSignals) {
        sum += java.lang.Double.valueOf(signal._2.toString)
      }
      1 - dampingFactor + dampingFactor * sum
    }
  }

  override def addEdge(e: Edge[_], graphEditor: GraphEditor[Any, Any]): Boolean = {
    val added = super.addEdge(e, graphEditor)
    if (added & e.weight < sumOfOutWeights & e.targetId.isInstanceOf[Char]) {
      sumOfOutWeights -= e.weight
    }
    added
  }
  override def scoreSignal: Double = {
    lastSignalState match {
      case None => 1
      case Some(oldState) => (state - oldState).abs
    }
  }

}
class PageRankEdge(t: Any) extends DefaultEdge(t) {
  type Source = PageRankVertex

  /**
   * The signal function calculates how much rank the source vertex
   *  transfers to the target vertex.
   */
  def signal = {
    source.state * weight / source.sumOfOutWeights
  }
}

class AveragePageRankVertex(id: Char) extends DataGraphVertex(id, 0.0) {

  type Signal = Any
  type State = Double

  override def addEdge(e: Edge[_], graphEditor: GraphEditor[Any, Any]): Boolean = {
    var added = super.addEdge(e, graphEditor)
    if (added & e.weight < sumOfOutWeights & e.targetId.isInstanceOf[Char]) {
      sumOfOutWeights -= e.weight
      if (e.sourceId == e.targetId) {
        added = false
      }
    }
    added
  }

  def collect: State = {
    val degreeSignals = mostRecentSignalMap.values.toList.filter(signal => !signal.getClass.toString().contains("Character")) //.filter(signal => signal
    var sum = 0.0
    for (signal <- degreeSignals) {
      sum += java.lang.Double.valueOf(signal.toString)
    }
    sum / degreeSignals.size.toDouble
  }
}

class AveragePageRankEdge(t: Any) extends DefaultEdge(t) {
  type Source = DataGraphVertex[Any, Any]
  def signal = source.state
}
