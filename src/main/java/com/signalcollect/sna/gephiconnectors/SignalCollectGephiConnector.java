package com.signalcollect.sna.gephiconnectors;

import java.io.IOException;
import java.util.Map;

import com.signalcollect.sna.DegreeDistribution;
import com.signalcollect.sna.GraphProperties;

public interface SignalCollectGephiConnector {

	public void executeGraph();

	public double getAverage();

	public GraphProperties getGraphProperties();
	
	public DegreeDistribution getDegreeDistrbution();

	public Map<String, Object> getAll();

	public void createImageFile(Map<Integer, Integer> degreeDistribution)
			throws IOException;
}
