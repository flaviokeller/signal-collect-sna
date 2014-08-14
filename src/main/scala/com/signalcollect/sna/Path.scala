package com.signalcollect.sna

import com.signalcollect.Vertex
import scala.collection.mutable.LinkedList

class Path(val sourceVertexId: Int, val targetVertexId: Int) {
  var path = scala.collection.mutable.ArrayBuffer(sourceVertexId, targetVertexId)
  override def toString(): String = {
    "Path(Source Vertex: " + sourceVertexId + " Target Vertex: " + targetVertexId + "\tVertices on Path: " + path + /*"\tNumber of Nodes on Path: " + length +*/ ")"
  }
  
}