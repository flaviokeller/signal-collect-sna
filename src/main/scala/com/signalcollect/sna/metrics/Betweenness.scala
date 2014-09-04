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

import com.signalcollect.sna.ComputationResults
import com.signalcollect.sna.ExecutionResult
import com.signalcollect.sna.Path
import com.signalcollect.sna.PathCollector
import com.signalcollect.Graph
import scala.collection.JavaConverters._
import java.math.MathContext

object Betweenness {
  var vertexIds = Set[Int]()
  def run(graph: Graph[Any, Any]): ExecutionResult = {
    val vertexArray = PathCollector.run(graph)
    val shortestPathList = PathCollector.allShortestPathsAsList
    vertexIds = PathCollector.allShortestPathsAsMap.keySet
    val betweennessMap = getBetweennessForAll(shortestPathList)
    new ExecutionResult(new ComputationResults(calcAvg(betweennessMap), betweennessMap), vertexArray)
  }

  def getBetweennessForAll(shortestPathList: List[Path]): java.util.Map[String, Object] = {
    var betweennessMap = new java.util.TreeMap[String, Object]
    for (s <- vertexIds) {
      val pathsThroughVertex = shortestPathList.filter(p => p.sourceVertexId != s && p.targetVertexId != s && p.path.contains(s))
      val betweenness = BigDecimal(pathsThroughVertex.size.toDouble / shortestPathList.size.toDouble).round(new MathContext(3)).toDouble
      betweennessMap.put(s.toString, betweenness.asInstanceOf[Object])
    }
    betweennessMap
  }

  def calcAvg(betweennessMap: java.util.Map[String, Object]): Double = {
    val betweennessValues = betweennessMap.asScala.asInstanceOf[scala.collection.mutable.Map[String, Double]].values.toList
    BigDecimal(betweennessValues.foldLeft(0.0)(_ + _) / betweennessValues.foldLeft(0.0)((r, c) => r + 1)).round(new MathContext(3)).toDouble
  }
}