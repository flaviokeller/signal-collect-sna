package com.signalcollect.sna;

import java.util.Map;
import java.util.TreeMap;

import com.signalcollect.sna.metrics.Betweenness;

public class BetweennessSignalCollectGephiConnectorImpl implements
		SignalCollectGephiConnector {

	private ExecutionResult betweennessResult;
	private GraphProperties graphProps;

	@Override
	public double getAverage() {
		return betweennessResult.compRes().average();
	}

	@Override
	public Map<String, Object> getAll() {
		TreeMap<String, Object> result = new TreeMap<String, Object>(
				new NumbersThenWordsComparator());
		result.putAll(betweennessResult.compRes().vertexMap());
		return result;
	}

	@Override
	public void executeGraph() {
		if (betweennessResult == null) {
			betweennessResult = Betweenness.run();
		}
	}

	@Override
	public String getGraphProperties() {
		if (betweennessResult == null) {
			executeGraph();
		}
		graphProps = new GraphProperties(betweennessResult.vertexArray());
		graphProps.setPathVertexArray(betweennessResult.vertexArray());
		return graphProps.toString();
	}

	public static void main(String[] args) {
		SignalCollectGephiConnector a = new BetweennessSignalCollectGephiConnectorImpl();
		a.executeGraph();
		double d = a.getAverage();
		Map<String, Object> l = a.getAll();
		String p = a.getGraphProperties();
		System.out.println("The average degree is: " + d);
		System.out.println("The single vertex betweenness values are: " + l);
		System.out.println(p);
	}
}
