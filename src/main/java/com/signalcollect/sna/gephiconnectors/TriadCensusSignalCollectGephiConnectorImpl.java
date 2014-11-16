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
import com.signalcollect.sna.metrics.TriadCensus;

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
			transitivityResult = TriadCensus.run(getGraph());
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

}
