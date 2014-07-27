package com.signalcollect.sna

import com.signalcollect.Graph
import scala.collection.mutable.ArrayBuffer
import com.signalcollect.Vertex
import com.signalcollect.DataGraphVertex

class GraphProperties(var size: Int, var density: Double, val diameter: Int, val reciprocity: Int, val degreeDistribution: Double) {

  override def toString(): String = {
    "The Properties of the graph are:\n\nSize: " + size + "\nDensity: " + density + "\nDiameter: " + diameter + "\nReciprocity: " + reciprocity + "\nDegree Distribution: " + degreeDistribution + "\n"
  }

  def calcSize(l: ArrayBuffer[Vertex[Any, _]]) {
    val averageVertices = l.filter(v => v.getClass().toString().contains("Average"))
    size = l.size - averageVertices.size
  }

  def calcDensity(l: ArrayBuffer[Vertex[Any, _]]) {
	var edges = 0.0
    val nonAverageVertices = l.filter(v => !v.getClass().toString().contains("Average"))
    for(v <- nonAverageVertices){
      
      val currentVertex:DataGraphVertex[Any,_] = v.asInstanceOf[DataGraphVertex[Any,_]]
      val currentOutgoingEdges = currentVertex.outgoingEdges.filter(e => !e._2.getClass().toString().contains("Average"))
      edges += currentOutgoingEdges.size
    }
    density=edges/(size*(size-1))
  }
}