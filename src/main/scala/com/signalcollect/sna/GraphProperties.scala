package com.signalcollect.sna

import com.signalcollect.Graph
import scala.collection.mutable.ArrayBuffer
import com.signalcollect.Vertex
import com.signalcollect.DataGraphVertex
import java.math.MathContext

class GraphProperties(l: ArrayBuffer[Vertex[Any, _]], graph: Graph[Any, Any]) {

  var pathVertexArray = ArrayBuffer[Vertex[Any, _]]()
  def setPathVertexArray(pva: ArrayBuffer[Vertex[Any, _]]) = pathVertexArray = pva
  override def toString(): String = {
    "\nThe Properties of the graph are:\n\nSize:\t" + calcSize + "\nDensity:\t" + calcDensity + "\nDiameter:\t" + calcDiameter + "\nReciprocity:\t" + calcReciprocity + "\nDegree Distribution:\t" + "degreeDistribution" + "\n"
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
      pathVertexArray = PathCollector.run(graph)
    }
    val listOfShortestPaths = PathCollector.allShortestPathsAsList
    def getdiameter(p1: Path, p2: Path): Path = if (p1.path.size > p2.path.size) p1 else p2
    listOfShortestPaths.reduceLeft(getdiameter).path.size
  }

  def calcReciprocity(): Double = {
    if (pathVertexArray == null || pathVertexArray.isEmpty) {
      println("in if-statement")
      pathVertexArray = PathCollector.run(graph)
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