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
import scala.collection.mutable.SynchronizedBuffer
import com.signalcollect.DataGraphVertex
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.Graph
import com.signalcollect.Vertex
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.sna.ComputationResults
import com.signalcollect.sna.ExecutionResult
import com.signalcollect.sna.constants.SignalCollectSNAConstants
import com.signalcollect.DefaultEdge

/**
 * Executes the Triad Census Algorithm for Signal/Collect
 */
object TriadCensus {

  /**
   * Function responsible for the execution of the algorithm
   * @param the parsed graph, instance of {@link com.signalcollect.Graph}
   * @return {@link com.signalcollect.sna.ExecutionResult} object
   *
   * Parts of this code is an adaption of the triad census algorithm for signal/collect and scala.
   * Originally implemented by Batagelj and Mrvar at the University of Ljubljana, Slovenia
   *
   * @see <a href="http://vlado.fmf.uni-lj.si/vlado/papers/triads.pdf">Triad Census algorithm</a>
   *
   */
  def run(graph: Graph[Any, Any]): ExecutionResult = {
    val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
    val stats = graph.execute(execmode)
    graph.awaitIdle

    var vertexArray = new ArrayBuffer[Vertex[Any, _, Any, Any]] with SynchronizedBuffer[Vertex[Any, _, Any, Any]]
    graph.foreachVertex(v => vertexArray += v)

    /*
     * scala SortedMap with the vertex' id as key and the actual vertex object as value
     * It is important to use a SortedMap here because the algorithm needs to iterate
     * through the vertices in ascending id order
     */
    var vertexMap = SortedMap[Int, TriadCensusVertex]()

    for (vertex <- vertexArray) {
      vertexMap += ((vertex.id.asInstanceOf[Int], vertex.asInstanceOf[TriadCensusVertex]))
    }
    graph.shutdown

    /*
     * java TreeMap in order to package the values into a generic ExecutionResult object 
     * which should be returned at the end
     */
    var treeMap = new java.util.TreeMap[String, Object]()

    /*
     * scala SortedMap to work with the values and determine the triad type distribution
     * the map values are of type Long because some really high numbers may be reached with large graphs
     */
    var countMap = SortedMap[Int, Long]()

    /*
     * The following code part is an adaption of the triad census algorithm for Signal/Collect and Scala. 
     */
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

          /*
           * vertices neighbouring to either the vertex or the current neighbourVertex (excluding the vertex and the current neighbour)
           */
          val neighboursOfBothVertices = vertex._2.neighbours union neighbourVertex.neighbours diff Set(vertex._1, neighbour)

          if (vertex._2.outgoingEdges.contains(neighbourVertex.id) && neighbourVertex.outgoingEdges.contains(vertex._2.id)) {
            triadType = 3;
          } else {
            triadType = 2;
          }
          var countValue = countMap.get(triadType).getOrElse(0.toLong)

          countMap += ((triadType, countValue + (vertexMap.size - neighboursOfBothVertices.size - 2).toLong))
          for (neighbourOfBoth <- neighboursOfBothVertices) {
            if (neighbour < neighbourOfBoth || (vertex._2.id.asInstanceOf[Int] < neighbourOfBoth && neighbourOfBoth < neighbour && !vertex._2.neighbours.contains(neighbourOfBoth))) {
              val neighbourOfBothVertex = vertexMap.get(neighbourOfBoth).get

              /*
               * The triadType is determined by the constant codeToType array by using the triCode function
               */
              triadType = SignalCollectSNAConstants.codeToType(triCode(vertex._2, neighbourVertex, neighbourOfBothVertex))

              countValue = countMap.get(triadType).getOrElse(0)
              countMap += ((triadType, countValue + 1.toLong))
            }
          }
        }
      }
      var sum = 0.toLong
      for (i <- 2 to 16) {
        sum += countMap.get(i).getOrElse(0.toLong)
      }
      countMap += ((1, ((vertexMap.size.toLong * (vertexMap.size - 1).toLong * (vertexMap.size - 2).toLong) / 6).toLong - sum))
    }

    for (count <- countMap) {
      treeMap.put(count._1.toString, count._2.asInstanceOf[Object])
    }

    /*
     * If a triad type has no occurences in a graph, the value 0 has to be explicitly put in the map for it
     */
    for (i <- 1 to 16) {
      if (treeMap.get(i.toString) == null) {
        treeMap.put(i.toString, 0.toLong.asInstanceOf[Object])
      }
    }

    new ExecutionResult(new ComputationResults(0.0, treeMap), vertexArray, stats)
  }

  /**
   * Determines a triad code among three vertices according to how they are linked
   *
   * @param u: vertex 1
   * @param v: vertex 2
   * @param w: vertex 3
   * @return the Triad code
   */
  def triCode(u: TriadCensusVertex, v: TriadCensusVertex, w: TriadCensusVertex): Int = {
    var i = 0
    if (link(v, u)) i += 1
    if (link(u, v)) i += 2
    if (link(v, w)) i += 4
    if (link(w, v)) i += 8
    if (link(u, w)) i += 16
    if (link(w, u)) i += 32
    i
  }

  /**
   * Determines if a vertex belongs to the target vertices of another vertex' outgoing edges
   * @param u: vertex 1
   * @param v: vertex 2
   * @return true if v is among the targets of u's outgoing edges, false otherwise
   */
  def link(u: TriadCensusVertex, v: TriadCensusVertex): Boolean = {
    u.outgoingEdges.contains(v.id)
  }

}

/**
 * Represents a vertex of a Triad Census graph
 * Extends {@link com.signalcollect.DataGraphVertex}
 * @param the vertex' id
 */
class TriadCensusVertex(id: Int) extends DataGraphVertex(id, 0) {
  type Signal = Int
  type State = Int

  /*
   * this set stores all ids of the vertex' neighbours
   */
  var neighbours = scala.collection.mutable.Set[Int]()

  /**
   * The collect function puts all incoming and outgoing neighbours into a set
   */
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

/**
 * Represents an edge of a Triad census graph
 * Extends {@link com.signalcollect.DefaultEdge}
 * @param the target vertex' id
 */
class TriadCensusEdge(t: Int) extends DefaultEdge(t) {
  type Source = DataGraphVertex[Any, Any]

  /**
   * The signal function sends the source vertex' id to its target vertex
   */
  def signal = source.id
}
