package com.signalcollect.sna

class ExecutionResult(var average: Double, var nodeDegreeList: java.util.List[java.lang.Integer]) {
  type DegreeList = java.util.List[java.lang.Integer]
  def setAverage(a: Double) = average = a
  def getAverage(): Double = average
  def setNodeDegreeList(l: DegreeList) = nodeDegreeList = l
  def getNodeDegreeList(): DegreeList = nodeDegreeList
}