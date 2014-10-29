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

object PathCollector {

  def run(graph: Graph[Any, Any], className: SNAClassNames): ExecutionResult = {

    var vertexArray = new ArrayBuffer[Vertex[Any, _, Any, Any]] with SynchronizedBuffer[Vertex[Any, _, Any, Any]]
    
    val execmode = ExecutionConfiguration(ExecutionMode.Synchronous).withTimeLimit(43200000)
    val stats = graph.execute(execmode)
    graph.awaitIdle
    graph.foreachVertex(v => vertexArray += v.asInstanceOf[PathCollectorVertex])
    if (className.equals(SNAClassNames.CLOSENESS)) {
      graph.foreachVertex(v => v.asInstanceOf[PathCollectorVertex].calcCloseness(allShortestPathsAsList(vertexArray.asInstanceOf[ArrayBuffer[PathCollectorVertex]])))
    } else if (className.equals(SNAClassNames.BETWEENNESS)) {
      graph.foreachVertex(v => v.asInstanceOf[PathCollectorVertex].calcBetweenness(allShortestPathsAsList(vertexArray.asInstanceOf[ArrayBuffer[PathCollectorVertex]])))
    }
    graph.shutdown

    var valueMap = new java.util.TreeMap[String, Object]
    var avg = 0.0
    if (className.equals(SNAClassNames.CLOSENESS)) {
      for (closenessVertex <- vertexArray) {
        valueMap.put(closenessVertex.id.toString, closenessVertex.asInstanceOf[PathCollectorVertex].closeness.asInstanceOf[Object])
      }
      avg = calcAvg(valueMap)
    } else if (className.equals(SNAClassNames.BETWEENNESS)) {
      for (closenessVertex <- vertexArray) {
        valueMap.put(closenessVertex.id.toString, closenessVertex.asInstanceOf[PathCollectorVertex].betweenness.asInstanceOf[Object])
      }
      avg = calcAvg(valueMap)
    }
    new ExecutionResult(new ComputationResults(avg, valueMap), vertexArray, stats)
  }

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

  def calcAvg(valueMap: java.util.Map[String, Object]): Double = {
    val values = valueMap.asScala.asInstanceOf[scala.collection.mutable.Map[String, Double]].values.toList
    BigDecimal(values.foldLeft(0.0)(_ + _) / values.foldLeft(0.0)((r, c) => r + 1)).round(new MathContext(3)).toDouble
  }
}

class PathCollectorVertex(id: Int) extends DataGraphVertex(id, Map[Int, Path]()) {
  type Signal = ArrayBuffer[Path]
  type State = Map[Int, Path]
  var shortestPaths = scala.collection.mutable.Map[Int, Path]()
  var closeness = 0.0
  var betweenness = 0.0
  var stepcount = 0
  def collect: State = {
    stepcount+=1
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

  def calcCloseness(shortestPathList: List[Path]) {
    val pathsThroughVertex = shortestPathList.filter(p => p.sourceVertexId == id)
    if (!pathsThroughVertex.isEmpty) {
      for (path <- pathsThroughVertex) {
        closeness += (path.path.size - 1)
      }
      closeness = BigDecimal(closeness / pathsThroughVertex.size.toDouble).round(new MathContext(3)).toDouble
    }

  }
  def calcBetweenness(shortestPathList: List[Path]) {
    val pathsThroughVertex = shortestPathList.filter(path => path.sourceVertexId != id && path.targetVertexId != id && path.path.contains(id))
    if (!pathsThroughVertex.isEmpty) {
      betweenness = BigDecimal(pathsThroughVertex.size.toDouble / shortestPathList.size.toDouble).round(new MathContext(3)).toDouble
    }
  }
}

class PathCollectorEdge(t: Int) extends DefaultEdge(t) {
  var startingState = true
  type Source = PathCollectorVertex
  def signal = {
    var currentPathArray = ArrayBuffer[Path]()
    for (path <- source.state.values) {
      if (!path.path.contains(t)) {
        val pathToAdd = new Path(path.sourceVertexId, t)
        pathToAdd.path = path.path.clone
        pathToAdd.path += t
        currentPathArray += pathToAdd
      }
    }
    if (startingState) {
      currentPathArray += new Path(source.id, t)
    }
    startingState = false
    currentPathArray
  }
}

