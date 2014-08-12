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
 
  // TODO: implement closeness just as computation and assign paths to vertices


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