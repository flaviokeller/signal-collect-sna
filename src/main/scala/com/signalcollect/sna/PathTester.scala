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

    val paths = new GraphPaths(graph)
    
    
//    val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
//    val stats = graph.execute(execmode)
//    graph.awaitIdle
    var s = new ArrayBuffer[DataGraphVertex[Any, _]] with SynchronizedBuffer[DataGraphVertex[Any, _]]
//    var t = new ArrayBuffer[Set[PathTestVertex]] with SynchronizedBuffer[Set[PathTestVertex]]
    graph.foreachVertex(v => s.add(v.asInstanceOf[DataGraphVertex[Any,_]]))
    paths.createNeighbourPaths(s)
    paths.createPathsByVertexId(4,scala.collection.mutable.Set[Int]())
    println(paths)
//    graph.foreachVertex(v => t.add(v.asInstanceOf[PathTestVertex].e.toSet))
//    println(s)
//    println(t)
    graph.shutdown
}

class PathTestVertex(id: Int) extends DataGraphVertex(id, Set[Int]()) {
  type Signal = PathTestVertex
  type State = Set[Int]
  var d = scala.collection.mutable.Set[Int]()
  var e = scala.collection.mutable.Set[PathTestVertex]()
  def collect: State = {
    try {
      for (x <- mostRecentSignalMap) {
        d.add(Integer.valueOf(x._1.toString))
        e.add(x._2)
      }
      println(e + " ID: " + id)
      d.toSet
    } catch {
      case t: Throwable =>
        println("some error " + t.getCause + " " + t.getStackTraceString) // todo: handle error
        Breaks.break
    }
  }
}

class PathTestEdge(t: Any) extends DefaultEdge(t) {
  type Source = PathTestVertex
  def signal = {
    source
  }
}
