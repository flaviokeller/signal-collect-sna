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

import scala.collection.JavaConverters.mapAsJavaMapConverter
import scala.util.Random
import com.signalcollect.DataGraphVertex
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.Graph
import com.signalcollect.Vertex
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.DefaultEdge

/**
 * Executes the Label Propagation Algorithm for Signal/Collect
 */
object LabelPropagation {

  /**
   * Function responsible for the execution of the algorithm
   * @param the parsed graph, instance of {@link com.signalcollect.Graph}
   * @param number of signal and collect steps, vertices and edges stop signalling and collecting after this number of steps
   * @return Map (key = step number, value = distribution of labels at this step (as a map))
   */
  def run(graph: Graph[Any, Any], signalSteps: Int): java.util.Map[Integer, java.util.Map[String, Integer]] = {
    type ResultType = Int

    /*
     * This vertex is responsible for keeping track of the label distribution in the graph at each step
     */
    val stepCountVertex = new CountLabelPropagationVertex("StepCounter", "StepCounter", signalSteps)
    graph.addVertex(stepCountVertex)
    graph.foreachVertex((v: Vertex[Any, _, Any, Any]) => graph.addEdge(v.id, new CountLabelPropagationEdge(stepCountVertex.id)))
    graph.foreachVertex((v: Vertex[Any, _, Any, Any]) => graph.addEdge(stepCountVertex.id, new CountLabelPropagationEdge(v.id)))
    val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
    val stats = graph.execute(execmode)
    graph.awaitIdle

    /*
     * This is the map stored by the stepCountVertex for keeping track of the label distribution at each step
     */
    val evolvingMap = graph.forVertexWithId(stepCountVertex.id, { v: CountLabelPropagationVertex => v.evolvingMap.toMap })

    graph.shutdown
    evolvingMap.asJava.asInstanceOf[java.util.Map[Integer, java.util.Map[String, Integer]]]
  }

}

/**
 * Represents a vertex of a Label Propagation graph, extends {@link com.signalcollect.DataGraphVertex}
 * @param the vertex' id
 */
class LabelPropagationVertex(id: Int, var label: String, val signalSteps: Int) extends DataGraphVertex(id, 1) {

  type Signal = String
  type State = Int
  var labelMap = scala.collection.mutable.Map[String, Int]()

  /**
   * The collect function filters out the label that occurs most from all incoming signals
   * This label will be signaled at all outgoing edges at the next signal step
   * If two or more labels tie for the most occurring, one among them is picked at random
   */
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

/**
 * Represents an edge of a Label Propagation graph, extends {@link com.signalcollect.DefaultEdge}
 * @param the traget vertex' id
 */
class LabelPropagationEdge(t: Any) extends DefaultEdge(t) {
  type Source = LabelPropagationVertex

  /**
   * The signal function passes the label of the source vertex to its target.
   */
  def signal = {
    source.label
  }
}

/**
 * Represents a vertex of a Label Propagation graph,
 * which is concerned with keeping track of how the labels are distributed in a graph.
 * Extends {@link com.signalcollect.DataGraphVertex}
 * @param the vertex' id
 */
class CountLabelPropagationVertex(id: String, var label: String, val signalSteps: Int) extends DataGraphVertex(id, 1) {

  type Signal = String
  type State = Int

  /*
   * This map keeps track of the distribution of labels in the whole graph at different numbers of steps
   */
  var evolvingMap = scala.collection.SortedMap[Int, java.util.Map[String, Int]]()

  /**
   * The collect function gathers the signals of all incoming edges and puts them into a map (key = label, value = nr. of occurrences)
   * This map is then added to the evolvingMap with its current signal step
   */
  def collect: State = {
    var labelMap = new java.util.HashMap[String, Int]()
    val relevantSignals = mostRecentSignalMap.filter(s => !s._1.equals("StepCounter")).values.toList
    for (x <- relevantSignals) {
      labelMap.put(x, labelMap.get(x) + 1)
    }
    evolvingMap += ((state, labelMap.asInstanceOf[java.util.Map[String, Int]]))
    state += 1
    if (state < signalSteps) {
      state
    } else {
      signalSteps
    }
  }
}

/**
 * Represents an edge of a Label Propagation graph,
 * which is concerned with keeping track of how the labels are distributed in a graph.
 * Extends {@link com.signalcollect.DefaultEdge}
 * @param the target vertex' id
 */
class CountLabelPropagationEdge(t: Any) extends DefaultEdge(t) {

  type Source = DataGraphVertex[Any, Any]

  /**
   * The signal function passes the label of the source vertex to its target,
   * in order to let the StepCount vertex see how the labels are distributed
   */
  def signal = {
    if (source.id.equals("StepCounter")) {
      None
    } else {
      source.asInstanceOf[LabelPropagationVertex].label
    }
  }
}