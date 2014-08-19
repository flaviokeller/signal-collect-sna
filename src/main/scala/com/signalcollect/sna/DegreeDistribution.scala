package com.signalcollect.sna

import com.signalcollect.Graph
import scala.collection.mutable.ArrayBuffer
import com.signalcollect.Vertex
import com.signalcollect.sna.metrics.Degree
import scala.collection.mutable.ArrayBuilder
import scala.collection.mutable.SynchronizedBuffer

class DegreeDistribution(degreeGraph: Graph[Any, Any]) {

  var degreeVertexArray = new ArrayBuffer[Vertex[Any, _]]()

  var degreeDistribution = new java.util.TreeMap[Integer, Integer]()

  def calcDistribution {
	  degreeDistribution = gatherDegreeeDistribution
  }

  def setVertexArray(dva: ArrayBuffer[Vertex[Any, _]]) = degreeVertexArray = dva
  override def toString(): String = "degree Distribution: " + degreeDistribution

  def gatherDegreeeDistribution(): java.util.TreeMap[Integer, Integer] = {
    if (degreeVertexArray == null || degreeVertexArray.isEmpty) {
      degreeVertexArray = Degree.run(degreeGraph).vertexArray
    }
    val degreeDistrMap = new java.util.TreeMap[Integer, Integer]()
    for (v <- degreeVertexArray) {
      if (v.state.isInstanceOf[Int]) {
        if (degreeDistrMap.keySet().contains(v.state)) {
          degreeDistrMap.put(Integer.valueOf(v.state.toString), degreeDistrMap.get(v.state) + 1)
        } else {
          degreeDistrMap.put(Integer.valueOf(v.state.toString), 1)
        }
      }
    }
    degreeDistrMap
  }
}