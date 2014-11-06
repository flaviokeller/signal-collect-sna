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

import com.signalcollect.Vertex
import com.signalcollect.sna.constants.SNAClassNames
import com.signalcollect.sna.metrics.Degree
import com.signalcollect.sna.parser.ParserImplementor

class DegreeDistribution(fileName: String) {

  /**
   * Array containing the vertex' objects
   */
  var degreeVertexArray = new ArrayBuffer[Vertex[Any, _, Any, Any]]()

  /**
   * The degreeVertexArray may be set with this function
   * only possible if local cluster coefficient values are already calculated
   */
  def setVertexArray(dva: ArrayBuffer[Vertex[Any, _, Any, Any]]) = degreeVertexArray = dva

  /**
   * Function that returns the degree distribution as a map (key = degree centrality value, value = number of occurrences)
   * @note The degreeVertexArray has to be created first, if was not yet set
   */
  def gatherDegreeeDistribution(): java.util.TreeMap[Integer, Integer] = {

    if (degreeVertexArray == null || degreeVertexArray.isEmpty) {
      val degreeGraph = ParserImplementor.getGraph(fileName, SNAClassNames.DEGREE, None)
      degreeVertexArray = Degree.run(degreeGraph).vertexArray
    }
    val degreeDistrMap = new java.util.TreeMap[Integer, Integer]()
    for (degreeVertex <- degreeVertexArray) {
      /*
       * the vertex' value must be of Integer (average vertex may occur)
       */
      if (degreeVertex.state.isInstanceOf[Int]) {
        if (degreeDistrMap.keySet().contains(degreeVertex.state)) {
          degreeDistrMap.put(degreeVertex.state.asInstanceOf[Int], degreeDistrMap.get(degreeVertex.state) + 1)
        } else {
          degreeDistrMap.put(degreeVertex.state.asInstanceOf[Int], 1)
        }
      }
    }
    degreeDistrMap
  }
}