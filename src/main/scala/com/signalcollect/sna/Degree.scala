package com.signalcollect.sna

import com.signalcollect.Graph
import com.signalcollect.GraphBuilder
import com.signalcollect.Vertex
import com.signalcollect.DataGraphVertex
import com.signalcollect.DefaultEdge
import com.signalcollect.interfaces.ComplexAggregation
import com.signalcollect.AbstractVertex
import com.sun.corba.se.spi.protocol.InitialServerRequestDispatcher
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.Edge

object Degree extends App {
  run()
  def run() {
    val graph = GraphBuilder.build
    graph.addVertex(new AverageVertex('a'))
    graph.addVertex(new DegreeVertex(1))
    graph.addVertex(new DegreeVertex(2))
    graph.addVertex(new DegreeVertex(3))
    graph.addVertex(new DegreeVertex(4))
    graph.addVertex(new DegreeVertex(5))
    graph.addEdge(1, new DegreeEdge(4))
    graph.addEdge(1, new DegreeEdge(3))
    graph.addEdge(2, new DegreeEdge(1))
    graph.addEdge(2, new DegreeEdge(3))
    graph.addEdge(4, new DegreeEdge(1))
    graph.addEdge(4, new DegreeEdge(2))
    graph.addEdge(5, new DegreeEdge(2))
    graph.addEdge(5, new DegreeEdge(3))

    average(graph, 'a')
    def average(g: Graph[Any, Any], id: Any) = {
      g.foreachVertex((v: Vertex[Any, _]) => graph.addEdge(v.id, new AverageEdge(id)))
      g.foreachVertex((v: Vertex[Any, _]) => graph.addEdge(id, new AverageEdge(v.id)))
    }
    val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
    val stats = graph.execute(execmode)
    graph.awaitIdle
    graph.foreachVertex(println(_))

    graph.shutdown
    println(stats)
  }
}

class DegreeVertex(id: Any) extends DataGraphVertex(id, 0) {

  type Signal = Set[Int]
  type State = Int

  lazy val edgeSet = outgoingEdges.values.toSet
  lazy val signalSet = signals.toSet
  def collect: State = {
    var degreeEdges = collection.mutable.Set(edgeSet.toSeq: _*)
    for (edge <- degreeEdges) {
      degreeEdges = degreeEdges.filter(edge => edge.getClass().toString().contains("DegreeEdge"))
    }
    var degreeSignals = collection.mutable.Set(mostRecentSignalMap.values.toSeq: _*)
    //    println(degreeSignals)

    for (signal <- degreeSignals) {
      degreeSignals = degreeSignals.filter(signal => signal.toString().contains("DegreeEdge"))
    }

    degreeEdges.size + degreeSignals.size
  }

}
class DegreeEdge(t: Any) extends DefaultEdge(t) {
  type Source = DegreeVertex
  def signal = source.edgeSet
}

class AverageVertex(id: Char) extends DataGraphVertex(id, 0) {

  type Signal = Int
  type State = Int
  def collect: State = {
    //    println(signals)

//    var states = mostRecentSignalMap.values
//    var summ = 0.0
//    for (state <- states) {
//      //      summ += state
//    }

    //    var result = (summ/signals.size.toDouble).toDouble
    //    result
    0
  }
}

class AverageEdge(t: Any) extends DefaultEdge(t) {
  type Source = DegreeVertex
  def signal = Set(this)
}