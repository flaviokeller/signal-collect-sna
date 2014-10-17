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

import scala.collection.JavaConverters._
import scala.util.Random

import com.signalcollect.DataGraphVertex
import com.signalcollect.DefaultEdge
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.Graph
import com.signalcollect.Vertex
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.sna.constants.SNAClassNames
import com.signalcollect.sna.parser.ParserImplementor

object StepLabelPropagation{
//  var graph = ParserImplementor.getGraph("/Users/flaviokeller/Desktop/football.gml", SNAClassNames.STEPLABELPROPAGATION, Some(10))
  //  println(run(graph, 10))

  def run(graph: Graph[Any, Any], signalSteps: Int): java.util.Map[Integer, java.util.Map[String, Integer]] = {
    type ResultType = Int
    val stepCountVertex = new StepCountLabelPropagationVertex("StepCounter", "StepCounter", signalSteps)
    graph.addVertex(stepCountVertex)
    graph.foreachVertex((v: Vertex[Any, _, Any, Any]) => graph.addEdge(v.id, new StepCountLabelPropagationEdge(stepCountVertex.id)))
    graph.foreachVertex((v: Vertex[Any, _, Any, Any]) => graph.addEdge(stepCountVertex.id, new StepCountLabelPropagationEdge(v.id)))
    val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
    val stats = graph.execute(execmode)
    graph.awaitIdle
    val evolvingMap = graph.forVertexWithId(stepCountVertex.id, { v: StepCountLabelPropagationVertex => v.evolvingMap.toMap })
    val javaMap = new java.util.HashMap[Integer, java.util.HashMap[String, Integer]]()
//    for (y <- evolvingMap) {
//    	javaMap.put(y._1 , y._2.asJava)
//    }
    graph.shutdown
    evolvingMap.asJava.asInstanceOf[java.util.Map[Integer,java.util.Map[String,Integer]]]
//    javaMap

  }

}

class StepLabelPropagationVertex(id: Int, var label: String, val signalSteps: Int) extends DataGraphVertex(id, 0) {

  type Signal = String
  type State = Int
  var labelMap = scala.collection.mutable.Map[String, Int]()
  def collect: State = {
    labelMap.clear
    val relevantSignals = mostRecentSignalMap.filter(s => !s._1.equals("StepCounter")).values.toList
    for (x <- relevantSignals) {
      labelMap.put(x, labelMap.get(x).getOrElse(0) + 1)
    }
    if (!labelMap.isEmpty) {
      val groupedLabels = labelMap.groupBy(_._2)
      val highestCountLabels = groupedLabels.maxBy(_._1)._2
      label = highestCountLabels.keySet.toList(Random.nextInt(highestCountLabels.size))
    }
    state += 1
    if (state < signalSteps) {
      state
    } else {
      signalSteps
    }
  }
}
class StepLabelPropagationEdge(t: Any) extends DefaultEdge(t) {
  type Source = StepLabelPropagationVertex
  def signal = {
    source.label
  }
}

class StepCountLabelPropagationVertex(id: String, var label: String, val signalSteps: Int) extends DataGraphVertex(id, 0) {
  type Signal = String
  type State = Int
  var evolvingMap = scala.collection.SortedMap[Int, java.util.Map[String, Int]]()

  def collect: State = {
    var labelMap = new java.util.HashMap[String, Int]()
    val relevantSignals = mostRecentSignalMap.filter(s => !s._1.equals("StepCounter")).values.toList
    for (x <- relevantSignals) {
      labelMap.put(x, labelMap.get(x) + 1)
    }
    state += 1
    evolvingMap += ((state, labelMap.asInstanceOf[java.util.Map[String,Int]]))
    if (state < signalSteps) {
      state
    } else {
      signalSteps
    }
  }
}
class StepCountLabelPropagationEdge(t: Any) extends DefaultEdge(t) {
  type Source = DataGraphVertex[Any, Any]
  def signal = {
    if (source.id.equals("StepCounter")) {
      None
    } else {
      source.asInstanceOf[StepLabelPropagationVertex].label
    }
  }
}