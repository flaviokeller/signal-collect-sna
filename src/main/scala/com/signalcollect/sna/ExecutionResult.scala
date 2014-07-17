package com.signalcollect.sna

class ExecutionResult(var average: Double, var vertexMap: java.util.Map[String, Object]) {
  type VertexMap = java.util.Map[String,Object]
  def setAverage(a: Double) = average = a
  def getAverage(): Double = average
  def setNodeMap(l: VertexMap) = vertexMap = l
  def getNodeMap(): VertexMap = vertexMap
}