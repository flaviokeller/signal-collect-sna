package com.signalcollect.sna.metrics

import com.signalcollect.DataGraphVertex
import com.signalcollect.DefaultEdge
import com.signalcollect.Graph
import com.signalcollect.Vertex
import com.signalcollect.sna.ExampleGraph
import com.signalcollect.GraphBuilder
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.configuration.ExecutionMode
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SynchronizedBuffer

object LocalClusterCoefficient extends App {

  val graph = GraphBuilder.build
  val eg = new ExampleGraph
  eg.baseLocalClusterCoefficientGraph(graph)
  eg.extendLocalClusterCoefficientGraph(graph)

  val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
  val stats = graph.execute(execmode)
  graph.awaitIdle
  var s = new ArrayBuffer[Vertex[Any, _]] with SynchronizedBuffer[Vertex[Any, _]]
  graph.foreachVertex(v => s += v)
  graph.shutdown

  for (d <- s) {
//    println(d)
//    println(d.asInstanceOf[DataGraphVertex[Any, _]].outgoingEdges)
    gatherNeighbours(d.asInstanceOf[LocalClusterCoefficientVertex], s.asInstanceOf[ArrayBuffer[LocalClusterCoefficientVertex]])
  }

  def run(graph: Graph[Any, Any]) {
  }

  def gatherNeighbours(vertex: LocalClusterCoefficientVertex, vertexArray: ArrayBuffer[LocalClusterCoefficientVertex]) {
    for (n <- vertex.neighbours) {
      val a = vertexArray.find(p => p.id.equals(n)).get
      val neighboursOfneighbour = a.neighbours.filter(p => vertex.neighbours.contains(p))
      println(vertex.id + "  " + n + "  " + neighboursOfneighbour)
    }
  }
}
class LocalClusterCoefficientVertex(id: Any) extends DataGraphVertex(id, Set[Int]()) {

  type Signal = Int
  type State = Set[Int]
  var neighbours = scala.collection.mutable.Set[Int]()
  def collect: State = {
    val mostRecentNeighbours = mostRecentSignalMap.keys.toSet

    for (neighbour <- mostRecentNeighbours) {
      neighbours.add(Integer.valueOf(neighbour.toString))
    }
    for (neighbour <- outgoingEdges) {
      neighbours.add(Integer.valueOf(neighbour._1.toString))
    }
    neighbours.toSet

  }

}
class LocalClusterCoefficientEdge(t: Any) extends DefaultEdge(t) {
  type Source = DataGraphVertex[Any, Any]
  def signal = source.id
}
