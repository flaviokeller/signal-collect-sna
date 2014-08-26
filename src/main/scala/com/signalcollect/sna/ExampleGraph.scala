package com.signalcollect.sna

import com.signalcollect.Graph
import com.signalcollect.Vertex
import com.signalcollect.sna.metrics._


class ExampleGraph {

  final private var avgId = "Average"
  final private var avgDeg = new AverageDegreeVertex(avgId)
  final private var avgPR = new AveragePageRankVertex(avgId)
  final def initDegree() {
    avgId = "Average"
    avgDeg = new AverageDegreeVertex(avgId)
  }
  final def initPageRank() {
    avgId = "Average"
    avgPR = new AveragePageRankVertex(avgId)
  }

  def baseDegreeGraph(graph: Graph[Any, Any]) {
    graph.addVertex(new DegreeVertex(1))
    graph.addVertex(new DegreeVertex(2))
    graph.addVertex(new DegreeVertex(3))
    graph.addVertex(new DegreeVertex(4))
    graph.addVertex(new DegreeVertex(5))
    graph.addEdge(1, new DegreeEdge(4))
    graph.addEdge(1, new DegreeEdge(3))
    graph.addEdge(2, new DegreeEdge(1))
    graph.addEdge(2, new DegreeEdge(3))
    graph.addEdge(4, new DegreeEdge(1))
    graph.addEdge(4, new DegreeEdge(2))
    graph.addEdge(5, new DegreeEdge(2))
    graph.addEdge(5, new DegreeEdge(3))
  }
  def extendDegreeGraph(graph: Graph[Any, Any]) {
    graph.addVertex(new DegreeVertex(6))
    graph.addVertex(new DegreeVertex(7))
    graph.addVertex(new DegreeVertex(8))
    graph.addVertex(new DegreeVertex(9))
    graph.addVertex(new DegreeVertex(10))
    graph.addEdge(5, new DegreeEdge(6))
    graph.addEdge(5, new DegreeEdge(7))
    graph.addEdge(5, new DegreeEdge(8))
    graph.addEdge(5, new DegreeEdge(9))
    graph.addEdge(6, new DegreeEdge(7))
    graph.addEdge(6, new DegreeEdge(10))
    graph.addEdge(7, new DegreeEdge(10))
    graph.addEdge(8, new DegreeEdge(7))
    graph.addEdge(9, new DegreeEdge(6))
    graph.addEdge(10, new DegreeEdge(8))
  }
  def basePageRankGraph(graph: Graph[Any, Any]) {
	  graph.addVertex(new PageRankVertex(1))
	  graph.addVertex(new PageRankVertex(2))
	  graph.addVertex(new PageRankVertex(3))
	  graph.addVertex(new PageRankVertex(4))
	  graph.addVertex(new PageRankVertex(5))
	  graph.addEdge(1, new PageRankEdge(4))
	  graph.addEdge(1, new PageRankEdge(3))
	  graph.addEdge(2, new PageRankEdge(1))
	  graph.addEdge(2, new PageRankEdge(3))
	  graph.addEdge(4, new PageRankEdge(1))
	  graph.addEdge(4, new PageRankEdge(2))
	  graph.addEdge(5, new PageRankEdge(2))
	  graph.addEdge(5, new PageRankEdge(3))
  }
  def extendPageRankGraph(graph: Graph[Any, Any]) {
	  graph.addVertex(new PageRankVertex(6))
	  graph.addVertex(new PageRankVertex(7))
	  graph.addVertex(new PageRankVertex(8))
	  graph.addVertex(new PageRankVertex(9))
	  graph.addVertex(new PageRankVertex(10))
	  graph.addEdge(5, new PageRankEdge(6))
	  graph.addEdge(5, new PageRankEdge(7))
	  graph.addEdge(5, new PageRankEdge(8))
	  graph.addEdge(5, new PageRankEdge(9))
	  graph.addEdge(6, new PageRankEdge(7))
	  graph.addEdge(6, new PageRankEdge(10))
	  graph.addEdge(7, new PageRankEdge(10))
	  graph.addEdge(8, new PageRankEdge(7))
	  graph.addEdge(9, new PageRankEdge(6))
	  graph.addEdge(10, new PageRankEdge(8))
  }
  
  def baseLocalClusterCoefficientGraph(graph: Graph[Any, Any]) {
	  graph.addVertex(new LocalClusterCoefficientVertex(1))
	  graph.addVertex(new LocalClusterCoefficientVertex(2))
	  graph.addVertex(new LocalClusterCoefficientVertex(3))
	  graph.addVertex(new LocalClusterCoefficientVertex(4))
	  graph.addVertex(new LocalClusterCoefficientVertex(5))
	  graph.addEdge(1, new LocalClusterCoefficientEdge(4))
	  graph.addEdge(1, new LocalClusterCoefficientEdge(3))
	  graph.addEdge(2, new LocalClusterCoefficientEdge(1))
	  graph.addEdge(2, new LocalClusterCoefficientEdge(3))
	  graph.addEdge(4, new LocalClusterCoefficientEdge(1))
	  graph.addEdge(4, new LocalClusterCoefficientEdge(2))
	  graph.addEdge(5, new LocalClusterCoefficientEdge(2))
	  graph.addEdge(5, new LocalClusterCoefficientEdge(3))
  }
  def extendLocalClusterCoefficientGraph(graph: Graph[Any, Any]) {
	  graph.addVertex(new LocalClusterCoefficientVertex(6))
	  graph.addVertex(new LocalClusterCoefficientVertex(7))
	  graph.addVertex(new LocalClusterCoefficientVertex(8))
	  graph.addVertex(new LocalClusterCoefficientVertex(9))
	  graph.addVertex(new LocalClusterCoefficientVertex(10))
	  graph.addEdge(5, new LocalClusterCoefficientEdge(6))
	  graph.addEdge(5, new LocalClusterCoefficientEdge(7))
	  graph.addEdge(5, new LocalClusterCoefficientEdge(8))
	  graph.addEdge(5, new LocalClusterCoefficientEdge(9))
	  graph.addEdge(6, new LocalClusterCoefficientEdge(7))
	  graph.addEdge(6, new LocalClusterCoefficientEdge(10))
	  graph.addEdge(7, new LocalClusterCoefficientEdge(10))
	  graph.addEdge(8, new LocalClusterCoefficientEdge(7))
	  graph.addEdge(9, new LocalClusterCoefficientEdge(6))
	  graph.addEdge(10, new LocalClusterCoefficientEdge(8))
  }
  
  
  
//   def basePathTestGraph(graph: Graph[Any, Any]) {
//    graph.addVertex(new PathCollectorVertex(1))
//    graph.addVertex(new PathCollectorVertex(2))
//    graph.addVertex(new PathCollectorVertex(3))
//    graph.addVertex(new PathCollectorVertex(4))
//    graph.addVertex(new PathCollectorVertex(5))
//    Collectorh.addEdge(1, new PathTestEdge(4))
//    Collectorh.addEdge(1, new PathTestEdge(3))
//    Collectorh.addEdge(2, new PathTestEdge(1))
//    Collectorh.addEdge(2, new PathTestEdge(3))
//    Collectorh.addEdge(4, new PathTestEdge(1))
//    Collectorh.addEdge(4, new PathTestEdge(2))
//    Collectorh.addEdge(5, new PathTestEdge(2))
//    Collectorh.addEdge(5, new PathTestEdge(3))
//  }
//  def extendPathTestGraph(graph: Graph[Any, Any]) {
//    graph.addVertex(new PathCollectorVertex(6))
//    graph.addVertex(new PathCollectorVertex(7))
//    graph.addVertex(new PathCollectorVertex(8))
//    graph.addVertex(new PathCollectorVertex(9))
//    graph.addVertex(new PathCollectorectorVertex(10))
//    graph.addEdge(5, CollectorPathTestEdge(6))
//    graph.addEdge(5, CollectorPathTestEdge(7))
//    graph.addEdge(5, CollectorPathTestEdge(8))
//    graph.addEdge(5, CollectorPathTestEdge(9))
//    graph.addEdge(6, CollectorPathTestEdge(7))
//    graph.addEdge(6, nCollectorathTestEdge(10))
//    graph.addEdge(7, nCollectorathTestEdge(10))
//    graph.addEdge(8, CollectorPathTestEdge(7))
//    graph.addEdge(9, nCollectorathTestEdge(6))
//    graph.addEdge(10, new PathTestEdge(8))
//  }

  def setAverageDegreeVertex(graph: Graph[Any, Any]) {
    graph.addVertex(avgDeg)
    graph.foreachVertex((v: Vertex[Any, _]) => graph.addEdge(v.id, new AverageDegreeEdge(avgDeg.id)))
    graph.foreachVertex((v: Vertex[Any, _]) => graph.addEdge(avgDeg.id, new AverageDegreeEdge(v.id)))
  }

  def getAverageDegreeVertex(): AverageDegreeVertex = avgDeg
  
  def setAveragePageRankVertex(graph: Graph[Any, Any]) {
    graph.addVertex(avgPR)
    graph.foreachVertex((v: Vertex[Any, _]) => graph.addEdge(v.id, new AveragePageRankEdge(avgPR.id)))
    graph.foreachVertex((v: Vertex[Any, _]) => graph.addEdge(avgPR.id, new AveragePageRankEdge(v.id)))
  }

  def getAveragePageRankVertex(): AveragePageRankVertex = avgPR
}