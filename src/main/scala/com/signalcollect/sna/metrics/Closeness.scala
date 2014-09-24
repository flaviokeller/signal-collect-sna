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

import com.signalcollect.Graph
import com.signalcollect.Vertex
import com.signalcollect.sna.ComputationResults
import com.signalcollect.sna.ExecutionResult
import com.signalcollect.sna.Path
import com.signalcollect.sna.PathCollector
import com.signalcollect.sna.PathCollectorVertex

object Closeness {

  def run(graph: Graph[Any, Any]): ExecutionResult = {
    val vertexArray = PathCollector.run(graph)
    val shortestPathList = PathCollector.allShortestPathsAsList(vertexArray.asInstanceOf[ArrayBuffer[PathCollectorVertex]])
    val closenessMap = getClosenessForAll(vertexArray, shortestPathList)
    new ExecutionResult(new ComputationResults(calcAvg(closenessMap), closenessMap), vertexArray)
  }

  def getClosenessForVertexId(shortestPathList: List[Path]): Double = {
    var closeness = 0.0
    for (path <- shortestPathList) {
      closeness += (path.path.size - 1)
    }
    BigDecimal(closeness / shortestPathList.size.toDouble).round(new MathContext(3)).toDouble
  }

  def getClosenessForAll(vertices: ArrayBuffer[Vertex[Any, _, Any, Any]], shortestPathList: List[Path]): java.util.Map[String, Object] = {
    var closenessMap = new java.util.TreeMap[String, Object]
    for (closenessVertex <- vertices) {
      val pathsThroughVertex = shortestPathList.filter(p => p.sourceVertexId == closenessVertex.id)
      val closeness = if (pathsThroughVertex.isEmpty) 0.0 else getClosenessForVertexId(pathsThroughVertex)
      closenessMap.put(closenessVertex.id.toString, closeness.asInstanceOf[Object])
    }
    closenessMap
  }

  def calcAvg(closenessMap: java.util.Map[String, Object]): Double = {
    val closenessValues = closenessMap.asScala.asInstanceOf[scala.collection.mutable.Map[String, Double]].values.toList
    BigDecimal(closenessValues.foldLeft(0.0)(_ + _) / closenessValues.foldLeft(0.0)((r, c) => r + 1)).round(new MathContext(3)).toDouble
  }
}
