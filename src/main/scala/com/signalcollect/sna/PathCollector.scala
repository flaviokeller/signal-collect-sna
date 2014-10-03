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

package com.signalcollect.sna

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SynchronizedBuffer
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.Graph
import com.signalcollect.GraphBuilder
import com.signalcollect.Vertex
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.DataGraphVertex
import com.signalcollect.DefaultEdge

object PathCollector {

  def run(pGraph: Graph[Any, Any]): ArrayBuffer[Vertex[Any, _, Any, Any]] = {

    var vertexArray = new ArrayBuffer[Vertex[Any, _, Any, Any]] with SynchronizedBuffer[Vertex[Any, _, Any, Any]]
    var graph: Graph[Any, Any] = null
    if (pGraph == null) {
      graph = GraphBuilder.build
    } else {
      graph = pGraph
    }
    val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
    val stats = graph.execute(execmode)
    graph.awaitIdle
    graph.foreachVertex(v => vertexArray += v.asInstanceOf[PathCollectorVertex])
    graph.shutdown
    vertexArray
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
}

class PathCollectorVertex(id: Int) extends DataGraphVertex(id, Map[Int, Path]()) {
  type Signal = ArrayBuffer[Path]
  type State = Map[Int, Path]
  var shortestPaths = scala.collection.mutable.Map[Int, Path]()
  def collect: State = {
    var mostRecentPaths = new ArrayBuffer[Path]()
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

