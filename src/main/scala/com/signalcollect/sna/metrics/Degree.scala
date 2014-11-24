/*
 *  @author Flavio Keller
 *
 *  Copyright 2014 University of Zurich
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.signalcollect.sna.metrics

import java.math.MathContext
import scala.BigDecimal
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.SynchronizedBuffer
import com.signalcollect.DataGraphVertex
import com.signalcollect.ExecutionConfiguration
import com.signalcollect.Graph
import com.signalcollect.Vertex
import com.signalcollect.configuration.ExecutionMode
import com.signalcollect.sna.ComputationResults
import com.signalcollect.sna.ExecutionResult
import com.signalcollect.sna.constants.SignalCollectSNAConstants
import com.signalcollect.DefaultEdge

/**
 * Executes the calculation of the degree centrality values of a graph's vertices
 */
object Degree {

	/**
	 * Function responsible for the execution
	 * @param graph: the parsed graph, instance of {@link com.signalcollect.Graph}
	 * @return {@link com.signalcollect.sna.ExecutionResult} object
	 */
	def run(graph: Graph[Any, Any]): ExecutionResult = {

		/* This vertex is responsible for the calculation of the PageRank average of the graph */
		val avgVertex = new AverageDegreeVertex(SignalCollectSNAConstants.avgVertexId)

		graph.addVertex(avgVertex)
		graph.foreachVertex((v: Vertex[Any, _, Any, Any]) => graph.addEdge(v.id, new AverageDegreeEdge(avgVertex.id)))
		graph.foreachVertex((v: Vertex[Any, _, Any, Any]) => graph.addEdge(avgVertex.id, new AverageDegreeEdge(v.id)))
		val execmode = ExecutionConfiguration(ExecutionMode.Synchronous)
		val stats = graph.execute(execmode)
		graph.awaitIdle
		var vertexArray = new ArrayBuffer[Vertex[Any, _, Any, Any]] with SynchronizedBuffer[Vertex[Any, _, Any, Any]]
		graph.foreachVertex(v => vertexArray += v)
		graph.shutdown
		new ExecutionResult(new ComputationResults(BigDecimal(avgVertex.state).round(new MathContext(3)).toDouble, createTreeMap(vertexArray)), vertexArray, stats)
	}

	/**
	 * Function that creates an ordered Key-Value map out of the vertex array in order to have the degree values packaged in order
	 */
	def createTreeMap(vertexArray: ArrayBuffer[Vertex[Any, _, Any, Any]]): java.util.TreeMap[String, Object] = {
		var vertices = new java.util.TreeMap[String, Object]
		for (vertex <- vertexArray) {
			vertices.put(vertex.id.toString, vertex.state.asInstanceOf[Object])
		}
		vertices.remove(SignalCollectSNAConstants.avgVertexId)
		vertices
	}
}

/**
 * Represents a vertex of a Degree Centrality graph, extends {@link com.signalcollect.DataGraphVertex}
 * @param id: the vertex' id
 */
class DegreeVertex(id: Any) extends DataGraphVertex(id, 0) {

	type Signal = DataGraphVertex[Any, Any]
	type State = Int

	lazy val edgeSet = outgoingEdges.values.toSet

	/**
	 * The collect function calculates the vertex' degree centrality value according to its number of neighbours (incoming and outgoing)
	 */
	def collect: State = {
		val degreeEdges = edgeSet.filter(edge => edge.targetId.isInstanceOf[Integer])

		/* The incoming edge with an {@link com.signalcollect.sna.metrics.AverageDegreeVertex} as source is not relevant and therefore filtered out */
		val degreeSignals = mostRecentSignalMap.values.toList.filter(signal => !signal.getClass.toString().contains(SignalCollectSNAConstants.avgVertexId))
		degreeEdges.size + degreeSignals.size
	}

}

/**
 * Represents an edge of a Degree Centrality graph, extends {@link com.signalcollect.DefaultEdge}
 * @param t: the traget vertex' id
 */
class DegreeEdge(t: Any) extends DefaultEdge(t) {
	type Source = DataGraphVertex[Any, Any]

	/**
	 * The signal function passes the whole vertex object of the source vertex to its target,
	 * such that the target vertex is able to distinguish what type the source vertex has.
	 */
	def signal = source
}

/**
 * Represents a vertex of a Degree Centrality graph, which is concerned with calculating the average Degree centrality.
 * Extends {@link com.signalcollect.DataGraphVertex}
 * @param id: the vertex' id
 */
class AverageDegreeVertex(id: String) extends DataGraphVertex(id, 0.0) {

	type Signal = DataGraphVertex[Any, Any]
	type State = Double

	/**
	 * The collect function calculates the average degree centrality value.
	 * It takes the states of all incoming edges (except those with a {@link com.signalcollect.sna.metrics.AverageDegreeVertex} as source)
	 * and calculates the average out of them
	 */
	def collect: State = {

		/* The incoming edge with an {@link com.signalcollect.sna.metrics.AverageDegreeVertex} as source is not relevant and therefore filtered out */
		val degreeSignals = mostRecentSignalMap.filter(signal => !signal._1.equals(SignalCollectSNAConstants.avgVertexId)).values.toList
		var sum = 0
		for (signal <- degreeSignals) {
			sum += signal.state.asInstanceOf[Int]
		}
		BigDecimal(sum.toDouble / degreeSignals.size.toDouble).round(new MathContext(3)).toDouble
	}
}

/**
 * Represents an edge of a Degree Centrality graph, which is concerned with the calculation of the average Degree centrality.
 * Extends {@link com.signalcollect.DefaultEdge}
 * @param t: the target vertex' id
 */
class AverageDegreeEdge(t: Any) extends DefaultEdge(t) {
	type Source = DataGraphVertex[Any, Any]

	/**
	 * The signal function passes the whole vertex object of the source vertex to its target,
	 * such that the target vertex is able to distinguish what type the source vertex has.
	 */
	def signal = source
}
