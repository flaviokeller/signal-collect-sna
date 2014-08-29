package com.signalcollect.sna.metrics

import java.math.MathContext

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SynchronizedBuffer

import com.signalcollect.DataGraphVertex
import com.signalcollect.DefaultEdge
import com.signalcollect.Edge
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.Graph
import com.signalcollect.Vertex
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.sna.ComputationResults
import com.signalcollect.sna.ExampleGraph
import com.signalcollect.sna.ExecutionResult

object LocalClusterCoefficient extends App {

  def run(graph: Graph[Any, Any]): ExecutionResult = {
    //    val graph = GraphBuilder.build
    //    val eg = new ExampleGraph
    //    eg.baseLocalClusterCoefficientGraph(graph)
    //    eg.extendLocalClusterCoefficientGraph(graph)

    val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
    val stats = graph.execute(execmode)
    graph.awaitIdle

    var s = new ArrayBuffer[Vertex[Any, _]] with SynchronizedBuffer[Vertex[Any, _]]
    graph.foreachVertex(v => s += v)
    var vertexMap = scala.collection.mutable.Map[Int, LocalClusterCoefficientVertex]()
    for (v <- s) {
      vertexMap.put(Integer.valueOf(v.id.toString), v.asInstanceOf[LocalClusterCoefficientVertex])
    }
    graph.shutdown

    var sumOfLCC = 0.0
    var treeMap = new java.util.TreeMap[String, Object]()
    for (d <- vertexMap) {
      val lcc = gatherNeighbours(d._2, vertexMap.toMap)
      sumOfLCC += lcc
      treeMap.put(d._1.toString, BigDecimal(lcc).round(new MathContext(3)))
    }
    val averageclcoeff = sumOfLCC / vertexMap.toMap.size.toDouble

    new ExecutionResult(new ComputationResults(BigDecimal(averageclcoeff).round(new MathContext(3)).toDouble, treeMap), s)

  }

  def gatherNeighbours(vertex: LocalClusterCoefficientVertex, vertexMap: Map[Int, LocalClusterCoefficientVertex]): Double = {
    var connectedNeighbours = 0.0
    var passedNeighbours = scala.collection.mutable.Set[Int]()
    val neighbourSet = vertex.neighbours.keySet.union(vertex.outgoingEdges.keySet.asInstanceOf[Set[Int]])
    val nrOfPossibleConnections = if (neighbourSet.size == 1) 1 else (neighbourSet.size * (neighbourSet.size - 1)).toDouble
    for (outgoingNeighbour <- vertex.outgoingEdges) {
      val neighbourVertex = vertexMap.get(Integer.valueOf(outgoingNeighbour._2.targetId.toString)).get
      if (!passedNeighbours.contains(Integer.valueOf(outgoingNeighbour._1.toString))) {
        val outgoingneighboursOfneighbour = neighbourVertex.neighbours.filter(p => neighbourSet.contains(p._1))
        connectedNeighbours += outgoingneighboursOfneighbour.size
      }
      passedNeighbours.add(Integer.valueOf(outgoingNeighbour._1.toString))
    }

    for (incomingNeighbour <- vertex.state) {
      val neighbourVertex = vertexMap.get(incomingNeighbour._1).get
      val neighbourSet = vertex.neighbours.keySet.union(vertex.outgoingEdges.keySet.asInstanceOf[Set[Int]])
      if (!passedNeighbours.contains(incomingNeighbour._1)) {
        val outgoingneighboursOfneighbour = neighbourVertex.neighbours.filter(p => neighbourSet.contains(p._1))
        connectedNeighbours += outgoingneighboursOfneighbour.size
      }
      passedNeighbours.add(Integer.valueOf(incomingNeighbour._1.toString))
    }
    val localClusterCoefficient = connectedNeighbours / nrOfPossibleConnections
    //    println("id: " + vertex.id + "\tcn: " + connectedNeighbours + "\tt: " + nrOfPossibleConnections + "\tlcl clustercoeff.: " + BigDecimal(localClusterCoefficient).round(new MathContext(3)).toDouble)
    localClusterCoefficient
  }
}
class LocalClusterCoefficientVertex(id: Any) extends DataGraphVertex(id, Map[Int, Set[Int]]()) {
  type Signal = Set[Int]
  type State = Map[Int, Set[Int]]
  var neighbours = scala.collection.mutable.Map[Int, Set[Int]]()
  def collect: State = {
    val mostRecentNeighbours = mostRecentSignalMap.values
    for (neighbour <- mostRecentSignalMap) {
      neighbours.put(Integer.valueOf(neighbour._1.toString), neighbour._2)
    }
    neighbours.toMap
  }
}
class LocalClusterCoefficientEdge(t: Int) extends DefaultEdge(t) {
  type Source = DataGraphVertex[Any, Any]
  def signal = source.outgoingEdges.values.toSet
}
