package com.signalcollect.sna.gephiconnectors;

import java.io.IOException;
import java.util.Map;

import org.jfree.chart.JFreeChart;

import com.signalcollect.sna.DegreeDistribution;
import com.signalcollect.sna.GraphProperties;

public interface SignalCollectGephiConnector {

	public void executeGraph();

	public double getAverage();

	public GraphProperties getGraphProperties();
	
	public Map<Integer,Integer> getDegreeDistrbution();

	public Map<String, Object> getAll();

	public JFreeChart createImageFile(Map<Integer, Integer> degreeDistribution)
			throws IOException;
}
