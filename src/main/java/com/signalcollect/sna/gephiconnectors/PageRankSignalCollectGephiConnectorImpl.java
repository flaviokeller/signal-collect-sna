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
import com.signalcollect.sna.metrics.PageRank;

public class PageRankSignalCollectGephiConnectorImpl extends
		SignalCollectGephiConnector {

	private ExecutionResult pageRankResult;
	private GraphProperties graphProps;

	public PageRankSignalCollectGephiConnectorImpl(String fileName) {
		super(fileName, SNAClassNames.PAGERANK);
	}

	@Override
	public void executeGraph() {
		if (pageRankResult == null) {
			pageRankResult = PageRank.run(getGraph());
		}
	}

	@Override
	public double getAverage() {
		if (pageRankResult == null) {
			executeGraph();
		}
		return pageRankResult.compRes().average();
	}

	@Override
	public Map<String, Object> getAll() {
		if (pageRankResult == null) {
			executeGraph();
		}
		TreeMap<String, Object> result = new TreeMap<String, Object>(
				new NumbersThenWordsComparator());
		result.putAll(pageRankResult.compRes().vertexMap());
		return result;

	}

	@Override
	public GraphProperties getGraphProperties() {
		if (pageRankResult == null) {
			executeGraph();
		}
		graphProps = new GraphProperties(pageRankResult.vertexArray(),
				getFileName());
		return graphProps;
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		SignalCollectGephiConnector a = new PageRankSignalCollectGephiConnectorImpl(
				"/Users/flaviokeller/Desktop/examplegraph.gml");
		a.executeGraph();
		double d = a.getAverage();
		Map<String, Object> l = a.getAll();
		long intermediate = System.currentTimeMillis();
		double intermediateTime = Double.valueOf(intermediate - startTime) / 1000d;
		System.out.println("execution time: " + intermediateTime + " seconds");

		GraphProperties p = a.getGraphProperties();
		p.toString();
		long intermediate2 = System.currentTimeMillis();
		intermediateTime = Double.valueOf(intermediate2 - intermediate) / 1000d;
		System.out.println("properties time: " + intermediateTime + " seconds");

		Map<Integer, Integer> dd = a.getDegreeDistribution();
		Map<Double, Integer> cd = a.getClusterDistribution();

		long intermediate3 = System.currentTimeMillis();
		intermediateTime = Double.valueOf(intermediate3 - startTime) / 1000d;
		System.out.println("elapsed time until image creation: "
				+ intermediateTime + " seconds");

		try {
			a.createDegreeDistributionChart(dd);
			a.createClusterDistributionChart(cd);
			long stopTime = System.currentTimeMillis();
			double elapsedTime = Double.valueOf(stopTime - startTime) / 1000d;
			System.out.println("full elapsed time: " + elapsedTime
					+ " seconds\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("The average degree is: " + d);
		System.out.println("The single vertex degree values are: " + l);
		System.out.println(p);
		System.out.println("The degree distribution is: " + dd);
		System.out.println("The local cluster coefficient distribution is: "
				+ cd);
	}
}
