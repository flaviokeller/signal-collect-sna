package com.signalcollect.sna.metrics

import java.math.MathContext
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SynchronizedBuffer
import com.signalcollect.DataGraphVertex
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.Graph
import com.signalcollect.Vertex
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.sna.ComputationResults
import com.signalcollect.sna.ExecutionResult
import com.signalcollect.DefaultEdge
import com.signalcollect.sna.NodeTriad
import com.signalcollect.sna.NodeTriad

object Transitivity extends App {
 
  def run(graph: Graph[Any, Any]): ExecutionResult = {
    val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
    val stats = graph.execute(execmode)
    graph.awaitIdle

    var s = new ArrayBuffer[Vertex[Any, _]] with SynchronizedBuffer[Vertex[Any, _]]
    graph.foreachVertex(v => s += v)
    var vertexMap = scala.collection.mutable.Map[Int, TransitivityVertex]()
    for (v <- s) {
      println(v)
      vertexMap.put(Integer.valueOf(v.id.toString), v.asInstanceOf[TransitivityVertex])
    }
    graph.shutdown

    var sumOfLCC = 0.0
    var treeMap = new java.util.TreeMap[String, Object]()
    //    for (d <- vertexMap) {
    //      val lcc = gatherNeighbours(d._2, vertexMap.toMap)
    //      sumOfLCC += lcc
    //      treeMap.put(d._1.toString, BigDecimal(lcc).round(new MathContext(3)))
    //    }
    val averageclcoeff = sumOfLCC / vertexMap.toMap.size.toDouble

    new ExecutionResult(new ComputationResults(BigDecimal(averageclcoeff).round(new MathContext(3)).toDouble, treeMap), s)

  }

  //  def gatherNeighbours(vertex: TransitivityVertex, vertexMap: Map[Int, TransitivityVertex]): Double = {
  //    var connectedNeighbours = 0.0
  //    var passedNeighbours = scala.collection.mutable.Set[Int]()
  //    val neighbourSet = vertex.state.keySet.union(vertex.outgoingEdges.keySet.asInstanceOf[Set[Int]])
  //    val nrOfPossibleConnections = if (neighbourSet.size == 1) 1 else (neighbourSet.size * (neighbourSet.size - 1)).toDouble
  //    for (outgoingNeighbour <- vertex.outgoingEdges) {
  //      val neighbourVertex = vertexMap.get(Integer.valueOf(outgoingNeighbour._2.targetId.toString)).get
  //      if (!passedNeighbours.contains(Integer.valueOf(outgoingNeighbour._1.toString))) {
  //        val outgoingneighboursOfneighbour = neighbourVertex.state.filter(p => neighbourSet.contains(p._1))
  //        connectedNeighbours += outgoingneighboursOfneighbour.size
  //      }
  //      passedNeighbours.add(Integer.valueOf(outgoingNeighbour._1.toString))
  //    }
  //
  //    for (incomingNeighbour <- vertex.state) {
  //      val neighbourVertex = vertexMap.get(incomingNeighbour._1).get
  //      val neighbourSet = vertex.state.keySet.union(vertex.outgoingEdges.keySet.asInstanceOf[Set[Int]])
  //      if (!passedNeighbours.contains(incomingNeighbour._1)) {
  //        val outgoingneighboursOfneighbour = neighbourVertex.state.filter(p => neighbourSet.contains(p._1))
  //        connectedNeighbours += outgoingneighboursOfneighbour.size
  //      }
  //      passedNeighbours.add(Integer.valueOf(incomingNeighbour._1.toString))
  //    }
  //    val localClusterCoefficient = connectedNeighbours / nrOfPossibleConnections
  //    localClusterCoefficient
  //  }
}
class TransitivityVertex(id: Any) extends DataGraphVertex(id, 0) {
  type Signal = Int
  type State = Int
  var triadSet = scala.collection.mutable.Set[NodeTriad]()
  def collect: State = {
    for (incomingneighbour <- mostRecentSignalMap) {
      for (outgoingneighbour <- outgoingEdges) {
        val triadNotPresent = triadSet.filter(t => t.headId == Integer.valueOf(incomingneighbour._1.toString) && t.centerId == Integer.valueOf(id.toString) && t.tailId == Integer.valueOf(outgoingneighbour._1.toString)).isEmpty
        if (triadNotPresent) {
          val triad = new NodeTriad(Integer.valueOf(incomingneighbour._1.toString), Integer.valueOf(id.toString), Integer.valueOf(outgoingneighbour._1.toString))
          triadSet.add(triad)
          if (id == 1) {
            println("new triad constructed: " + triad.headId + " " + triad.centerId + " " + triad.tailId + " " + triad.triadType)
          }

        }
      }
    }
    triadSet.size
  }
  
}
class TransitivityEdge(t: Int) extends DefaultEdge(t) {
  type Source = DataGraphVertex[Any, Any]
  def signal = source.id
}
