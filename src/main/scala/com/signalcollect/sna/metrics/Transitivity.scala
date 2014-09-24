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

import scala.collection.SortedMap
import scala.collection.mutable.ArrayBuffer
import com.signalcollect.DataGraphVertex
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.Graph
import com.signalcollect.Vertex
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.sna.ComputationResults
import com.signalcollect.sna.ExecutionResult
import com.signalcollect.sna.constants.SignalCollectSNAConstants
import com.signalcollect.DefaultEdge
import scala.collection.mutable.SynchronizedBuffer

object Transitivity {

  def run(graph: Graph[Any, Any]): ExecutionResult = {
    val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
    val stats = graph.execute(execmode)
    graph.awaitIdle

    var vertexArray = new ArrayBuffer[Vertex[Any, _, Any, Any]] with SynchronizedBuffer[Vertex[Any, _,Any,Any]]
    graph.foreachVertex(v => vertexArray += v)
    var vertexMap = SortedMap[Int, TransitivityVertex]()
    for (vertex <- vertexArray) {
      vertexMap += ((vertex.id.asInstanceOf[Int], vertex.asInstanceOf[TransitivityVertex]))
    }
    graph.shutdown
    var treeMap = new java.util.TreeMap[String, Object]()
    var countMap = SortedMap[Int, Int]()

    for (vertex <- vertexMap.toMap) {
      if (vertex._2.neighbours.isEmpty) {
        for (outgoingneighbour <- vertex._2.outgoingEdges) {
          vertex._2.neighbours += outgoingneighbour._1.asInstanceOf[Int]
        }
      }
      for (neighbour <- vertex._2.neighbours) {
        if (vertex._2.id.asInstanceOf[Int] < neighbour) {
          var triadType = -1;
          val neighbourVertex = vertexMap.get(neighbour).get

          val neighboursOfBothVertices = vertex._2.neighbours union neighbourVertex.neighbours diff Set(vertex._1, neighbour) //common neighbours?

          if (vertex._2.outgoingEdges.contains(neighbourVertex.id) && neighbourVertex.outgoingEdges.contains(vertex._2.id)) {
            triadType = 3;
          } else {
            triadType = 2;
          }
          var countValue = countMap.get(triadType).getOrElse(0)

          countMap += ((triadType, countValue + (vertexMap.size - neighboursOfBothVertices.size - 2)))
          for (neighbourOfBoth <- neighboursOfBothVertices) {
            if (neighbour < neighbourOfBoth || (vertex._2.id.asInstanceOf[Int] < neighbourOfBoth && neighbourOfBoth < neighbour && !vertex._2.neighbours.contains(neighbourOfBoth))) {
              val neighbourOfBothVertex = vertexMap.get(neighbourOfBoth).get

              triadType = SignalCollectSNAConstants.codeToType(triCode(vertex._2, neighbourVertex, neighbourOfBothVertex))

              countValue = countMap.get(triadType).getOrElse(0)
              countMap += ((triadType, countValue + 1))
            }

          }
        }
      }
      var sum = 0
      for (i <- 2 to 16) {
        sum += countMap.get(i).getOrElse(0)
      }
      countMap += ((1, ((vertexMap.size * (vertexMap.size - 1) * (vertexMap.size - 2)) / 6) - sum))
    }

    for (count <- countMap) {
      treeMap.put(count._1.toString, count._2.asInstanceOf[Object])
    }
    for (i <- 1 to 16) {
      if (treeMap.get(i.toString) == null) {
        treeMap.put(i.toString, Integer.valueOf(0))
      }
    }
    new ExecutionResult(new ComputationResults(0.0, treeMap), vertexArray)
  }

  /*
   * this function is an adaption of the triad census algorithm for signal/collect and scala
   */
  def getCounts(vertex: TransitivityVertex, vertexMap: Map[Int, TransitivityVertex]): Map[Int, Int] = {
    var countMap = SortedMap[Int, Int]()
    for (neighbour <- vertex.neighbours) {
      if (vertex.id.asInstanceOf[Int] < neighbour) {
        var triadType = -1;
        val neighbourVertex = vertexMap.get(neighbour).get

        val neighboursOfBothVertices = vertex.neighbours union neighbourVertex.neighbours //common neighbours?

        if (vertex.outgoingEdges.contains(neighbourVertex.id) && neighbourVertex.outgoingEdges.contains(vertex.id)) {
          triadType = 3;
        } else {
          triadType = 2;
        }
        var countValue = countMap.get(triadType).getOrElse(0)
        countMap += ((triadType, countValue + (vertexMap.size - vertex.outgoingEdges.size - 2)))

        for (neighbourOfBoth <- neighboursOfBothVertices) {
          if (neighbour < neighbourOfBoth || (vertex.id.asInstanceOf[Int] < neighbourOfBoth && neighbourOfBoth < neighbour && !vertex.neighbours.contains(neighbourOfBoth))) {
            val neighbourOfBothVertex = vertexMap.get(neighbourOfBoth).get

            triadType = SignalCollectSNAConstants.codeToType(triCode(vertex, neighbourVertex, neighbourOfBothVertex))
            countValue = countMap.get(triadType).getOrElse(0)
            countMap += ((triadType, countValue + 1))
          }

        }
      }
    }
    var sum = 0
    for (i <- 2 to 16) {
      sum += countMap.get(i).getOrElse(0)
    }
    countMap += ((1, ((vertexMap.size * (vertexMap.size - 1) * (vertexMap.size - 2)) / 6) - sum))
    countMap.toMap
  }

  def triCode(u: TransitivityVertex, v: TransitivityVertex, w: TransitivityVertex): Int = {
    var i = 0
    if (link(v, u)) i += 1
    if (link(u, v)) i += 2
    if (link(v, w)) i += 4
    if (link(w, v)) i += 8
    if (link(u, w)) i += 16
    if (link(w, u)) i += 32
    i
  }

  def link(u: TransitivityVertex, v: TransitivityVertex): Boolean = {
    u.outgoingEdges.contains(v.id)
  }

}
class TransitivityVertex(id: Int) extends DataGraphVertex(id, 0) {
  type Signal = Int
  type State = Int
  var neighbours = scala.collection.mutable.Set[Int]()
  def collect: State = {
    for (incomingneighbour <- mostRecentSignalMap) {
      neighbours += incomingneighbour._1.asInstanceOf[Int]
      for (outgoingneighbour <- outgoingEdges) {
        neighbours += outgoingneighbour._1.asInstanceOf[Int]
      }
    }
    neighbours.size
  }

}
class TransitivityEdge(t: Int) extends DefaultEdge(t) {
  type Source = DataGraphVertex[Any, Any]
  def signal = source.id
}
