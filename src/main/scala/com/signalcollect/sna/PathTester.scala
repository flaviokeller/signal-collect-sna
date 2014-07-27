package com.signalcollect.sna

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SynchronizedBuffer
import com.signalcollect.DataGraphVertex
import com.signalcollect.DefaultEdge
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.GraphBuilder
import com.signalcollect.Vertex
import com.signalcollect.configuration.ExecutionMode
import scala.util.control.Breaks
import com.signalcollect.AbstractVertex

object PathTester extends App {

  //  val eg = new ExampleGraph
  val graph = GraphBuilder.build
  graph.addVertex(new PathTestVertex(1))
  graph.addVertex(new PathTestVertex(2))
  graph.addVertex(new PathTestVertex(3))
  graph.addVertex(new PathTestVertex(4))
  graph.addVertex(new PathTestVertex(5))
  graph.addEdge(1, new PathTestEdge(4))
  graph.addEdge(1, new PathTestEdge(3))
  graph.addEdge(2, new PathTestEdge(1))
  graph.addEdge(2, new PathTestEdge(3))
  graph.addEdge(4, new PathTestEdge(1))
  graph.addEdge(4, new PathTestEdge(2))
  graph.addEdge(5, new PathTestEdge(2))
  graph.addEdge(5, new PathTestEdge(3))

  val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
  val stats = graph.execute(execmode)
  graph.awaitIdle
  var s = new ArrayBuffer[Vertex[Any, _]] with SynchronizedBuffer[Vertex[Any, _]]
  graph.foreachVertex(v => s.add(v))
  println(s)
  graph.shutdown
}

class PathTestVertex(id: Int) extends DataGraphVertex(id, Set[DataGraphVertex[Any,Any]]()) {
  type Signal = PathTestVertex
  type State = Set[DataGraphVertex[Any,Any]]
  var neighbours = mostRecentSignalMap.values.toList
  def collect: State = {
    try {
      var d = scala.collection.mutable.Set[DataGraphVertex[Any,Any]]()
      println(mostRecentSignalMap.values.toList + " ID: " + id)
      neighbours = mostRecentSignalMap.values.toList
//      d.addAll(neighbourIds)
      for (x <- mostRecentSignalMap) {
        println(x._2.edges + " ID: " + id)
        d.add(x._2)
      }
      d.toSet
    } catch {
      case t: Throwable =>
        println("some error " + t.getCause + " " +t.getStackTraceString) // todo: handle error
        Breaks.break
    }
  }
}

class PathTestEdge(t: Any) extends DefaultEdge(t) {
  type Source = PathTestVertex
  def signal = {
//    if (source.neighbours == null) {
//      List(id)
//    } else {
      source
//    }
  }
}
