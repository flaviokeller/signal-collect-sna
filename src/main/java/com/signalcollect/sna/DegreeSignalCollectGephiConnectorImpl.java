package com.signalcollect.sna;

import java.util.Map;
import java.util.TreeMap;

public class DegreeSignalCollectGephiConnectorImpl implements
		SignalCollectGephiConnector {

	private ExecutionResult degreeResult;

	@Override
	public double getAverage() {
		return degreeResult.getAverage();
	}

	@Override
	public Map<String, Object> getAll() {
		TreeMap<String,Object> result = new TreeMap<String, Object>(new NumbersThenWordsComparator());
		result.putAll(degreeResult.getNodeMap());
		return result;
	}

	@Override
	public void executeGraph() {
		degreeResult = Degree.run();
	}

	public static void main(String[] args) {
		SignalCollectGephiConnector a = new DegreeSignalCollectGephiConnectorImpl();
		a.executeGraph();
		double d = a.getAverage();
		Map<String, Object> l = a.getAll();
		System.out.println("The average degree is: " + d);
		System.out.println("The single vertex degrees are: " + l);
	}

}
