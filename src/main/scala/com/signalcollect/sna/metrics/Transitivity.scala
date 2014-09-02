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
import com.signalcollect.sna.TriadType

object Transitivity extends App {

  def run(graph: Graph[Any, Any]): ExecutionResult = {
    val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
    val stats = graph.execute(execmode)
    graph.awaitIdle

    var s = new ArrayBuffer[Vertex[Any, _]] with SynchronizedBuffer[Vertex[Any, _]]
    graph.foreachVertex(v => s += v)
    var vertexMap = scala.collection.mutable.Map[Int, TransitivityVertex]()
    for (v <- s) {
      vertexMap.put(Integer.valueOf(v.id.toString), v.asInstanceOf[TransitivityVertex])
    }
    graph.shutdown

    var treeMap = new java.util.TreeMap[String, Object]()
    for (d <- vertexMap) {
      determineTriadType(d._2, vertexMap.toMap)
      //      val lcc = gatherNeighbours(d._2, vertexMap.toMap)
      //      sumOfLCC += lcc
      //      treeMap.put(d._1.toString, BigDecimal(lcc).round(new MathContext(3)))
    }

    //    new ExecutionResult(new ComputationResults(BigDecimal(averageclcoeff).round(new MathContext(3)).toDouble, treeMap), s)
    null
  }

  def determineTriadType(vertex: TransitivityVertex, vertexMap: Map[Int, TransitivityVertex]): Double = {
    if (vertex.state != 0) {
      for (triad <- vertex.triadSet) {
        if (vertexMap.get(triad.headId).get.outgoingEdges.contains(triad.tailId)) {
          triad.triadType = TriadType.transitive
        		  println("id: " + vertex.id + "\ttriad: " + triad)
        }
//        if (Integer.valueOf(vertex.id.toString) < 15)
      }
    }
    0.0
  }
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
