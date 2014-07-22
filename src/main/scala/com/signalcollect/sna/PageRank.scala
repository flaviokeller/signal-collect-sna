package com.signalcollect.sna

import scala.collection.JavaConversions.bufferAsJavaList
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SynchronizedBuffer
import com.signalcollect.DataGraphVertex
import com.signalcollect.Edge
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.Graph
import com.signalcollect.GraphBuilder
import com.signalcollect.GraphEditor
import com.signalcollect.Vertex
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.DefaultEdge

object PageRank extends App{
  final def run(): ExecutionResult = {
    val e = new ExampleGraph
    val graph = GraphBuilder.build

    e.initPageRank
    e.basePageRankGraph(graph)
    e.extendPageRankGraph(graph)
    e.setAveragePageRankVertex(graph)
    val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
    val stats = graph.execute(execmode)
    graph.awaitIdle
    var s = new ArrayBuffer[Vertex[Any, _]] with SynchronizedBuffer[Vertex[Any, _]]
    graph.foreachVertex(v => s.add(v))
    val vertexMap = filterInteger(s)
    val prCompRes = new ComputationResults(e.getAveragePageRankVertex.state, vertexMap)
    val res = new ExecutionResult(prCompRes, null)
    graph.shutdown
    res
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

  type Signal = Pair[Any,Any]
  type State = Double
  /**
   * The collect function calculates the rank of this vertex based on the rank
   *  received from neighbors and the damping factor.
   */
  def collect: State = {
    val pageRankSignals = mostRecentSignalMap.filter(signal => !signal._2._1.getClass.toString().contains("Average")).values.toList
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
    if (added & e.weight < sumOfOutWeights & e.isInstanceOf[AveragePageRankEdge]) {
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
    Pair(source,source.state * weight / source.sumOfOutWeights)
  }
}

class AveragePageRankVertex(id: Char) extends DataGraphVertex(id, 0.0) {

  type Signal = Pair[Any,Any]
  type State = Double

  override def addEdge(e: Edge[_], graphEditor: GraphEditor[Any, Any]): Boolean = {
    var added = super.addEdge(e, graphEditor)
    if (added & e.weight < sumOfOutWeights & e.isInstanceOf[AveragePageRankEdge]) {
      sumOfOutWeights -= e.weight
      if (e.sourceId == e.targetId) {
        added = false
      }
    }
    added
  }

  def collect: State = {
    val pageRankSignals = mostRecentSignalMap.filter(signal => !signal._2._1.getClass.toString().contains("Average")).values.toList
    var sum = 0.0
    for (signal <- pageRankSignals) {
      sum += java.lang.Double.valueOf(signal._2.toString)
    }
    sum / pageRankSignals.size.toDouble
  }
}

class AveragePageRankEdge(t: Any) extends DefaultEdge(t) {
  type Source = DataGraphVertex[Any, Any]
  def signal = Pair(source,source.state)
}
