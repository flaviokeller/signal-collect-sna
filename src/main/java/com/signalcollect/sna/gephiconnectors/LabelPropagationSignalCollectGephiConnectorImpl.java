package com.signalcollect.sna.gephiconnectors;

import java.io.IOException;
import java.util.Map;

import com.signalcollect.sna.GraphProperties;
import com.signalcollect.sna.constants.SNAClassNames;

public class LabelPropagationSignalCollectGephiConnectorImpl extends
		SignalCollectGephiConnector {

	public LabelPropagationSignalCollectGephiConnectorImpl(String fileName,
			scala.Option<Integer> steps) {
		super(fileName, SNAClassNames.STEPLABELPROPAGATION, steps);
	}

	@Override
	public void executeGraph() {
		// TODO Auto-generated method stub

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
