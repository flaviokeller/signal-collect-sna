package com.signalcollect.sna

import java.lang.Double
import java.math.MathContext

import scala.collection.mutable.ArrayBuffer

import com.signalcollect.DataGraphVertex
import com.signalcollect.Graph
import com.signalcollect.Vertex
import com.signalcollect.sna.metrics.Degree
import com.sun.java.util.jar.pack.Histogram
import com.sun.java.util.jar.pack.Histogram

class GraphProperties(l: ArrayBuffer[Vertex[Any, _]], pathGraph: Graph[Any, Any]) {

  var size: Integer = null
  var density: Double = null
  var diameter: Double = null
  var reciprocity: Double = null

  var pathVertexArray = ArrayBuffer[Vertex[Any, _]]()

  def setPathVertexArray(pva: ArrayBuffer[Vertex[Any, _]]) = pathVertexArray = pva

  def calcProperties {
    size = calcSize
    density = calcDensity
    diameter = calcDiameter
    reciprocity = calcReciprocity
  }
  override def toString(): String = {
    "\nThe Properties of the graph are:\n\nSize:\t\t" + size + "\nDensity:\t" + density + "\nDiameter:\t" + diameter + "\nReciprocity:\t" + reciprocity + "\n"
  }

  def calcSize(): Int = {
    val averageVertices = l.filter(v => v.getClass().toString().contains("Average"))
    l.size - averageVertices.size
  }

  def calcDensity(): Double = {
    var edges = 0.0
    val nonAverageVertices = l.filter(v => !v.getClass().toString().contains("Average"))
    for (v <- nonAverageVertices) {

      val currentVertex: DataGraphVertex[Any, _] = v.asInstanceOf[DataGraphVertex[Any, _]]
      val currentOutgoingEdges = currentVertex.outgoingEdges.filter(e => !e._2.getClass().toString().contains("Average"))
      edges += currentOutgoingEdges.size
    }
    val density = edges / (nonAverageVertices.size * (nonAverageVertices.size - 1))
    BigDecimal(edges / (nonAverageVertices.size * (nonAverageVertices.size - 1))).round(new MathContext(3)).toDouble
  }

  def calcDiameter(): Double = {
    if (pathVertexArray == null || pathVertexArray.isEmpty) {
      pathVertexArray = PathCollector.run(pathGraph)
    }
    val listOfShortestPaths = PathCollector.allShortestPathsAsList
    def getdiameter(p1: Path, p2: Path): Path = if (p1.path.size > p2.path.size) p1 else p2
    listOfShortestPaths.reduceLeft(getdiameter).path.size
  }

  def calcReciprocity(): Double = {
    if (pathVertexArray == null || pathVertexArray.isEmpty) {
      pathVertexArray = PathCollector.run(pathGraph)
    }
    val mapOfShortestPathsForTargetVertices = PathCollector.allShortestPathsAsMap
    var numberOfReciprocalPaths = 0
    for (targetVertex <- mapOfShortestPathsForTargetVertices) {
      for (path <- targetVertex._2) {
        val reciprocalPathExists = !mapOfShortestPathsForTargetVertices.get(path.sourceVertexId).get.filter(p => p.sourceVertexId == path.targetVertexId && p.targetVertexId == path.sourceVertexId).isEmpty
        if (reciprocalPathExists) numberOfReciprocalPaths += 1
      }
    }
    numberOfReciprocalPaths.toDouble / mapOfShortestPathsForTargetVertices.size.toDouble

  }

}