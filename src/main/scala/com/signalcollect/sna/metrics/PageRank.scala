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
import scala.math.BigDecimal
import com.signalcollect.DataGraphVertex
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.Graph
import com.signalcollect.Vertex
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.sna.ComputationResults
import com.signalcollect.sna.ExecutionResult
import com.signalcollect.DefaultEdge

object PageRank {
  def run(graph: Graph[Any, Any]): ExecutionResult = {
    val avgVertex = new AveragePageRankVertex("Average")
    graph.addVertex(avgVertex)
    graph.foreachVertex((v: Vertex[Any, _, Any, Any]) => graph.addEdge(v.id, new AveragePageRankEdge(avgVertex.id)))
    graph.foreachVertex((v: Vertex[Any, _, Any, Any]) => graph.addEdge(avgVertex.id, new AveragePageRankEdge(v.id)))
    val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
    val stats = graph.execute(execmode)
    graph.awaitIdle
    var vertexArray = new ArrayBuffer[Vertex[Any, _, Any, Any]] with SynchronizedBuffer[Vertex[Any, _, Any, Any]]
    graph.foreachVertex(v => vertexArray += v)
    graph.shutdown
    new ExecutionResult(new ComputationResults(avgVertex.state, filterInteger(vertexArray)), vertexArray, stats)
  }

  def filterInteger(vertexArray: ArrayBuffer[Vertex[Any, _, Any, Any]]): java.util.Map[String, Object] = {
    var vertices = new java.util.TreeMap[String, Object]
    for (vertex <- vertexArray) {
      vertices.put(vertex.id.toString, vertex.state.asInstanceOf[java.lang.Double])
    }
    vertices
  }
}

class PageRankVertex(id: Any, dampingFactor: Double = 0.85) extends DataGraphVertex(id, 1 - dampingFactor) {

  type Signal = Tuple2[Any, Any]
  type State = Double
  /**
   * The collect function calculates the rank of this vertex based on the rank
   *  received from neighbors and the damping factor.
   */
  def collect: State = {
    val pageRankSignals = mostRecentSignalMap.filter(signal => !signal._1.equals("Average")).values.toList //getClass.toString().contains("Average")).values.toList
    var sum = 0.0
    if (pageRankSignals.isEmpty) {
      BigDecimal.valueOf(state).round(new MathContext(3)).toDouble
    } else {
      for (signal <- pageRankSignals) {
        sum += signal._2.asInstanceOf[Double]
      }
      BigDecimal.valueOf(1 - dampingFactor + dampingFactor * sum).round(new MathContext(3)).toDouble
    }
  }
  override def scoreSignal: Double = {
    lastSignalState match {
      case None => 1
      case Some(oldState) => (state - oldState).abs
    }
  }

}
class PageRankEdge(t: Any) extends DefaultEdge(t) {
  type Source = PageRankVertex

  /**
   * The signal function calculates how much rank the source vertex
   *  transfers to the target vertex.
   */
  def signal = {
    if (source.outgoingEdges.contains("Average")) {
      var outweightsNoAvg = (source.sumOfOutWeights - 1)
      Tuple2(source.id, source.state * weight / outweightsNoAvg)
    } else {
      Tuple2(source.id, source.state * weight / source.sumOfOutWeights)
    }
  }
}

class AveragePageRankVertex(id: String) extends DataGraphVertex(id, 0.0) {

  type Signal = Tuple2[Any, Any]
  type State = Double

  def collect: State = {
    val pageRankSignals = mostRecentSignalMap.filter(signal => !signal._1.equals("Average")).values.toList //getClass.toString().contains("Average")).values.toList
    var sum = 0.0
    for (signal <- pageRankSignals) {
      sum += signal._2.asInstanceOf[Double]
    }
    scala.math.BigDecimal.valueOf(sum / pageRankSignals.size.toDouble).round(new MathContext(3)).toDouble
  }
}

class AveragePageRankEdge(t: Any) extends DefaultEdge(t) {
  type Source = DataGraphVertex[Any, Any]
  def signal = Tuple2(source, source.state)
}
