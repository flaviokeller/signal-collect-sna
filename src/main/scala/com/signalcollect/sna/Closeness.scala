package com.signalcollect.sna

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SynchronizedBuffer

import com.signalcollect.DataGraphVertex
import com.signalcollect.DefaultEdge
import com.signalcollect.GraphBuilder

object Closeness extends App {

  var vertexArray = new ArrayBuffer[PathTestVertex] with SynchronizedBuffer[PathTestVertex]
  vertexArray = new ArrayBuffer[PathTestVertex] with SynchronizedBuffer[PathTestVertex]
  PathTester.run
  val shortestPathList = PathTester.allShortestPathsAsMap
  val closenessMap = getClosenessForAll(shortestPathList)
  for (c <- closenessMap) {
    println(c._1 + " " + c._2)
  }
  //
  //    val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
  //    val stats = graph.execute(execmode)
  //
  //    graph.awaitIdle
  //
  //    graph.foreachVertex(v => vertexArray.add(v))
  //    graph.shutdown

  // TODO: implement closeness just as computation and assign paths to vertices

  //TODO: desired implementation here

  def getClosenessForVertexId(id: Int, shortestPathList: List[Path]): Double = {

    val shortestPathsForId = shortestPathList.filter(p => p.sourceVertexId == id)
    var closeness = 0.0
    for (s <- shortestPathsForId) {
      closeness += s.path.size
    }
    //TODO: find out easy way to compute average (probably with foldleft or similarly)
    closeness / shortestPathsForId.size.toDouble
  }
  def getClosenessForAll(shortestPathList: Map[Int, List[Path]]): Map[Int, Double] = {
    var closenessMap = scala.collection.mutable.HashMap[Int, Double]()
    for (s <- shortestPathList) {
      if (!s._2.isEmpty) {
        val closeness = BigDecimal(getClosenessForVertexId(s._1, s._2)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
        closenessMap.put(s._1, closeness)
      } else { println("no outgoing paths for vertex " + s._1) }
    }
    closenessMap.toMap
  }
}
//      }
//    }
//    mostRecentPaths
//  }
//}
//
//class ClosenessEdge(t: Int) extends DefaultEdge(t) {
//  var startingState = true
//  type Source = ClosenessVertex
//  def signal = {
//    var currentPathArray = ArrayBuffer[Path]()
//
//    for (path <- source.state) {
//      if (!path.path.contains(t)) {
//        val pathToAdd = new Path(path.sourceVertexId, t)
//        pathToAdd.path = path.path.clone
//        pathToAdd.path.add(t)
//        path.incrementSize
//        currentPathArray += pathToAdd
//      }
//    }
//    if (startingState) {
//      currentPathArray += new Path(source.id, t)
//    }
//    startingState = false
//    currentPathArray
//  }
//}