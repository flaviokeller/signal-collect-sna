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

import scala.collection.mutable.ArrayBuffer
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.Vertex
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.sna.constants.SNAClassNames
import com.signalcollect.sna.parser.ParserImplementor
import com.signalcollect.DataGraphVertex
import com.signalcollect.DefaultEdge
import scala.collection.mutable.SynchronizedBuffer

object NeighborMajorityLabelPropagation extends App {

  var graph = ParserImplementor.getGraph("/Users/flaviokeller/Desktop/football.gml", SNAClassNames.NEIGHBORMAJORITYLABELPROPAGATION)
  val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
  val stats = graph.execute(execmode)
  graph.awaitIdle
  var s = new ArrayBuffer[Vertex[Any, _,Any,Any]] with SynchronizedBuffer[Vertex[Any, _,Any,Any]]
  var labelDistribution = scala.collection.mutable.Map[String, Int]()
  graph.foreachVertex(v => s += v)
  graph.foreachVertex(println(_))
  for (v <- s) {

  }
  println(labelDistribution)
  graph.shutdown

}

class NeighborMajorityLabelPropagationVertex(id: Int, var label: String) extends DataGraphVertex(id, label) {

  type Signal = String
  type State = String
  def collect: State = {
    var dampa = scala.collection.mutable.Map[String, Int]()
    if (id == 98) println("id: " + id + " " + mostRecentSignalMap)
    for (x <- mostRecentSignalMap) {
      dampa.put(x._2, dampa.get(x._2).getOrElse(0) + 1)
    }
    val x = dampa.maxBy(_._2)._1
    if(id == 98)println("id: " + id + " " + dampa)
    x
  }
}
class NeighborMajorityLabelPropagationEdge(t: Any) extends DefaultEdge(t) {
  type Source = NeighborMajorityLabelPropagationVertex
  def signal = {
    source.state
  }
}

