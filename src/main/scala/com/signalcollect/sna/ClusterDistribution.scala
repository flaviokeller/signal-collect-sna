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

import scala.collection.JavaConverters.mapAsScalaMapConverter

import com.signalcollect.sna.constants.SNAClassNames
import com.signalcollect.sna.metrics.LocalClusterCoefficient
import com.signalcollect.sna.parser.ParserImplementor
/**
 * Class that creates a map of the distribution of the local cluster coefficient in a graph
 */
class ClusterDistribution(fileName: String) {

  /**
   * Map containing the vertex' ids and its clustering values
   * @note a map is used here because the LocalClusterCoefficient returns a vertexArray
   * whose vertices states are not the values but a map with neighbours
   */
  var clusterVertexMap = Map[String, Object]()

  /**
   * The clusterVertexMap may be set with this function
   * only possible if local cluster coefficient values are already calculated
   */
  def setClusterMap(cvm: java.util.Map[String, Object]) = clusterVertexMap = cvm.asScala.toMap

  /**
   * Function that returns the cluster distribution as a map (key = cluster coefficient value, value = number of occurrences)
   * @note The clusterVertexMap has to be created first, if was not yet set
   */
  def gatherClusterDistribution(): java.util.TreeMap[java.lang.Double, Integer] = {

    if (clusterVertexMap == null || clusterVertexMap.isEmpty) {
      val clusteringGraph = ParserImplementor.getGraph(fileName, SNAClassNames.LOCALCLUSTERCOEFFICIENT, None)
      clusterVertexMap = LocalClusterCoefficient.run(clusteringGraph).compRes.vertexMap.asScala.toMap
    }

    val clusteringMap = new java.util.TreeMap[java.lang.Double, Integer]()

    for (clusterVertex <- clusterVertexMap) {
      if (clusteringMap.keySet().contains(java.lang.Double.valueOf(clusterVertex._2.toString()))) {
        clusteringMap.put(clusterVertex._2.asInstanceOf[Double], clusteringMap.get(clusterVertex._2) + 1)
      } else {
        clusteringMap.put(clusterVertex._2.asInstanceOf[Double], 1)
      }
    }
    clusteringMap
  }
}