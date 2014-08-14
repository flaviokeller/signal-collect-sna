package com.signalcollect.sna

import com.signalcollect.Graph
import scala.collection.mutable.ArrayBuffer
import com.signalcollect.Vertex
import com.signalcollect.DataGraphVertex

class GraphProperties(l: ArrayBuffer[Vertex[Any, _]]) {

  val pathInstance = new PathCollector
  var pathVertexArray = ArrayBuffer[Vertex[Any, _]]()
  def setPathVertexArray(pva: ArrayBuffer[Vertex[Any, _]]) = pathVertexArray = pva
  override def toString(): String = {
    "\nThe Properties of the graph are:\n\nSize: " + calcSize + "\nDensity: " + calcDensity + "\nDiameter: " + calcDiameter + "\nReciprocity: " + calcReciprocity + "\nDegree Distribution: " + "degreeDistribution" + "\n"
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
    edges / (nonAverageVertices.size * (nonAverageVertices.size - 1))
  }

  def calcDiameter(): Double = {
    if (pathVertexArray == null || pathVertexArray.isEmpty) {
      pathVertexArray = pathInstance.run
    }
    val listOfShortestPaths = pathInstance.allShortestPathsAsList
    def getdiameter(p1: Path, p2: Path): Path = if (p1.path.size > p2.path.size) p1 else p2
    listOfShortestPaths.reduceLeft(getdiameter).path.size
  }

  def calcReciprocity(): Double = {
    if (pathVertexArray == null || pathVertexArray.isEmpty) {
      pathVertexArray = pathInstance.run
    }
    val listOfShortestPaths = pathInstance.allShortestPathsAsList
    var numberOfReciprocalPaths = 0
    for (path <- listOfShortestPaths) {
      val reciprocalPathExists = !listOfShortestPaths.filter(p => p.sourceVertexId == path.targetVertexId && p.targetVertexId == path.sourceVertexId).isEmpty
      if (reciprocalPathExists) numberOfReciprocalPaths += 1
    }
    numberOfReciprocalPaths.toDouble / listOfShortestPaths.size.toDouble

  }
}