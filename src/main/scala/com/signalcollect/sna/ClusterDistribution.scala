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

import com.signalcollect.Graph
import scala.collection.mutable.ArrayBuffer
import com.signalcollect.Vertex
import com.signalcollect.sna.metrics.Degree
import scala.collection.mutable.ArrayBuilder
import scala.collection.mutable.SynchronizedBuffer
import com.signalcollect.GraphBuilder
import com.signalcollect.sna.parser.ParserImplementor
import com.signalcollect.sna.constants.SNAClassNames
import com.signalcollect.sna.metrics.LocalClusterCoefficient
import com.signalcollect.sna.metrics.LocalClusterCoefficientVertex
import scala.collection.JavaConverters._
class ClusterDistribution(fileName: String) {

  var clusterVertexMap = Map[String, Object]()

  var clusterDistribution = new java.util.TreeMap[Integer, Integer]()

  //  def setVertexArray(dva: ArrayBuffer[Vertex[Any, _]]) = clusterVertexMap = dva
  override def toString(): String = "cluster Distribution: " + clusterDistribution

  def gatherClusterDistribution(): java.util.TreeMap[Integer, Integer] = {

    if (clusterVertexMap == null || clusterVertexMap.isEmpty) {
      val clusteringGraph = ParserImplementor.getGraph(fileName, SNAClassNames.LOCALCLUSTERCOEFFICIENT)
      clusterVertexMap = LocalClusterCoefficient.run(clusteringGraph).compRes.vertexMap.asScala.toMap
    }
    println(clusterVertexMap)
    val clusteringMap = new java.util.TreeMap[Integer, Integer]()
    for (v <- clusterVertexMap) {
      //      if (v._1.isInstanceOf[Int]) {
      if (clusteringMap.keySet().contains(Integer.valueOf(v._1))) {
        clusteringMap.put(Integer.valueOf(v._1.toString), clusteringMap.get(v._2) + 1)
      } else {
        clusteringMap.put(Integer.valueOf(v._1.toString), 1)
      }
      //      }
    }
    clusteringMap
  }
}