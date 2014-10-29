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

public class LabelPropagationSignalCollectGephiConnectorImpl extends
		SignalCollectGephiConnector {

	public LabelPropagationSignalCollectGephiConnectorImpl(String fileName,
			scala.Option<Integer> steps) {
		super(fileName, SNAClassNames.LABELPROPAGATION, steps);
	}

	@Override
	public void executeGraph() {
		try {
			super.getLabelPropagation();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public double getAverage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<String, Object> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphProperties getGraphProperties() {
		// TODO Auto-generated method stub
		return null;
	}
}
