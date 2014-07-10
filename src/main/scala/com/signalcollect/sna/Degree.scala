package com.signalcollect.sna

import com.signalcollect.Graph
import com.signalcollect.GraphBuilder
import com.signalcollect.Vertex
import com.signalcollect.DataGraphVertex
import com.signalcollect.DefaultEdge
import com.signalcollect.interfaces.ComplexAggregation
import com.signalcollect.AbstractVertex
import com.sun.corba.se.spi.protocol.InitialServerRequestDispatcher
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.configuration.ExecutionMode

object Degree extends App {
  run()
  def run(){
  val graph = GraphBuilder.build
  graph.addVertex(new DegreeVertex(1))
  graph.addVertex(new DegreeVertex(2))
  graph.addVertex(new DegreeVertex(3))
  graph.addVertex(new DegreeVertex(4))
  graph.addVertex(new DegreeVertex(5))
  graph.addVertex(new DegreeVertex(6))
  graph.addEdge(1, new DegreeEdge(4))
  graph.addEdge(1, new DegreeEdge(3))
  graph.addEdge(2, new DegreeEdge(1))
  graph.addEdge(2, new DegreeEdge(3))
  graph.addEdge(4, new DegreeEdge(1))
  graph.addEdge(4, new DegreeEdge(2))
  graph.addEdge(5, new DegreeEdge(2))
  graph.addEdge(5, new DegreeEdge(3))

  val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
  val stats = graph.execute(execmode)
  graph.foreachVertex(println(_))
  
  
  average(graph)
  
  def average(g:Graph[Any,Any])={
    var y = 0
    
    g.foreachVertex((v:Vertex[Any,_])=>(y=y+(Integer.valueOf(v.state.toString))))
    println("average: " +y)
  }
  def getState(v:Vertex[Any,_]):Unit={
    v.state
  }
  
  graph.awaitIdle
  graph.shutdown
  println(stats)
  }
}
class AverageVertex (id:Any) extends DataGraphVertex(id,0){
  
  type State = Int
  def collect:State={
    1
  }
}
class DegreeVertex(id: Any) extends DataGraphVertex(id, 1) {

  type Signal = Int
  type State = Int
  lazy val neighbourIds = getTargetIdsOfOutgoingEdges.toSet

  def collect: State = {
    neighbourIds.size + signals.size
  }

}
class DegreeEdge(t: Any) extends DefaultEdge(t) {
  type Source = DegreeVertex
  def signal = source.neighbourIds
}