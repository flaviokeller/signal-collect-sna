package com.signalcollect.sna

import com.signalcollect.Graph
import scala.collection.mutable.ArrayBuffer
import com.signalcollect.Vertex

class GraphProperties(var size: Int, val density: Int, val diameter: Int, val reciprocity: Int, val degreeDistribution: Double) {

  override def toString(): String = {
    "The Properties of the graph are:\n\nSize: " + size + "\nDensity: " + density + "\nDiameter: " + diameter + "\nReciprocity: " + reciprocity + "\nDegree Distribution: " + degreeDistribution + "\n"
  }
  
  def calcSize(l: ArrayBuffer[Vertex[Any, _]]){
    val averageVertices = l.filter(v => v.getClass().toString().contains("Average"))
    size  = l.size - averageVertices.size
  }
}