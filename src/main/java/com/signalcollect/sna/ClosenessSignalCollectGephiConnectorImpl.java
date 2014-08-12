package com.signalcollect.sna;

import java.util.Map;
import java.util.TreeMap;

public class ClosenessSignalCollectGephiConnectorImpl implements
		SignalCollectGephiConnector {

	private ExecutionResult degreeResult;

	@Override
	public double getAverage() {
		return degreeResult.compRes().average();
	}

	@Override
	public Map<String, Object> getAll() {
		TreeMap<String,Object> result = new TreeMap<String, Object>(new NumbersThenWordsComparator());
		result.putAll(degreeResult.compRes().vertexMap());
		return result;
	}

	@Override
	public void executeGraph() {
		degreeResult = Closeness.run();
	}


	@Override
	public String getGraphProperties() {
		return degreeResult.graphProps().toString();
	}

	public static void main(String[] args) {
		SignalCollectGephiConnector a = new ClosenessSignalCollectGephiConnectorImpl();
		a.executeGraph();
		double d = a.getAverage();
		Map<String, Object> l = a.getAll();
//		String p = a.getGraphProperties();
		System.out.println("The average degree is: " + d);
		System.out.println("The single vertex closeness values are: " + l);
//		System.out.println(p);
	}
}
