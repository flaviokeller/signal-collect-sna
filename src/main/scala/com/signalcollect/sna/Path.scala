package com.signalcollect.sna

import com.signalcollect.Vertex
import com.signalcollect.Edge
import com.signalcollect.AbstractVertex
import com.signalcollect.DataGraphVertex
import javax.lang.model.SourceVersion

class Path(sourceVertex: PathTestVertex, targetVertex: PathTestVertex) {

  var isShortestPath = false

  def createPath(sourceVertex: PathTestVertex, targetVertex: PathTestVertex): List[Int] = {
    
    val targetIsNeighbour = targetVertex.neighbours.contains(sourceVertex.id)
    if (targetIsNeighbour) {
      List(sourceVertex.id,targetVertex.id)
    } else {
//      for()
//      for(n<-targetVertex.neighbours){
//        createPath(n, sourceVertex)
//      }
      null
    }
  }
}