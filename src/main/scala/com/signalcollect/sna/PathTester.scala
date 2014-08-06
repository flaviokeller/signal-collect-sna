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
import java.lang.Throwable

/**
 * @author flaviokeller
 *
 */
object PathTester extends App {
  val graph = GraphBuilder.build
  var vertexArray = new ArrayBuffer[PathTestVertex] with SynchronizedBuffer[PathTestVertex]
  val eg = new ExampleGraph

  eg.basePathTestGraph(graph)
  eg.extendPathTestGraph(graph)

  val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
  val stats = graph.execute(execmode)

  graph.awaitIdle

  graph.foreachVertex(v => vertexArray.add(v.asInstanceOf[PathTestVertex]))
  graph.shutdown

  allShortestPathsAsMap
  allShortestPathsAsList

  /**
   * @param sourceVertexId
   * @param targetVertexId
   * @return
   */
  def getAllPaths(sourceVertexId: Int, targetVertexId: Int): List[Path] = {
    val targetVertex = vertexArray.filter(v => v.id.equals(targetVertexId))
    if (targetVertex.size != 1) throw new NoSuchElementException("The vertex with id " + targetVertexId + " doesn't exist or exists multiple times!")
    val actualVertex = targetVertex.get(0)
    val allPaths = actualVertex.allIncomingPaths.filter(p => p.sourceVertexId == sourceVertexId).toList
    if (allPaths.isEmpty)
      throw new NoSuchElementException("No Path exists between vertex " + sourceVertexId + " and vertex " + targetVertexId + "!")
    else allPaths
  }

  def getShortestPath(sourceVertexId: Int, targetVertexId: Int): Path = {
    def shortest(p1: Path, p2: Path): Path = if (p1.path.size < p2.path.size) p1 else p2
    val allPaths = getAllPaths(sourceVertexId, targetVertexId)
    val shortestpath = allPaths.reduceLeft(shortest)
    println(shortestpath)
    shortestpath
  }

  def allShortestPathsAsMap(): Map[Int, List[Path]] = {
    var shortestPathMap = scala.collection.mutable.Map[Int, List[Path]]()
    for (sourceVertex <- vertexArray) {
      //      println("\n----------------\n  Vertex id: " + sourceVertex.id + "\n  shortest Paths: \n----------------")
      var pathList = scala.collection.mutable.ListBuffer[Path]()
      for (targetVertex <- vertexArray.filter(v => !v.id.equals(sourceVertex.id))) {
        try {
          pathList.add(getShortestPath(sourceVertex.id, targetVertex.id))
        } catch {
          case noPath: NoSuchElementException => //do nothing
        }
      }
      shortestPathMap.put(sourceVertex.id, pathList.toList)
    }
    println()
    var mapsize = 0
    for (path <- shortestPathMap) {
      //      println(path._1 + "\t " + path._2)
      mapsize += path._2.size
    }
    println("Size: " + mapsize)
    shortestPathMap.toMap
  }
  def allShortestPathsAsList(): List[Path] = {
    var shortestPathList = scala.collection.mutable.ListBuffer[Path]()
    for (sourceVertex <- vertexArray) {
      for (targetVertex <- vertexArray.filter(v => !v.id.equals(sourceVertex.id))) {
        try {
          shortestPathList.add(getShortestPath(sourceVertex.id, targetVertex.id))
        } catch {
          case noPath: NoSuchElementException => //do nothing
        }
      }
    }
    println("Size: " + shortestPathList.size)
    //    for (path <- shortestPathList) {
    //      println(path)
    //    }
    shortestPathList.toList
  }

}

class PathTestVertex(id: Int) extends DataGraphVertex(id, ArrayBuffer[Path]()) {
  type Signal = ArrayBuffer[Path]
  type State = ArrayBuffer[Path]
  var allIncomingPaths = ArrayBuffer[Path]()
  def collect: State = {
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

