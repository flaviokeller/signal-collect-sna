package com.signalcollect.sna;

import java.util.List;

public class SignalCollectGephiConnectorImpl implements
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
	public List<Integer> getAll() {
		List<Integer> l = degreeResult.getNodeDegreeList();
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

	private void run() {
		// TODO Auto-generated method stub

	}

	public static void main(String[] args) {
		SignalCollectGephiConnector a = new SignalCollectGephiConnectorImpl();
		a.executeGraph();
		double d = a.getAverage();
		List<Integer> l = a.getAll();
		System.out.println("The average degree is: " + d);
		System.out.println("The degree edge is: " + l);
	}

}
