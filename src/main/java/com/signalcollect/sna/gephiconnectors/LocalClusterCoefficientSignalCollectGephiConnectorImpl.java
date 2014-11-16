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

import com.signalcollect.sna.ClusterDistribution;
import com.signalcollect.sna.ExecutionResult;
import com.signalcollect.sna.GraphProperties;
import com.signalcollect.sna.constants.SNAClassNames;
import com.signalcollect.sna.metrics.LocalClusterCoefficient;

/**
 * The {@link SignalCollectGephiConnector} implementation for the Local Cluster Coefficient
 * @author flaviokeller
 *
 */
public class LocalClusterCoefficientSignalCollectGephiConnectorImpl extends
		SignalCollectGephiConnector {

	/** The result of the execution */
	private ExecutionResult localClusterCoefficientResult;
	
	/** The properties of the graph */
	private GraphProperties graphProps;
	
	/**
	 * The constructor
	 * @param fileName
	 */
	public LocalClusterCoefficientSignalCollectGephiConnectorImpl(
			String fileName) {
		super(fileName, SNAClassNames.LOCALCLUSTERCOEFFICIENT);
	}

	@Override
	public void executeGraph() {
		if (localClusterCoefficientResult == null) {
			localClusterCoefficientResult = LocalClusterCoefficient
					.run(getGraph());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getAverage() {
		if (localClusterCoefficientResult == null) {
			executeGraph();
		}
		return localClusterCoefficientResult.compRes().average();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getAll() {
		if (localClusterCoefficientResult == null) {
			executeGraph();
		}
		TreeMap<String, Object> result = new TreeMap<String, Object>(
				new NumbersThenWordsComparator());
		result.putAll(localClusterCoefficientResult.compRes().vertexMap());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GraphProperties getGraphProperties() {
		if (localClusterCoefficientResult == null) {
			executeGraph();
		}
		graphProps = new GraphProperties(
				localClusterCoefficientResult.vertexArray(), getFileName());
		return graphProps;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<Double, Integer> getClusterDistribution() {
		if (localClusterCoefficientResult == null) {
			executeGraph();
		}
		clusterDistribution = new ClusterDistribution(getFileName());
		clusterDistribution.setClusterMap(localClusterCoefficientResult
				.compRes().vertexMap());

		return clusterDistribution.gatherClusterDistribution();
	}

}
