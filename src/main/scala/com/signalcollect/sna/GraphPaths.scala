package com.signalcollect.sna

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SynchronizedBuffer
import com.signalcollect.AbstractVertex
import com.signalcollect.DataGraphVertex
import com.signalcollect.Edge
import com.signalcollect.Graph
import com.signalcollect.Vertex

class GraphPaths(val graph: Graph[Any, Any]) {

  var neighbourPaths = scala.collection.mutable.Map[Int, Set[Path]]()

  def createNeighbourPaths(vertices: ArrayBuffer[DataGraphVertex[Any, _]]): Map[Int, Set[Path]] = {

    for (v <- vertices) {
      var pathSet = scala.collection.mutable.Set[Path]()
      //      createPathsByVertexId(Integer.valueOf(v.id.toString))
      for (t <- v.targetIds) {
        pathSet.add(new Path(Integer.valueOf(v.id.toString), Integer.valueOf(t.toString)))
      }
      neighbourPaths.put(Integer.valueOf(v.id.toString), pathSet.toSet)
    }

    neighbourPaths.toMap
  }

  def createPathsByVertexId(vertex: Int, passedVertices: scala.collection.mutable.Set[Int]): Set[Path] = {
    passedVertices.add(vertex)
    var paths = scala.collection.mutable.Set[Path]()
    val pathsToNeighbours = neighbourPaths.get(vertex).get
    paths = collection.mutable.Set(pathsToNeighbours.toSeq: _*)
    for (neighbourPath <- pathsToNeighbours) { //iterating through paths to neighbours
      val neighbourPathsOfNeighbour = neighbourPaths.get(neighbourPath.targetVertexId).get

      def furtherPath(vertex: Int, verticesOnPath: Set[Int], nPaths: Set[Path]): Unit = {
        passedVertices.add(verticesOnPath.last)
        println("Passed vertices: " + passedVertices)
        for (x <- nPaths) {
          if (!passedVertices.contains(x.targetVertexId) && !passedVertices.contains(x.sourceVertexId)) {
            val datpath = new Path(vertex, x.targetVertexId)
            for (pv <- verticesOnPath) {
              datpath.path += (pv)
              datpath.incrementSize
            }
            datpath.path += (x.sourceVertexId)
            datpath.incrementSize
            paths.add(datpath)
            furtherPath(vertex, verticesOnPath + (x.targetVertexId), neighbourPaths.get(x.targetVertexId).get)
          }
        }
      }
      for (t <- neighbourPathsOfNeighbour) {
        if (!passedVertices.contains(t.targetVertexId) /*&& !passedVertices.contains(x.sourceVertexId)*/ ) {
          val datpath = new Path(vertex, t.targetVertexId)
          datpath.path += (t.sourceVertexId)
          datpath.incrementSize
          paths.add(datpath)
        }
        furtherPath(vertex, Set(t.sourceVertexId), neighbourPaths.get(t.targetVertexId).get)
      }
    }
    neighbourPaths.put(vertex, paths.toSet)
    paths.toSet
  }

  override def toString(): String = {
    var res = "The resulting paths are: \n"
    for (p <- neighbourPaths) {
      res += "Vertex ID: " + p._1 + "\n"
      for (v <- p._2) {
        res += v + "\n"
      }
    }
    res
  }
}