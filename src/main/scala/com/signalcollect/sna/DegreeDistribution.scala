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
import com.signalcollect.AbstractVertex

class DegreeDistribution(fileName: String) {

  var degreeVertexArray = new ArrayBuffer[Vertex[Any, _,Any,Any]]()

  var degreeDistribution = new java.util.TreeMap[Integer, Integer]()

  def setVertexArray(dva: ArrayBuffer[Vertex[Any, _,Any,Any]]) = degreeVertexArray = dva
  override def toString(): String = "degree Distribution: " + degreeDistribution

  def gatherDegreeeDistribution(): java.util.TreeMap[Integer, Integer] = {

    if (degreeVertexArray == null || degreeVertexArray.isEmpty) {
      val degreeGraph = ParserImplementor.getGraph(fileName, SNAClassNames.DEGREE)
      degreeVertexArray = Degree.run(degreeGraph).vertexArray
    }
    val degreeDistrMap = new java.util.TreeMap[Integer, Integer]()
    for (v <- degreeVertexArray) {
      if (v.state.isInstanceOf[Int]) {
        if (degreeDistrMap.keySet().contains(v.state)) {
          degreeDistrMap.put(Integer.valueOf(v.state.toString), degreeDistrMap.get(v.state) + 1)
        } else {
          degreeDistrMap.put(Integer.valueOf(v.state.toString), 1)
        }
      }
    }
    degreeDistrMap
  }
}