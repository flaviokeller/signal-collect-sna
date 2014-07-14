package com.signalcollect.sna

import com.signalcollect.DataGraphVertex
import com.signalcollect.DefaultEdge
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.Graph
import com.signalcollect.GraphBuilder
import com.signalcollect.Vertex
import com.signalcollect.configuration.ExecutionMode
import scala.collection.mutable.ArrayBuffer

object Degree extends App {
  //  getAverage
  //  getAll
  run

  final private var aId = 'a'
  final private var a = new AverageDegreeVertex(aId)
  final var d = 0.0
  final def init() {
    aId = 'b'
    a = new AverageDegreeVertex(aId)
  }
  def run(): ExecutionResult = {
    val graph = GraphBuilder.build

    baseGraph(graph)
    extendGraph(graph)
    average(graph, aId)
    val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
    val stats = graph.execute(execmode)
    graph.awaitIdle
    graph.foreachVertex(println(_))
    var s = ArrayBuffer[String]()
    graph.foreachVertex(v => s += v.state.toString)
    val il = filterInteger(s)
    val res = new ExecutionResult(a.state, il)
    graph.shutdown
    res
    //    println(stats)
  }

  def filterInteger(l: ArrayBuffer[String]): java.util.List[java.lang.Integer] = {
    var res = new java.util.ArrayList[java.lang.Integer]
    val states = l.filter(s => !s.contains("."))
    for (state <- states) {
      res.add(Integer.valueOf(state))
    }
    res
  }

  def baseGraph(graph: Graph[Any, Any]) {
    graph.addVertex(a)
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
  }
  def extendGraph(graph: Graph[Any, Any]) {
    graph.addVertex(new DegreeVertex(6))
    graph.addVertex(new DegreeVertex(7))
    graph.addVertex(new DegreeVertex(8))
    graph.addVertex(new DegreeVertex(9))
    graph.addVertex(new DegreeVertex(10))
    graph.addEdge(5, new DegreeEdge(6))
    graph.addEdge(5, new DegreeEdge(7))
    graph.addEdge(5, new DegreeEdge(8))
    graph.addEdge(5, new DegreeEdge(9))
    graph.addEdge(6, new DegreeEdge(7))
    graph.addEdge(6, new DegreeEdge(10))
    graph.addEdge(7, new DegreeEdge(10))
    graph.addEdge(8, new DegreeEdge(7))
    graph.addEdge(9, new DegreeEdge(6))
    graph.addEdge(10, new DegreeEdge(8))
  }
  def average(g: Graph[Any, Any], id: Any) = {
    g.foreachVertex((v: Vertex[Any, _]) => g.addEdge(v.id, new DegreeEdge(id)))
    g.foreachVertex((v: Vertex[Any, _]) => g.addEdge(id, new AverageDegreeEdge(v.id)))
  }

}

class DegreeVertex(id: Any) extends DataGraphVertex(id, 0) {

  type Signal = Any
  type State = Int

  lazy val edgeSet = outgoingEdges.values.toSet
  def collect: State = {
    val degreeEdges = edgeSet.filter(edge => edge.targetId.isInstanceOf[Integer])
    val degreeSignals = mostRecentSignalMap.values.toList.filter(signal => signal.getClass.toString().contains("Integer"))
    degreeEdges.size + degreeSignals.size
  }

}
class DegreeEdge(t: Any) extends DefaultEdge(t) {
  type Source = DataGraphVertex[Any, Any]
  def signal = source.state
}

class AverageDegreeVertex(id: Char) extends DataGraphVertex(id, 0.0) {

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

class AverageDegreeEdge(t: Any) extends DefaultEdge(t) {
  type Source = AverageDegreeVertex
  def signal = source.id
}
