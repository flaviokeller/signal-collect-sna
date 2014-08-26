package com.signalcollect.sna.metrics

import java.math.MathContext
import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SynchronizedBuffer
import com.signalcollect.Graph
import com.signalcollect.sna.ComputationResults
import com.signalcollect.sna.ExecutionResult
import com.signalcollect.sna.Path
import com.signalcollect.sna.PathCollector
import com.signalcollect.sna.PathCollectorVertex

object Closeness {
  def run(graph: Graph[Any, Any]): ExecutionResult = {
    var vertexArray = new ArrayBuffer[PathCollectorVertex] with SynchronizedBuffer[PathCollectorVertex]

    vertexArray = new ArrayBuffer[PathCollectorVertex] with SynchronizedBuffer[PathCollectorVertex]
    val execRes = PathCollector.run(graph)
    val shortestPathMap = PathCollector.allShortestPathsAsMap
    val closenessMap = getClosenessForAll(shortestPathMap)
    val compres = new ComputationResults(calcAvg(closenessMap), closenessMap)
    new ExecutionResult(compres, execRes)
  }

  def getClosenessForVertexId(id: Int, shortestPathList: List[Path]): Double = {
    var closeness = 0.0
    for (s <- shortestPathList) {
      closeness += s.path.size
    }
    closeness / shortestPathList.size.toDouble
  }
  def getClosenessForAll(shortestPathList: Map[Int, List[Path]]): java.util.Map[String, Object] = {
    var closenessMap = new java.util.TreeMap[String, Object]
    for (s <- shortestPathList) {
      if (!s._2.isEmpty) {
        val closeness = BigDecimal(getClosenessForVertexId(s._1, s._2)).round(new MathContext(3)).toDouble
        closenessMap.put(s._1.toString, closeness.asInstanceOf[Object])
      }
    }
    closenessMap
  }

  def calcAvg(closenessMap: java.util.Map[String, Object]): Double = {
    val closenessValues = closenessMap.asScala.asInstanceOf[scala.collection.mutable.Map[String, Double]].values.toList
    BigDecimal(closenessValues.foldLeft(0.0)(_ + _) / closenessValues.foldLeft(0.0)((r, c) => r + 1)).round(new MathContext(3)).toDouble
  }
}
