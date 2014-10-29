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

package com.signalcollect.sna.gephiconnectors;

import java.util.Map;
import java.util.TreeMap;

import com.signalcollect.sna.ExecutionResult;
import com.signalcollect.sna.GraphProperties;
import com.signalcollect.sna.constants.SNAClassNames;
import com.signalcollect.sna.metrics.Transitivity;

public class TriadCensusSignalCollectGephiConnectorImpl extends
		SignalCollectGephiConnector {

	private ExecutionResult transitivityResult;
	private GraphProperties graphProps;

	public TriadCensusSignalCollectGephiConnectorImpl(String fileName) {
		super(fileName, SNAClassNames.TRIADCENSUS);
	}

	@Override
	public void executeGraph() {
		if (transitivityResult == null) {
			transitivityResult = Transitivity.run(getGraph());
		}
	}

	@Override
	public Map<String, Object> getAll() {
		if (transitivityResult == null) {
			executeGraph();
		}
		TreeMap<String, Object> result = new TreeMap<String, Object>(
				new NumbersThenWordsComparator());
		result.putAll(transitivityResult.compRes().vertexMap());
		return result;
	}

	@Override
	public double getAverage() {
		if (transitivityResult == null) {
			executeGraph();
		}
		return transitivityResult.compRes().average();
	}

	@Override
	public GraphProperties getGraphProperties() {
		if (transitivityResult == null) {
			executeGraph();
		}
		graphProps = new GraphProperties(transitivityResult.vertexArray(),
				getFileName());
		return graphProps;
	}

	// public static void main(String[] args) {
	// long startTime = System.currentTimeMillis();
	// SignalCollectGephiConnector a = new
	// TriadCensusSignalCollectGephiConnectorImpl(
	// "/Users/flaviokeller/Documents/Uni/Bachelorarbeit/Datasets/gml/polblogs.gml");
	// a.executeGraph();
	// double d = a.getAverage();
	// /*
	// * this map doesn't display the vertex values. Instead it represents the
	// * distribution of the triad types
	// */
	// Map<String, Object> l = a.getAll();
	// long intermediate = System.currentTimeMillis();
	// double intermediateTime = Double.valueOf(intermediate - startTime) /
	// 1000d;
	// System.out.println("execution time: " + intermediateTime + " seconds");
	//
	// GraphProperties p = a.getGraphProperties();
	// p.toString();
	// long intermediate2 = System.currentTimeMillis();
	// intermediateTime = Double.valueOf(intermediate2 - intermediate) / 1000d;
	// System.out.println("properties time: " + intermediateTime + " seconds");
	//
	// Map<Integer, Integer> dd = a.getDegreeDistribution();
	// Map<Double, Integer> cd = a.getClusterDistribution();
	//
	// long intermediate3 = System.currentTimeMillis();
	// intermediateTime = Double.valueOf(intermediate3 - startTime) / 1000d;
	// System.out.println("elapsed time until image creation: "
	// + intermediateTime + " seconds");
	//
	// try {
	// a.createDegreeDistributionImageFile(dd, "degreeDistr.png");
	// a.createClusterDistributionImageFile(cd, "clusterdistr.png");
	// long stopTime = System.currentTimeMillis();
	// double elapsedTime = Double.valueOf(stopTime - startTime) / 1000d;
	// System.out.println("full elapsed time: " + elapsedTime
	// + " seconds\n");
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// System.out.println("The triad census values are: " + l);
	// System.out.println(p);
	// System.out.println("The degree distribution is: " + dd);
	// System.out.println("The local cluster coefficient distribution is: "
	// + cd);
	// }

}
