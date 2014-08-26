package com.signalcollect.sna.metrics

import com.signalcollect.DataGraphVertex
import com.signalcollect.DefaultEdge
import com.signalcollect.Graph
import com.signalcollect.Vertex
import com.signalcollect.sna.ExampleGraph

object LocalClusterCoefficient extends App {

  val eg = new ExampleGraph
  def run(graph: Graph[Any, Any]) {
  }
  class LocalClusterCoefficientVertex(id: Any) extends DataGraphVertex(id, Set[Int]()) {

    type Signal = Int
    type State = Set[Int]
    var neighbours = scala.collection.mutable.Set[Int]()
    outgoingEdges
    def collect: State = {
      val mostRecentNeighbours = mostRecentSignalMap.keys.toSet
      for (neighbour <- mostRecentNeighbours) {
        neighbours.add(Integer.valueOf(neighbour.toString))
      }
      neighbours.toSet

    }

  }
  class LocalClusterCoefficientEdge(t: Any) extends DefaultEdge(t) {
    type Source = DataGraphVertex[Any, Any]
    def signal = source.id
  }
}