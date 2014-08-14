package com.signalcollect.sna;

import java.util.Map;
import java.util.TreeMap;

import com.signalcollect.sna.metrics.Degree;

public class DegreeSignalCollectGephiConnectorImpl implements
		SignalCollectGephiConnector {

	private ExecutionResult degreeResult;
	private GraphProperties graphProps;

	@Override
	public double getAverage() {
		return degreeResult.compRes().average();
	}

	@Override
	public Map<String, Object> getAll() {
		TreeMap<String, Object> result = new TreeMap<String, Object>(
				new NumbersThenWordsComparator());
		result.putAll(degreeResult.compRes().vertexMap());
		return result;
	}

	@Override
	public void executeGraph() {
		if (degreeResult == null) {
			degreeResult = Degree.run();
		}
	}

	@Override
	public String getGraphProperties() {
		if (degreeResult == null) {
			executeGraph();
		}
		graphProps = new GraphProperties(degreeResult.vertexArray());
		return graphProps.toString();
	}

	public static void main(String[] args) {
		SignalCollectGephiConnector a = new DegreeSignalCollectGephiConnectorImpl();
		a.executeGraph();
		double d = a.getAverage();
		Map<String, Object> l = a.getAll();
		String p = a.getGraphProperties();
		System.out.println("The average degree is: " + d);
		System.out.println("The single vertex degrees are: " + l);
		System.out.println(p);
	}
}
