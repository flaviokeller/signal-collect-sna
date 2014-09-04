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
import com.signalcollect.DefaultEdge
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.Graph
import com.signalcollect.Vertex
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.sna.ComputationResults
import com.signalcollect.sna.ExecutionResult
import com.signalcollect.GraphBuilder

object Degree {

  def run(pGraph: Graph[Any, Any]): ExecutionResult = {

    val avgVertex = new AverageDegreeVertex("Average")
    var graph: Graph[Any, Any] = null
    if (pGraph == null) {
      graph = GraphBuilder.build
      println("built new graph for degrees")
    } else {
      graph = pGraph
    }
    graph.addVertex(avgVertex)
    graph.foreachVertex((v: Vertex[Any, _]) => graph.addEdge(v.id, new AverageDegreeEdge(avgVertex.id)))
    graph.foreachVertex((v: Vertex[Any, _]) => graph.addEdge(avgVertex.id, new AverageDegreeEdge(v.id)))
    val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
    val stats = graph.execute(execmode)
    graph.awaitIdle
    var s = new ArrayBuffer[Vertex[Any, _]] with SynchronizedBuffer[Vertex[Any, _]]
    graph.foreachVertex(v => s += v)
    graph.shutdown
    new ExecutionResult(new ComputationResults(BigDecimal(avgVertex.state).round(new MathContext(3)).toDouble, filterInteger(s)), s)
  }

  def filterInteger(l: ArrayBuffer[Vertex[Any, _]]): java.util.TreeMap[String, Object] = {
    var vertices = new java.util.TreeMap[String, Object]
    for (vertex <- l) {
      vertices.put(vertex.id.toString, vertex.state.toString)
    }
    vertices
  }
}

class DegreeVertex(id: Any) extends DataGraphVertex(id, 0) {

  type Signal = DataGraphVertex[Any, Any]
  type State = Int

  lazy val edgeSet = outgoingEdges.values.toSet
  def collect: State = {
    val degreeEdges = edgeSet.filter(edge => edge.targetId.isInstanceOf[Integer])
    val degreeSignals = mostRecentSignalMap.values.toList.filter(signal => !signal.getClass.toString().contains("Average"))
    degreeEdges.size + degreeSignals.size
  }

}
class DegreeEdge(t: Any) extends DefaultEdge(t) {
  type Source = DataGraphVertex[Any, Any]
  def signal = source
}

class AverageDegreeVertex(id: String) extends DataGraphVertex(id, 0.0) {

  type Signal = DataGraphVertex[Any, Any]
  type State = Double
  def collect: State = {
    val degreeSignals = mostRecentSignalMap.filter(signal => !signal._2.getClass().toString().contains("Average")).values.toList
    var sum = 0
    for (signal <- degreeSignals) {
      sum += Integer.valueOf(signal.state.toString)
    }
    sum.toDouble / degreeSignals.size.toDouble
  }
}

class AverageDegreeEdge(t: Any) extends DefaultEdge(t) {
  type Source = DataGraphVertex[Any, Any]
  def signal = source
}
