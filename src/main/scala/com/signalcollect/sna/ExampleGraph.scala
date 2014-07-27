package com.signalcollect.sna

import com.signalcollect.Graph
import com.signalcollect.DataGraphVertex
import com.signalcollect.Vertex

class ExampleGraph {

  final private var avgId = 'a'
  final private var avgDeg = new AverageDegreeVertex(avgId)
  final private var avgPR = new AveragePageRankVertex(avgId)
  final def initDegree() {
    avgId = 'a'
    avgDeg = new AverageDegreeVertex(avgId)
  }
  final def initPageRank() {
    avgId = 'a'
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