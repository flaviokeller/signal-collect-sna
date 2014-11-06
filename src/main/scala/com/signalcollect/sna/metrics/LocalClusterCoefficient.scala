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
import scala.BigDecimal
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

/**
 * Executes the calculation of the local cluster coefficient values of a graph's vertices
 */
object LocalClusterCoefficient {

  /**
   * Function responsible for the execution
   * @param the parsed graph, instance of {@link com.signalcollect.Graph}
   * @return {@link com.signalcollect.sna.ExecutionResult} object
   */
  def run(graph: Graph[Any, Any]): ExecutionResult = {
    val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
    val stats = graph.execute(execmode)
    graph.awaitIdle

    var vertexArray = new ArrayBuffer[Vertex[Any, _, Any, Any]] with SynchronizedBuffer[Vertex[Any, _, Any, Any]]
    graph.foreachVertex(v => vertexArray += v)
    var vertexMap = scala.collection.mutable.Map[Int, LocalClusterCoefficientVertex]()
    for (vertex <- vertexArray) {
      vertexMap.put(vertex.id.asInstanceOf[Int], vertex.asInstanceOf[LocalClusterCoefficientVertex])
    }
    graph.shutdown

    var sumOfLCC = 0.0
    var treeMap = new java.util.TreeMap[String, Object]()

    /*
     * determining the single local cluster coefficient values and adding them up
     * in order to determine the average later
     */
    for (d <- vertexMap) {
      val lcc = gatherNeighbours(d._2, vertexMap.toMap)
      sumOfLCC += lcc
      treeMap.put(d._1.toString, BigDecimal(lcc).round(new MathContext(3)).toDouble.asInstanceOf[Object])
    }
    val averageclcoeff = sumOfLCC / vertexMap.toMap.size.toDouble

    new ExecutionResult(new ComputationResults(BigDecimal(averageclcoeff).round(new MathContext(3)).toDouble, treeMap), vertexArray, stats)
  }

  /**
   * Function which is responsible to gather a vertex' neighbours together
   * and calculate its local cluster coefficient value out of it
   */
  def gatherNeighbours(vertex: LocalClusterCoefficientVertex, vertexMap: Map[Int, LocalClusterCoefficientVertex]): Double = {
    var connectedNeighbours = 0.0
    var passedNeighbours = scala.collection.mutable.Set[Int]()

    /*
     * set that represents the incoming edges and outgoing edges together
     */
    val neighbourSet = vertex.state.keySet.union(vertex.outgoingEdges.keySet.asInstanceOf[Set[Int]])

    if (neighbourSet.isEmpty) {
      0.0
    } else {
      val nrOfPossibleConnections = if (neighbourSet.size == 1) 1 else (neighbourSet.size * (neighbourSet.size - 1)).toDouble

      /*
       * iterating through outgoing edges
       */
      for (outgoingNeighbour <- vertex.outgoingEdges) {
        val neighbourVertex = vertexMap.get(outgoingNeighbour._2.targetId.asInstanceOf[Int]).get
        if (!passedNeighbours.contains(outgoingNeighbour._1.asInstanceOf[Int])) {
          val outgoingneighboursOfneighbour = neighbourVertex.state.filter(p => neighbourSet.contains(p._1))
          connectedNeighbours += outgoingneighboursOfneighbour.size
        }
        passedNeighbours.add(outgoingNeighbour._1.asInstanceOf[Int])
      }

      /*
       * iterating through incoming edges
       */
      for (incomingNeighbour <- vertex.state) {
        val neighbourVertex = vertexMap.get(incomingNeighbour._1).get
        val neighbourSet = vertex.state.keySet.union(vertex.outgoingEdges.keySet.asInstanceOf[Set[Int]])
        if (!passedNeighbours.contains(incomingNeighbour._1)) {
          val outgoingneighboursOfneighbour = neighbourVertex.state.filter(p => neighbourSet.contains(p._1))
          connectedNeighbours += outgoingneighboursOfneighbour.size
        }
        passedNeighbours.add(incomingNeighbour._1.asInstanceOf[Int])
      }
      val localClusterCoefficient = connectedNeighbours / nrOfPossibleConnections
      localClusterCoefficient
    }
  }
}

/**
 * Represents a vertex of a Local Cluster Coefficient graph, extends {@link com.signalcollect.DataGraphVertex}
 * @param the vertex' id
 */
class LocalClusterCoefficientVertex(id: Any) extends DataGraphVertex(id, Map[Int, Set[Int]]()) {
  type Signal = Set[Int]
  type State = Map[Int, Set[Int]]

  /**
   * The collect function stores all ids and outgoing edges of the incoming edges' source vertices in a map
   */
  def collect: State = {
    var neighbours = scala.collection.mutable.Map[Int, Set[Int]]()
    for (neighbour <- mostRecentSignalMap) {
      neighbours.put(neighbour._1.asInstanceOf[Int], neighbour._2)
    }
    neighbours.toMap
  }
}

/**
 * Represents an edge of a Local Cluster Coefficient graph, extends {@link com.signalcollect.DefaultEdge}
 * @param the traget vertex' id
 */
class LocalClusterCoefficientEdge(t: Int) extends DefaultEdge(t) {
  type Source = DataGraphVertex[Any, Any]

  /**
   * The signal function transmits all outgoing edges as a set to its target vertex
   */
  def signal = source.outgoingEdges.values.toSet
}
