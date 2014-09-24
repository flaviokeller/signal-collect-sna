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

import scala.collection.immutable.Set
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SynchronizedBuffer
import com.signalcollect.DataGraphVertex
import com.signalcollect.DefaultEdge
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.sna.parser.ParserImplementor
import com.signalcollect.sna.constants.SNAClassNames
import com.signalcollect.Vertex

object LabelPropagation extends App {

  var graph = ParserImplementor.getGraph("/Users/flaviokeller/Desktop/examplegraph_separated.gml", SNAClassNames.LABELPROPAGATION)
  val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
  val stats = graph.execute(execmode)
  graph.awaitIdle
  var s = new ArrayBuffer[Vertex[Any, _,Any,Any]] with SynchronizedBuffer[Vertex[Any, _,Any,Any]]
  var labelDistribution = scala.collection.mutable.Map[String, Int]()
  graph.foreachVertex(v => s += v)
  for (v <- s) {
//    println("id: " + v.id)
    for (x <- v.asInstanceOf[LabelPropagationVertex].highestProportionLabels) {
//      println("label id: " + x._1 + "\tlabel count: " + x._2)
      labelDistribution.put(x._1, labelDistribution.get(x._1).getOrElse(0) + 1)
    }

  }
  println(labelDistribution)
  graph.shutdown

}

class LabelPropagationVertex(id: Int, val label: String) extends DataGraphVertex(id, 0) {

  type Signal = Map[String, Set[Int]]
  type State = Int
  var relevantLabels = Map((label, 1))
  var highestProportionLabels = Map((label, 1))
  var labelMap = scala.collection.mutable.Map((label, 1))
  var incomingpropagationLabels = scala.collection.mutable.Map[String, Set[Int]]()
  def collect: State = {
    for (signal <- mostRecentSignalMap) {
      for (signalLabelMap <- signal._2) {
        if (!signalLabelMap._1.equals("no label")) {
          var idSet = incomingpropagationLabels.get(signalLabelMap._1).getOrElse(signalLabelMap._2) ++ signalLabelMap._2
          incomingpropagationLabels += ((signalLabelMap._1, idSet))
          //          if (id == 7) println("END:\tid: " + id + " label " + x._1 + " labelset " + idSet)
        }
      }
    }
    for (propagationLabel <- incomingpropagationLabels) {
      labelMap.put(propagationLabel._1, propagationLabel._2.size)
      //      if (id == 7) println("resmap: " + resMap)
    }
    //    if (label.equals("no label")) {
    relevantLabels = (labelMap - ("no label")).toMap
//    println(relevantLabels)
    //    }
    if (!relevantLabels.isEmpty) {
      for (relevantlabel <- relevantLabels) {
        if (relevantlabel._2 >= highestProportionLabels.head._2) {
          highestProportionLabels += relevantlabel
        }
        if (relevantlabel._2 > highestProportionLabels.head._2) {
          highestProportionLabels = Map(relevantlabel)
        }
      }
      highestProportionLabels -= ("no label")
    }
    labelMap.size

  }
}
class LabelPropagationEdge(t: Any) extends DefaultEdge(t) {
  var sendMap = scala.collection.mutable.Map[String, Set[Int]]()

  type Source = LabelPropagationVertex
  def signal = {
    for (label <- source.labelMap) {
      sendMap.put(label._1, Set(source.id))
    }
    sendMap.toMap
  }
}

