package com.signalcollect.sna.metrics

import java.math.MathContext
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SynchronizedBuffer
import com.signalcollect.DataGraphVertex
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.Graph
import com.signalcollect.Vertex
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.sna.ComputationResults
import com.signalcollect.sna.ExecutionResult
import com.signalcollect.DefaultEdge
import com.signalcollect.sna.NodeTriad
import com.signalcollect.sna.TriadType
import com.signalcollect.sna.gephiconnectors.SNAClassNames
import com.signalcollect.sna.parser.ParserImplementor
import com.signalcollect.GraphBuilder
import scala.collection.SortedMap

object Transitivity extends App {
	final val codeToType = List(1, 2, 2, 3, 2, 4, 6, 8, 2, 6, 5, 7, 3, 8,
			7, 11, 2, 6, 4, 8, 5, 9, 9, 13, 6, 10, 9, 14, 7, 14, 12, 15, 2, 5,
			6, 7, 6, 9, 10, 14, 4, 9, 9, 12, 8, 13, 14, 15, 3, 7, 8, 11, 7, 12,
			14, 15, 8, 14, 13, 15, 11, 15, 15, 16)
  run(ParserImplementor.getGraph("/Users/flaviokeller/Desktop/power.gml",
    SNAClassNames.TRANSITIVITY))
  def run(graph: Graph[Any, Any]): ExecutionResult = {
    val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
    val stats = graph.execute(execmode)
    graph.awaitIdle

    var s = new ArrayBuffer[Vertex[Any, _]] with SynchronizedBuffer[Vertex[Any, _]]
    graph.foreachVertex(v => s += v)
    var vertexMap = SortedMap[Int, TransitivityVertex]()
    for (v <- s) {
      vertexMap += ((Integer.valueOf(v.id.toString), v.asInstanceOf[TransitivityVertex]))
    }
    graph.shutdown
    var treeMap = new java.util.TreeMap[String, Object]()
        var countMap = SortedMap[Int, Int]()

    for (d <- vertexMap.toMap) {
      if (d._2.neighbours.isEmpty) {
        for (o <- d._2.outgoingEdges) {
          d._2.neighbours += Integer.valueOf(o._1.toString)
        }
      }
      println(d._1 + ", " + d._2.neighbours)
      var countList = scala.collection.mutable.LinkedList[Int]()
      
      countMap++=getCounts(d._2, vertexMap.toMap)
      
    }
    println(countMap)

    //    new ExecutionResult(new ComputationResults(BigDecimal(averageclcoeff).round(new MathContext(3)).toDouble, treeMap), s)
    null
  }
 
  def getCounts(vertex: TransitivityVertex, vertexMap: Map[Int, TransitivityVertex]): Map[Int,Int] = {
    var countMap = SortedMap[Int, Int]()
    for (neighbour <- vertex.neighbours) { //TODO maybe use neighbourset instead of triadset (refer to algorithm)
      if (Integer.valueOf(vertex.id.toString) < neighbour) {
        var triadType = -1;
        val neighbourVertex = vertexMap.get(neighbour).get

        //if(vertex.id <= iteration number (as if iterating from i = 0 until i < vertexmap.size)
        //==> does this even make sense

        val neighboursOfBothVertices = vertex.neighbours union neighbourVertex.neighbours //common neighbours?

        if (vertex.outgoingEdges.contains(neighbourVertex.id) && neighbourVertex.outgoingEdges.contains(vertex.id)) {
          triadType = 3;
        } else {
          triadType = 2;
        }
        var countValue = countMap.get(triadType).getOrElse(0)
        countMap += ((triadType, countValue + (vertexMap.size - vertex.outgoingEdges.size - 2)))

        for (neighbourOfBoth <- neighboursOfBothVertices) {
          if (neighbour < neighbourOfBoth || (Integer.valueOf(vertex.id.toString) < neighbourOfBoth && neighbourOfBoth < neighbour && !vertex.neighbours.contains(neighbourOfBoth))) {
            val neighbourOfBothVertex = vertexMap.get(neighbourOfBoth).get

            triadType = codeToType(triCode(vertex, neighbourVertex, neighbourOfBothVertex))
            countValue = countMap.get(triadType).getOrElse(0)
            countMap += ((triadType, countValue + 1))
          }

        }
      }
    }
    var sum = 0
    for (i <- 2 to 16) {
      sum+= countMap.get(i).getOrElse(0)
    }
    countMap += ((1,((vertexMap.size*(vertexMap.size-1)*(vertexMap.size-2))/6)-sum))
    countMap.toMap
  }

  def triCode(u: TransitivityVertex, v: TransitivityVertex, w: TransitivityVertex): Int = {
    var i = 0
    if (link(v, u)) i += 1
    if (link(u, v)) i += 2
    if (link(v, w)) i += 4
    if (link(w, v)) i += 8
    if (link(u, w)) i += 16
    if (link(w, u)) i += 32
    i
  }

  def link(u: TransitivityVertex, v: TransitivityVertex): Boolean = {
    u.outgoingEdges.contains(v.id)
  }

  def triType(triCode: Int): Int = {
    codeToType(triCode)
  }

  
  /**
   * keeping this function for now as backup
   */
  
   def determineTriadTypes(vertex: TransitivityVertex, vertexMap: Map[Int, TransitivityVertex]): Double = {
    //TODO find out how to determine type 1 triads
    //      val nrOfVerticesNotConnectedToVertex = vertexMap.keySet.filter(v => !vertex.outgoingEdges.contains(v.intValue)).size - vertex.triadSet.size
    //      val nrOfTypeoneTriads = ((nrOfVerticesNotConnectedToVertex-1) * (nrOfVerticesNotConnectedToVertex - 2))/2
    if (vertex.state != 0) {

      for (triad <- vertex.triadSet) {
        val headVertex = vertexMap.get(triad.headId).get
        val tailVertex = vertexMap.get(triad.tailId).get
        if (headVertex.outgoingEdges.contains(triad.tailId) && tailVertex.outgoingEdges.contains(triad.headId)) {
          triad.triadType = TriadType.typesixteen
        } else if (headVertex.outgoingEdges.contains(triad.tailId)) {
          triad.triadType = TriadType.typenine
          //          println("id: " + vertex.id + "\ttriad: " + triad)
        } else if (tailVertex.outgoingEdges.contains(triad.headId)) {
          triad.triadType = TriadType.typeten
        }
      }
    }
    0.0
  }

}
class TransitivityVertex(id: Any) extends DataGraphVertex(id, 0) {
  type Signal = Int
  type State = Int
  var neighbours = scala.collection.mutable.Set[Int]()
  var triadSet = scala.collection.mutable.Set[NodeTriad]()
  var triadMap = scala.collection.mutable.Map[String, Set[NodeTriad]]()
  def collect: State = {
    for (incomingneighbour <- mostRecentSignalMap) {
      neighbours += Integer.valueOf(incomingneighbour._1.toString)
      for (outgoingneighbour <- outgoingEdges) {
        neighbours += Integer.valueOf(outgoingneighbour._1.toString)
        val triadNotPresent = triadSet.filter(t => t.headId == Integer.valueOf(incomingneighbour._1.toString) && t.centerId == Integer.valueOf(id.toString) && t.tailId == Integer.valueOf(outgoingneighbour._1.toString)).isEmpty
        if (triadNotPresent) {
          val triad = new NodeTriad(Integer.valueOf(incomingneighbour._1.toString), Integer.valueOf(id.toString), Integer.valueOf(outgoingneighbour._1.toString))
          triadSet.add(triad)

        }
      }
    }
    triadSet.size
  }

}
class TransitivityEdge(t: Int) extends DefaultEdge(t) {
  type Source = DataGraphVertex[Any, Any]
  def signal = source.id
}
