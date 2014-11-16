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
import com.signalcollect.sna.metrics.PathCollector;

/**
 * The {@link SignalCollectGephiConnector} implementation for Closeness centrality
 * @author flaviokeller
 *
 */
public class ClosenessSignalCollectGephiConnectorImpl extends
		SignalCollectGephiConnector {

	/** The result of the execution */
	private ExecutionResult closenessResult;
	
	/** The properties of the graph */
	private GraphProperties graphProps;

	/**
	 * The constructor
	 * @param fileName
	 */
	public ClosenessSignalCollectGephiConnectorImpl(String fileName) {
		super(fileName, SNAClassNames.PATH);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void executeGraph() {
		if (closenessResult == null) {
			closenessResult = PathCollector.run(getGraph(),
					SNAClassNames.CLOSENESS);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getAverage() {
		if (closenessResult == null) {
			executeGraph();
		}
		return closenessResult.compRes().average();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getAll() {
		if (closenessResult == null) {
			executeGraph();
		}
		TreeMap<String, Object> result = new TreeMap<String, Object>(
				new NumbersThenWordsComparator());
		result.putAll(closenessResult.compRes().vertexMap());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GraphProperties getGraphProperties() {
		if (closenessResult == null) {
			executeGraph();
		}
		graphProps = new GraphProperties(closenessResult.vertexArray(),
				getFileName());
		graphProps.setPathVertexArray(closenessResult.vertexArray());
		return graphProps;
	}

}
