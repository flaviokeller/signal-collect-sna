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

import com.signalcollect.sna.GraphProperties;
import com.signalcollect.sna.constants.SNAClassNames;

/**
 * The {@link SignalCollectGephiConnector} implementation for Label Propagation
 * 
 * @author flaviokeller
 * 
 */
public class LabelPropagationSignalCollectGephiConnectorImpl extends
		SignalCollectGephiConnector {

	/**
	 * Constructor
	 * 
	 * @param fileName
	 * @param steps
	 */
	public LabelPropagationSignalCollectGephiConnectorImpl(String fileName,
			scala.Option<Integer> steps) {
		super(fileName, SNAClassNames.LABELPROPAGATION, steps);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void executeGraph() {
		try {
			super.getLabelPropagation();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * {@inheritDoc} not implemented/returns 0 because label propagation does
	 * not need to calculate an average vertex value
	 */
	@Override
	public double getAverage() {
		return 0;
	}

	/**
	 * {@inheritDoc} not implemented/returns null because label propagation does
	 * not need to gather all vertex values
	 */
	@Override
	public Map<String, Object> getAll() {
		return null;
	}

	/**
	 * {@inheritDoc} not implemented/returns null because label propagation does
	 * not need to get the graph properties
	 */
	@Override
	public GraphProperties getGraphProperties() {
		return null;
	}
}
