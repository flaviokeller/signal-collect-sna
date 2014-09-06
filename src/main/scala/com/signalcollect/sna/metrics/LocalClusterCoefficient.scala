/*
 *  @author Flavio Keller
 *
 *  Copyright 2014 University of Zurich
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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

object LocalClusterCoefficient extends App {

  def run(graph: Graph[Any, Any]): ExecutionResult = {
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
      treeMap.put(d._1.toString, BigDecimal(lcc).round(new MathContext(3)).toDouble.asInstanceOf[Object])
    }
    val averageclcoeff = sumOfLCC / vertexMap.toMap.size.toDouble

    new ExecutionResult(new ComputationResults(BigDecimal(averageclcoeff).round(new MathContext(3)).toDouble, treeMap), s)
  }

  def gatherNeighbours(vertex: LocalClusterCoefficientVertex, vertexMap: Map[Int, LocalClusterCoefficientVertex]): Double = {
    var connectedNeighbours = 0.0
    var passedNeighbours = scala.collection.mutable.Set[Int]()
    val neighbourSet = vertex.state.keySet.union(vertex.outgoingEdges.keySet.asInstanceOf[Set[Int]])
    val nrOfPossibleConnections = if (neighbourSet.size == 1) 1 else (neighbourSet.size * (neighbourSet.size - 1)).toDouble
    for (outgoingNeighbour <- vertex.outgoingEdges) {
      val neighbourVertex = vertexMap.get(Integer.valueOf(outgoingNeighbour._2.targetId.toString)).get
      if (!passedNeighbours.contains(Integer.valueOf(outgoingNeighbour._1.toString))) {
        val outgoingneighboursOfneighbour = neighbourVertex.state.filter(p => neighbourSet.contains(p._1))
        connectedNeighbours += outgoingneighboursOfneighbour.size
      }
      passedNeighbours.add(Integer.valueOf(outgoingNeighbour._1.toString))
    }

    for (incomingNeighbour <- vertex.state) {
      val neighbourVertex = vertexMap.get(incomingNeighbour._1).get
      val neighbourSet = vertex.state.keySet.union(vertex.outgoingEdges.keySet.asInstanceOf[Set[Int]])
      if (!passedNeighbours.contains(incomingNeighbour._1)) {
        val outgoingneighboursOfneighbour = neighbourVertex.state.filter(p => neighbourSet.contains(p._1))
        connectedNeighbours += outgoingneighboursOfneighbour.size
      }
      passedNeighbours.add(Integer.valueOf(incomingNeighbour._1.toString))
    }
    val localClusterCoefficient = connectedNeighbours / nrOfPossibleConnections
    localClusterCoefficient
  }
}
class LocalClusterCoefficientVertex(id: Any) extends DataGraphVertex(id, Map[Int, Set[Int]]()) {
  type Signal = Set[Int]
  type State = Map[Int, Set[Int]]
  def collect: State = {
    var neighbours = scala.collection.mutable.Map[Int, Set[Int]]()
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
