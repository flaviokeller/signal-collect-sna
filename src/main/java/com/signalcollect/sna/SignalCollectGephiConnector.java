package com.signalcollect.sna;

import java.util.List;

public interface SignalCollectGephiConnector {
	
	public void executeGraph();

	public int getAverage();
	
	public List<Integer> getAll();
	
}
