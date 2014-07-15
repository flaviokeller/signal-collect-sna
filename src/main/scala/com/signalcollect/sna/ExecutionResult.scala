package com.signalcollect.sna

class ExecutionResult(var average: Double, var nodeDegreeList: java.util.Map[java.lang.String,java.lang.Integer]) {
  type VertexMap = java.util.Map[java.lang.String,java.lang.Integer]
  def setAverage(a: Double) = average = a
  def getAverage(): Double = average
  def setNodeDegreeList(l: VertexMap) = nodeDegreeList = l
  def getNodeDegreeList(): VertexMap = nodeDegreeList
}