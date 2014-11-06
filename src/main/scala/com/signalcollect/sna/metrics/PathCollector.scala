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
import scala.collection.JavaConverters.mapAsScalaMapConverter
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SynchronizedBuffer
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.Graph
import com.signalcollect.Vertex
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.sna.ComputationResults
import com.signalcollect.sna.ExecutionResult
import com.signalcollect.sna.Path
import com.signalcollect.sna.constants.SNAClassNames
import com.signalcollect.DataGraphVertex
import com.signalcollect.DefaultEdge

/**
 * Executes the SNA methods and properties concerned with shortest paths
 */
object PathCollector {

  /**
   * Function responsible for the execution
   * --> is able to either determine Betweenness or Closeness and supports the calculation of a graph's diameter or reciprocity by collecting the shortest paths
   * @param the parsed graph, instance of {@link com.signalcollect.Graph}
   * @param className, determines if and what kind of metric is calculated (either Betweenness centrality or Closeness centrality)
   * @return {@link com.signalcollect.sna.ExecutionResult} object
   */
  def run(graph: Graph[Any, Any], className: SNAClassNames): ExecutionResult = {

    var vertexArray = new ArrayBuffer[Vertex[Any, _, Any, Any]] with SynchronizedBuffer[Vertex[Any, _, Any, Any]]

    /*
     * Execution mode should be synchronous in order to guarantee correct results
     */
    val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
    val stats = graph.execute(execmode)
    graph.awaitIdle
    graph.foreachVertex(v => vertexArray += v.asInstanceOf[PathCollectorVertex])

    /*
     * each vertex calculates its own betweenness or closeness if desired by the indicated {@link com.signalcollect.sna.constants.SNAClassNames}
     */
    if (className.equals(SNAClassNames.CLOSENESS)) {
      graph.foreachVertex(v => v.asInstanceOf[PathCollectorVertex].calcCloseness(allShortestPathsAsList(vertexArray.asInstanceOf[ArrayBuffer[PathCollectorVertex]])))
    } else if (className.equals(SNAClassNames.BETWEENNESS)) {
      graph.foreachVertex(v => v.asInstanceOf[PathCollectorVertex].calcBetweenness(allShortestPathsAsList(vertexArray.asInstanceOf[ArrayBuffer[PathCollectorVertex]])))
    }

    graph.shutdown

    var valueMap = new java.util.TreeMap[String, Object]
    var avg = 0.0

    /*
     * the vertices are added to the map if desired by the indicated {@link com.signalcollect.sna.constants.SNAClassNames}
     */
    if (className.equals(SNAClassNames.CLOSENESS)) {
      for (closenessVertex <- vertexArray) {
        valueMap.put(closenessVertex.id.toString, closenessVertex.asInstanceOf[PathCollectorVertex].closeness.asInstanceOf[Object])
      }
      avg = calcAvg(valueMap)
    } else if (className.equals(SNAClassNames.BETWEENNESS)) {
      for (betwennessVertex <- vertexArray) {
        valueMap.put(betwennessVertex.id.toString, betwennessVertex.asInstanceOf[PathCollectorVertex].betweenness.asInstanceOf[Object])
      }
      avg = calcAvg(valueMap)
    }
    new ExecutionResult(new ComputationResults(avg, valueMap), vertexArray, stats)
  }

  /**
   * Creates a map with all shortest paths (key is a vertex id, value is a list of all incoming shortest paths at this vertex)
   * @param an array of vertices
   * @return Map with all shortest paths
   */
  def allShortestPathsAsMap(vertexArray: ArrayBuffer[PathCollectorVertex]): Map[Int, List[Path]] = {
    var shortestPathMap = scala.collection.mutable.Map[Int, List[Path]]()
    for (targetVertex <- vertexArray) {
      var pathList = scala.collection.mutable.ListBuffer[Path]()
      try {
        pathList ++= targetVertex.shortestPaths.values.toList
      } catch {
        case noPath: NoSuchElementException => //do nothing
      }
      shortestPathMap.put(targetVertex.id, pathList.toList)
    }
    shortestPathMap.toMap
  }

  /**
   * Creates a list with all shortest paths
   * @param an array of vertices
   * @return Map with all shortest paths
   */
  def allShortestPathsAsList(vertexArray: ArrayBuffer[PathCollectorVertex]): List[Path] = {
    var shortestPathList = scala.collection.mutable.ListBuffer[Path]()
    for (targetVertex <- vertexArray.asInstanceOf[ArrayBuffer[PathCollectorVertex]]) {
      try {
        shortestPathList ++= targetVertex.shortestPaths.values.toList
      } catch {
        case noPath: NoSuchElementException => //do nothing
      }
    }
    shortestPathList.toList
  }

  /**
   * Calculates the average of a Map
   * can either be used for closeness or betweenness
   * @param the map (key = vertex id, value = vertex' value)
   * @return the average value
   */
  def calcAvg(valueMap: java.util.Map[String, Object]): Double = {
    val values = valueMap.asScala.asInstanceOf[scala.collection.mutable.Map[String, Double]].values.toList
    BigDecimal(values.foldLeft(0.0)(_ + _) / values.foldLeft(0.0)((r, c) => r + 1)).round(new MathContext(3)).toDouble
  }
}

/**
 * Represents a vertex of a Path Collection graph, extends {@link com.signalcollect.DataGraphVertex}
 * @param the vertex' id
 */
class PathCollectorVertex(id: Int) extends DataGraphVertex(id, Map[Int, Path]()) {
  type Signal = ArrayBuffer[Path]
  type State = Map[Int, Path]

  /*
   * stores all shortest paths arriving at this vertex 
   * key = source vertex, value = path it followed so far
   */
  var shortestPaths = scala.collection.mutable.Map[Int, Path]()
  var closeness = 0.0
  var betweenness = 0.0

  /**
   * The collect function gathers all incoming shortest paths and adds them to the map of shortest paths
   * --> the paths are only added if no path from that source vertex exists or if this path is shorter than a possible existing path
   */
  def collect: State = {
    for (pathArray <- mostRecentSignalMap.values) {
      for (path <- pathArray) {
        if (shortestPaths.contains(path.sourceVertexId)) {
          if (shortestPaths.get(path.sourceVertexId).get.path.size > path.path.size) {
            shortestPaths.put(path.sourceVertexId, path)
          }
        } else {
          shortestPaths.put(path.sourceVertexId, path)
        }
      }
    }
    shortestPaths.toMap
  }

  /**
   * Responsible to calculate the Closeness centrality of this vertex
   * @param the list of all shortest paths in the graph
   */
  def calcCloseness(shortestPathList: List[Path]) {
    val pathsThroughVertex = shortestPathList.filter(p => p.sourceVertexId == id)
    if (!pathsThroughVertex.isEmpty) {
      for (path <- pathsThroughVertex) {
        closeness += (path.path.size - 1)
      }
      closeness = BigDecimal(closeness / pathsThroughVertex.size.toDouble).round(new MathContext(3)).toDouble
    }
  }

  /**
   * Responsible to calculate the Betweenness centrality of this vertex
   * @param the list of all shortest paths in the graph
   */
  def calcBetweenness(shortestPathList: List[Path]) {
    val pathsThroughVertex = shortestPathList.filter(path => path.sourceVertexId != id && path.targetVertexId != id && path.path.contains(id))
    if (!pathsThroughVertex.isEmpty) {
      betweenness = BigDecimal(pathsThroughVertex.size.toDouble / shortestPathList.size.toDouble).round(new MathContext(3)).toDouble
    }
  }
}

/**
 * Represents an edge of a Path Collection graph, extends {@link com.signalcollect.DefaultEdge}
 * @param the traget vertex' id
 */
class PathCollectorEdge(t: Int) extends DefaultEdge(t) {
  var startingState = true
  type Source = PathCollectorVertex

  /**
   * The signal function sends an array of (shortest) paths to the target vertex
   */
  def signal = {
    var currentPathArray = ArrayBuffer[Path]()
    
    /*
     * Only the paths which don't have the target present are put into the array that is about to be sent
     * all paths are then extended by the target vertex they are sent to
     */
    for (path <- source.state.values) {
      if (!path.path.contains(t)) {
        val pathToAdd = new Path(path.sourceVertexId, t)
        pathToAdd.path = path.path.clone
        pathToAdd.path += t
        currentPathArray += pathToAdd
      }
    }
    
    /*
     * At the first signal step, the signaled array would be empty
     * For that reason, a path from the source vertex to the target vertex is added to the array
     */
    if (startingState) {
      currentPathArray += new Path(source.id, t)
    }
    startingState = false
    currentPathArray
  }
}

