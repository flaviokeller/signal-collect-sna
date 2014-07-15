package com.signalcollect.sna;

import java.util.Map;

public class DegreeSignalCollectGephiConnectorImpl implements
		SignalCollectGephiConnector {

	private ExecutionResult degreeResult;

	@Override
	public double getAverage() {
		double d = degreeResult.getAverage();
		if (d == 0.0) {
			executeGraph();
			d = degreeResult.getAverage();
			return d;
		} else {
			return d;
		}
	}

	@Override
	public Map<String, Integer> getAll() {
		Map<String, Integer> l = degreeResult.getNodeDegreeList();
		if (l == null) {
			executeGraph();
			l = degreeResult.getNodeDegreeList();
			return l;
		} else {
			return l;
		}
	}

	@Override
	public void executeGraph() {
		Degree.init();
		degreeResult = Degree.run();

	}

	public static void main(String[] args) {
		SignalCollectGephiConnector a = new DegreeSignalCollectGephiConnectorImpl();
		a.executeGraph();
		double d = a.getAverage();
		Map<String, Integer> l = a.getAll();
		System.out.println("The average degree is: " + d);
		System.out.println("The single vertex degrees are: " + l);
	}

}
