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
import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SynchronizedBuffer
import com.signalcollect.Graph
import com.signalcollect.sna.ComputationResults
import com.signalcollect.sna.ExecutionResult
import com.signalcollect.sna.Path
import com.signalcollect.sna.PathCollector
import com.signalcollect.sna.PathCollectorVertex

object Closeness {

  var vertexIds = Set[Int]()

  def run(graph: Graph[Any, Any]): ExecutionResult = {
    var vertexArray = new ArrayBuffer[PathCollectorVertex] with SynchronizedBuffer[PathCollectorVertex]

    vertexArray = new ArrayBuffer[PathCollectorVertex] with SynchronizedBuffer[PathCollectorVertex]
    val execRes = PathCollector.run(graph)
    val shortestPathMap = PathCollector.allShortestPathsAsMap
    val shortestPathList = PathCollector.allShortestPathsAsList
    vertexIds = PathCollector.allShortestPathsAsMap.keySet
    val closenessMap = getClosenessForAll(shortestPathMap)
    val daMap = getBetweennessForAll(shortestPathList, shortestPathMap)
    val compres = new ComputationResults(0.0, daMap)
    new ExecutionResult(compres, execRes)
  }

  def getClosenessForVertexId(id: Int, shortestPathList: List[Path]): Double = {
    var closeness = 0.0
    for (s <- shortestPathList) {
      closeness += (s.path.size-1)
    }
    closeness / shortestPathList.size.toDouble
  }
  def getClosenessForAll(shortestPathMap: Map[Int, List[Path]]): java.util.Map[String, Object] = {
    var closenessMap = new java.util.TreeMap[String, Object]
    var shortestPathMapReordered = new java.util.TreeMap[String, Object]
    for (s <- shortestPathMap) {
      if (!s._2.isEmpty) {
        val closeness = BigDecimal(getClosenessForVertexId(s._1, s._2)).round(new MathContext(3)).toDouble
        closenessMap.put(s._1.toString, closeness.asInstanceOf[Object])
      }
    }
    closenessMap
  }

  def getBetweennessForAll(shortestPathList: List[Path], shortestPathMap: Map[Int, List[Path]]): java.util.Map[String, Object] = {
    var betweennessMap = new java.util.TreeMap[String, Object]
    for (s <- vertexIds) {
      val pathsThroughVertex = shortestPathList.filter(p => p.sourceVertexId == s)
      val closeness = getClosenessForVertexId(s, pathsThroughVertex)
//      val betweenness = BigDecimal(closeness / pathsThroughVertex.size.toDouble).round(new MathContext(3)).toDouble
      betweennessMap.put(s.toString, closeness.asInstanceOf[Object])
    }
    betweennessMap
  }

  def calcAvg(closenessMap: java.util.Map[String, Object]): Double = {
    val closenessValues = closenessMap.asScala.asInstanceOf[scala.collection.mutable.Map[String, Double]].values.toList
    BigDecimal(closenessValues.foldLeft(0.0)(_ + _) / closenessValues.foldLeft(0.0)((r, c) => r + 1)).round(new MathContext(3)).toDouble
  }
}
