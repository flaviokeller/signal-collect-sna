package com.signalcollect.sna;

import java.util.Map;


public interface SignalCollectGephiConnector {
	
	public void executeGraph();

	public double getAverage();
	
	public Map<String,Integer> getAll();
	
}
