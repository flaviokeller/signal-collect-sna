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
import scala.collection.mutable.HashMap
import scala.collection.SortedMap
import scala.collection.mutable.SynchronizedBuffer
import scala.collection.mutable.SynchronizedMap

import com.signalcollect.DataGraphVertex
import com.signalcollect.DefaultEdge
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.Graph
import com.signalcollect.GraphBuilder
import com.signalcollect.Vertex
import com.signalcollect.configuration.ExecutionMode

object PathCollector {

  def run(pGraph: Graph[Any, Any]): ArrayBuffer[Vertex[Any, _]] = {

     var vertexArray = new ArrayBuffer[Vertex[Any, _]] with SynchronizedBuffer[Vertex[Any, _]]
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

  
  def allShortestPathsAsMap(vertexArray:ArrayBuffer[PathCollectorVertex]): Map[Int, List[Path]] = {
    var shortestPathMap = scala.collection.mutable.Map[Int, List[Path]]()
    for (targetVertex <- vertexArray) {
      var pathList = scala.collection.mutable.ListBuffer[Path]()
      try {
        pathList ++= targetVertex.shortestPaths.toList
      } catch {
        case noPath: NoSuchElementException => //do nothing
      }
      shortestPathMap.put(targetVertex.id, pathList.toList)
    }
    shortestPathMap.toMap
  }
  
 

 def allShortestPathsAsList(vertexArray:ArrayBuffer[PathCollectorVertex]): List[Path] = {
    var shortestPathList = scala.collection.mutable.ListBuffer[Path]()
    for (targetVertex <- vertexArray.asInstanceOf[ArrayBuffer[PathCollectorVertex]]) {
      try {
        shortestPathList ++= targetVertex.shortestPaths.toList
      } catch {
        case noPath: NoSuchElementException => //do nothing
      }
    }
    shortestPathList.toList
  }
}

class PathCollectorVertex(id: Int) extends DataGraphVertex(id, ArrayBuffer[Path]()) {
  type Signal = ArrayBuffer[Path]
  type State = ArrayBuffer[Path]
  var allIncomingPaths = ArrayBuffer[Path]()
  var shortestPaths = ArrayBuffer[Path]()
  def collect: State = {
    var mostRecentPaths = new ArrayBuffer[Path]()
    for (x <- mostRecentSignalMap.values) {
      for (y <- x) {
        val isNonExistentPath = allIncomingPaths.filter(p => p.path.sameElements(y.path) /* && p.sourceVertexId == y.sourceVertexId && p.targetVertexId == y.targetVertexId*/ ).isEmpty
        if (isNonExistentPath) {
          val existingShortestPath = shortestPaths.filter(p => p.sourceVertexId == y.sourceVertexId && p.targetVertexId == y.targetVertexId)
          val incomingPath = new Path(y.sourceVertexId, y.targetVertexId)
          incomingPath.path = y.path
          determineShortest(existingShortestPath, incomingPath)
          allIncomingPaths += incomingPath
          mostRecentPaths += y
        }
      }
    }
    mostRecentPaths
  }
  def determineShortest(p1: ArrayBuffer[Path], p2: Path) {
    if (p1.isEmpty) shortestPaths += p2
    else if (p1(0).path.size > p2.path.size) {
      shortestPaths -= p1(0)
      if (shortestPaths.filter(p => p.path.sameElements(p2.path)).isEmpty) {
        shortestPaths += p2
      }
    }
  }
}

class PathCollectorEdge(t: Int) extends DefaultEdge(t) {
  var startingState = true
  type Source = PathCollectorVertex
  def signal = {
    var currentPathArray = ArrayBuffer[Path]()

    for (path <- source.state) {
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

