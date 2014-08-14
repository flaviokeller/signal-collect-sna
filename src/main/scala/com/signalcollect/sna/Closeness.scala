package com.signalcollect.sna

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SynchronizedBuffer

import com.signalcollect.DataGraphVertex
import com.signalcollect.DefaultEdge
import com.signalcollect.GraphBuilder

object Closeness /*extends App*/ {
  def run: ExecutionResult = {
    var vertexArray = new ArrayBuffer[PathTestVertex] with SynchronizedBuffer[PathTestVertex]

    vertexArray = new ArrayBuffer[PathTestVertex] with SynchronizedBuffer[PathTestVertex]
    val execRes = PathTester.run
    val shortestPathList = PathTester.allShortestPathsAsMap
    val closenessMap = getClosenessForAll(shortestPathList)
    val compres = new ComputationResults(0.0, closenessMap)
    new ExecutionResult(compres, execRes)
  }
  // TODO: implement closeness just as computation and assign paths to vertices

  def getClosenessForVertexId(id: Int, shortestPathList: List[Path]): Double = {
    var closeness = 0.0
    for (s <- shortestPathList) {
      closeness += s.path.size
    }
    //TODO: find out easy way to compute average (probably with foldleft or similarly)
    closeness / shortestPathList.size.toDouble
  }
  def getClosenessForAll(shortestPathList: Map[Int, List[Path]]): java.util.Map[String, Object] = {
    var closenessMap = new java.util.TreeMap[String, Object]
    for (s <- shortestPathList) {
      if (!s._2.isEmpty) {
        val closeness = BigDecimal(getClosenessForVertexId(s._1, s._2)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble
        closenessMap.put(s._1.toString, closeness.asInstanceOf[Object])
      } else { println("no outgoing paths for vertex " + s._1) }
    }
    closenessMap
  }
}
