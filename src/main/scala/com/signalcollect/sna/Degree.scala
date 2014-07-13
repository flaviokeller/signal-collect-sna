package com.signalcollect.sna

import com.signalcollect.DataGraphVertex
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.Graph
import com.signalcollect.GraphBuilder
import com.signalcollect.Vertex
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.DefaultEdge

object Degree extends App {
//  getAverage
//  getAll
	run
	
//	var aId = Char
//	
//	def init(id:Char){
//	  var a = (id)
//	}
	def run() {
    val graph = GraphBuilder.build
    baseGraph(graph)
    extendGraph(graph)
    average(graph, 'a')
//    graph.forVertexWithId(aId, (a:AverageDegreeVertex => a.state))
    val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
        val stats = graph.execute(execmode)
    graph.awaitIdle
        graph.foreachVertex(println(_))

    graph.shutdown
    //    println(stats)
  }
  def getAverage(a: AverageDegreeVertex): Double = {
    a.state
  }

  def baseGraph(graph: Graph[Any, Any]) {
    graph.addVertex(new AverageDegreeVertex('a'))
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