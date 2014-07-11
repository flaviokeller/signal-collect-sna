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

  type Signal = Any
  type State = Int

  lazy val edgeSet = outgoingEdges.values.toSet
  def collect: State = {
    val degreeEdges = edgeSet.filter(edge => edge.getClass().toString().contains("DegreeEdge"))
    val degreeSignals = mostRecentSignalMap.values.toList.filter(signal => signal.getClass.toString().contains("Integer"))

    degreeEdges.size + degreeSignals.size
  }

}
class DegreeEdge(t: Any) extends DefaultEdge(t) {
  type Source = DegreeVertex
  def signal = source.state
}

class AverageVertex(id: Char) extends DataGraphVertex(id, 0.0) {

  type Signal = Any
  type State = Double
  def collect: State = {
    val degreeSignals = mostRecentSignalMap.values.toList.filter(signal => signal.getClass().toString().contains("Integer"))
    var sum = 0
    for (signal <- degreeSignals) {
      sum += Integer.valueOf(signal.toString)
    }
    sum.toDouble / degreeSignals.size.toDouble
  }
}

class AverageEdge(t: Any) extends DefaultEdge(t) {
  type Source = DataGraphVertex[Any, Any]
  def signal = source.state
}