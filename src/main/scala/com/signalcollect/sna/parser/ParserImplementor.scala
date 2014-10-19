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

package com.signalcollect.sna.parser

import scala.io.Codec.string2codec
import scala.io.Source

import com.signalcollect.DefaultEdge
import com.signalcollect.GraphBuilder
import com.signalcollect.Vertex
import com.signalcollect.sna.PathCollectorEdge
import com.signalcollect.sna.PathCollectorVertex
import com.signalcollect.sna.constants.SNAClassNames
import com.signalcollect.sna.metrics.DegreeEdge
import com.signalcollect.sna.metrics.DegreeVertex
import com.signalcollect.sna.metrics.LabelPropagationEdge
import com.signalcollect.sna.metrics.LabelPropagationVertex
import com.signalcollect.sna.metrics.LocalClusterCoefficientEdge
import com.signalcollect.sna.metrics.LocalClusterCoefficientVertex
import com.signalcollect.sna.metrics.NeighborMajorityLabelPropagationEdge
import com.signalcollect.sna.metrics.NeighborMajorityLabelPropagationVertex
import com.signalcollect.sna.metrics.PageRankEdge
import com.signalcollect.sna.metrics.PageRankVertex
import com.signalcollect.sna.metrics.StepLabelPropagationEdge
import com.signalcollect.sna.metrics.StepLabelPropagationVertex
import com.signalcollect.sna.metrics.TransitivityEdge
import com.signalcollect.sna.metrics.TransitivityVertex

object ParserImplementor {

  def getGraph(fileName: String, className: SNAClassNames, signalSteps: Option[Integer]): com.signalcollect.Graph[Any, Any] = {
    val parser = new GmlParser
    try {
      val parsedGraphs: List[Graph] = parser.parse(Source.fromFile(fileName)("ISO8859_1")) //Kann auch ein File-Objekt sein
      val graph = GraphBuilder.build
      parsedGraphs foreach {
        case g: UndirectedGraph =>
          g.nodes.foreach({ n: Node =>
            className match {
              case SNAClassNames.STEPLABELPROPAGATION => graph.addVertex(new StepLabelPropagationVertex(n.id, n.id.toString, signalSteps.getOrElse(0).asInstanceOf[Int]))
              case _ => graph.addVertex(createVertex(n, className))
            }
          })
          g.edges.foreach({ e: Edge =>
            graph.addEdge(e.source, createEdge(e.target, className))
            g match {
              case ug: UndirectedGraph => //add edges (Gegenrichtung, falls undirected graph)
            }
          })
      }
      graph
    } catch {
//      graph.sh
      case p: ParseException => throw new IllegalArgumentException("Error when reading graph file " + fileName +": "+ p.getMessage())
    }
    //    graph.shutdown
  }

  def createVertex(node: Node, vertexClass: SNAClassNames): Vertex[Any, _, Any, Any] = {
    vertexClass match {
      case SNAClassNames.DEGREE => new DegreeVertex(node.id)
      case SNAClassNames.PAGERANK => new PageRankVertex(node.id)
      case SNAClassNames.PATH => new PathCollectorVertex(node.id)
      case SNAClassNames.BETWEENNESS => new PathCollectorVertex(node.id)
      case SNAClassNames.CLOSENESS => new PathCollectorVertex(node.id)
      case SNAClassNames.LOCALCLUSTERCOEFFICIENT => new LocalClusterCoefficientVertex(node.id)
      case SNAClassNames.TRANSITIVITY => new TransitivityVertex(node.id)
      case SNAClassNames.LABELPROPAGATION => new LabelPropagationVertex(node.id, node.label)
    }
  }
  def createEdge(targetId: Int, edgeClass: SNAClassNames): DefaultEdge[_] = {
    edgeClass match {
      case SNAClassNames.DEGREE => new DegreeEdge(targetId)
      case SNAClassNames.PAGERANK => new PageRankEdge(targetId)
      case SNAClassNames.PATH => new PathCollectorEdge(targetId)
      case SNAClassNames.BETWEENNESS => new PathCollectorEdge(targetId)
      case SNAClassNames.CLOSENESS => new PathCollectorEdge(targetId)
      case SNAClassNames.LOCALCLUSTERCOEFFICIENT => new LocalClusterCoefficientEdge(targetId)
      case SNAClassNames.TRANSITIVITY => new TransitivityEdge(targetId)
      case SNAClassNames.LABELPROPAGATION => new LabelPropagationEdge(targetId)
      case SNAClassNames.STEPLABELPROPAGATION => new StepLabelPropagationEdge(targetId)
    }
  }

}