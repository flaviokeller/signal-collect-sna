package com.signalcollect.sna.gephiconnectors;

import java.util.Map;


public interface SignalCollectGephiConnector {
	
	public void executeGraph();

	public double getAverage();
	
	public String getGraphProperties();
	
	public Map<String,Object> getAll();
	
}
