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

package com.signalcollect.sna

import scala.collection.mutable.ArrayBuffer
import com.signalcollect.sna.constants.SNAClassNames
import com.signalcollect.sna.parser.ParserImplementor
import com.signalcollect.Vertex
import java.math.MathContext
import com.signalcollect.DataGraphVertex
import java.lang.Double
import com.signalcollect.sna.metrics.PathCollectorVertex
import com.signalcollect.sna.metrics.PathCollector
/**
 * Class that is responsible for calculating and storing the properties of a graph
 * It works by taking the resulting vertex array of a sna calculation
 * @note Sna calculation has to take place first in order to have an array of vertices to use
 *
 */
class GraphProperties(vertexArray: ArrayBuffer[Vertex[Any, _, Any, Any]], fileName: String) {

  /*
   * The four variables below are the properties which are implemented so far.
   * There may be more added.
   */
  var size: Integer = null
  var density: Double = null
  var diameter: Integer = null
  var reciprocity: Double = null

  /**
   * This array is used for the calcualtion of diameter and reciprocity.
   * Since diameter and reciprocity make use of shortest paths, these shortest paths have to be determined first
   * If the sna graph already worked with shortest paths, this array may be set with the @function setPathVertexArray
   */
  var pathVertexArray = ArrayBuffer[Vertex[Any, _, Any, Any]]()

  /**
   * function to set the pathVertexArray
   * @note the vertex can only be set, if the sna worked with paths (e.g. betweenness centrality or closeness centrality)
   */
  def setPathVertexArray(pva: ArrayBuffer[Vertex[Any, _, Any, Any]]) = pathVertexArray = pva

  override def toString(): String = {
    if (size == null) {
      size = calcSize
    }
    if (density == null) {
      density = calcDensity
    }
    if (diameter == null) {
      diameter = calcDiameter
    }
    if (reciprocity == null) {
      reciprocity = calcReciprocity
    }
    "\nThe Properties of the graph are:\n\nSize:\t\t" + size + "\nDensity:\t" + density + "\nDiameter:\t" + diameter + "\nReciprocity:\t" + reciprocity + "\n"
  }

  /**
   * function to calculate size of the current graph (which is represented by the vertexArray)
   */
  def calcSize(): Int = {
    /*
     * filtering out potential average vertices (which occur when calculating degree centrality or PageRank)
     */
    val averageVertices = vertexArray.filter(v => v.getClass().toString().contains("Average"))
    vertexArray.size - averageVertices.size
  }

  /**
   * function to calculate density of the current graph (which is represented by the vertexArray)
   */
  def calcDensity(): Double = {
    var edges = 0.0
    /*
     * filtering out potential average vertices (which occur when calculating degree centrality or PageRank)
     */
    val nonAverageVertices = vertexArray.filter(v => !v.getClass().toString().contains("Average"))
    for (v <- nonAverageVertices) {

      val currentVertex: DataGraphVertex[Any, _] = v.asInstanceOf[DataGraphVertex[Any, _]]
      val currentOutgoingEdges = currentVertex.outgoingEdges.filter(e => !e._2.getClass().toString().contains("Average"))
      edges += currentOutgoingEdges.size
    }
    val density = edges / (nonAverageVertices.size * (nonAverageVertices.size - 1))
    BigDecimal(edges / (nonAverageVertices.size * (nonAverageVertices.size - 1))).round(new MathContext(3)).toDouble
  }

  /**
   * function to calculate the diameter of the current graph
   * here, the pathVertexArray has to be used in order to calculate the property
   * if the pathVertexArray is not set yet, it has to be done first by running the PathCollector
   */
  def calcDiameter(): Int = {
    if (pathVertexArray == null || pathVertexArray.isEmpty) {
      val pathGraph = ParserImplementor.getGraph(fileName, SNAClassNames.PATH, None)
      pathVertexArray = PathCollector.run(pathGraph, SNAClassNames.PATH).vertexArray
    }
    val listOfShortestPaths = PathCollector.allShortestPathsAsList(pathVertexArray.asInstanceOf[ArrayBuffer[PathCollectorVertex]])
    def getdiameter(p1: Path, p2: Path): Path = if (p1.path.size > p2.path.size) p1 else p2
    listOfShortestPaths.reduceLeft(getdiameter).path.size - 1
  }

   /**
   * function to calculate the reciprocity of the current graph
   * here, the pathVertexArray has to be used in order to calculate the property
   * if the pathVertexArray is not set yet, it has to be done first by running the PathCollector
   */
  def calcReciprocity(): Double = {
    if (pathVertexArray == null || pathVertexArray.isEmpty) {
      val pathGraph = ParserImplementor.getGraph(fileName, SNAClassNames.PATH, None)
      pathVertexArray = PathCollector.run(pathGraph, SNAClassNames.PATH).vertexArray
    }
    val mapOfShortestPathsForTargetVertices = PathCollector.allShortestPathsAsMap(pathVertexArray.asInstanceOf[ArrayBuffer[PathCollectorVertex]])
    var numberOfReciprocalPaths = 0
    var size = 0
    for (targetVertex <- mapOfShortestPathsForTargetVertices.values) {
      size += targetVertex.size
      for (path <- targetVertex) {
        val reciprocalPathExists = !mapOfShortestPathsForTargetVertices.get(path.sourceVertexId).getOrElse(List()).filter(p => p.sourceVertexId == path.targetVertexId && p.targetVertexId == path.sourceVertexId).isEmpty
        if (reciprocalPathExists) numberOfReciprocalPaths += 1
      }
    }
    /*
     * needs to be divided by 2, since every path is counted twice
     */
    BigDecimal((numberOfReciprocalPaths / 2).toDouble / size).round(new MathContext(3)).toDouble
  }

}