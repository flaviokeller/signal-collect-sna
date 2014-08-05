package com.signalcollect.sna

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SynchronizedBuffer
import scala.util.control.Breaks
import com.signalcollect.AbstractVertex
import com.signalcollect.DataGraphVertex
import com.signalcollect.DefaultEdge
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.GraphBuilder
import com.signalcollect.Vertex
import com.signalcollect.configuration.ExecutionMode
import sun.awt.MostRecentKeyValue
import sun.awt.MostRecentKeyValue

object PathTester extends App {
  val graph = GraphBuilder.build
  val eg = new ExampleGraph
  eg.basePathTestGraph(graph)
  eg.extendPathTestGraph(graph)

  val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
  val stats = graph.execute(execmode)
  graph.awaitIdle
  var s = new ArrayBuffer[PathTestVertex] with SynchronizedBuffer[PathTestVertex]
  graph.foreachVertex(v => s.add(v.asInstanceOf[PathTestVertex]))
  for (d <- s) {
    println("Vertex: " + d.id + " size: " + d.allIncomingPaths.size + " incoming paths: " + d.allIncomingPaths)
  }
  graph.shutdown
}

class PathTestVertex(id: Int) extends DataGraphVertex(id, ArrayBuffer[Path]()) {
  type Signal = ArrayBuffer[Path]
  type State = ArrayBuffer[Path]
  var allIncomingPaths = ArrayBuffer[Path]()
  def collect: State = {
    try {
      var mostRecentPaths = new ArrayBuffer[Path]()
      for (x <- mostRecentSignalMap.values) {
        for (y <- x) {
          val isNonExistentPath = allIncomingPaths.filter(p => p.path.sameElements(y.path) /* && p.sourceVertexId == y.sourceVertexId && p.targetVertexId == y.targetVertexId*/ ).isEmpty
          if (isNonExistentPath) {
            val incomingPath = new Path(y.sourceVertexId, y.targetVertexId)
            incomingPath.path = y.path
            allIncomingPaths.add(incomingPath)
            mostRecentPaths.add(y)
          }
        }
      }
      mostRecentPaths
    } catch {
      case t: Throwable =>
        println("some error " + t.getCause + " " + t.getStackTraceString) // todo: handle error
        Breaks.break
    }
  }
}

class PathTestEdge(t: Int) extends DefaultEdge(t) {
  var startingState = true
  type Source = PathTestVertex
  def signal = {
    var currentPathArray = ArrayBuffer[Path]()

    for (path <- source.state) {
      if (!path.path.contains(t)) {
        val pathToAdd = new Path(path.sourceVertexId, t)
        pathToAdd.path = path.path.clone
        pathToAdd.path.add(t)
        path.incrementSize
        currentPathArray += pathToAdd
      }
    }
    if (startingState) {
      currentPathArray += new Path(source.id, t)
    }
    startingState = false
    currentPathArray
  }
}
