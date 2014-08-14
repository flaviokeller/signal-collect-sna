package com.signalcollect.sna;

import java.util.Map;
import java.util.TreeMap;

public class ClosenessSignalCollectGephiConnectorImpl implements
		SignalCollectGephiConnector {

	private ExecutionResult closenessResult;
	private GraphProperties graphProps;

	@Override
	public double getAverage() {
		return closenessResult.compRes().average();
	}

	@Override
	public Map<String, Object> getAll() {
		TreeMap<String,Object> result = new TreeMap<String, Object>(new NumbersThenWordsComparator());
		result.putAll(closenessResult.compRes().vertexMap());
		return result;
	}

	@Override
	public void executeGraph() {
		if(closenessResult == null){
			closenessResult = Closeness.run();
		}
	}


	@Override
	public String getGraphProperties() {
		if(closenessResult == null){
			executeGraph();
		}
		graphProps = new GraphProperties(closenessResult.vertexArray());
		return graphProps.toString();
	}

	public static void main(String[] args) {
		SignalCollectGephiConnector a = new ClosenessSignalCollectGephiConnectorImpl();
		a.executeGraph();
		double d = a.getAverage();
		Map<String, Object> l = a.getAll();
		String p = a.getGraphProperties();
		System.out.println("The average degree is: " + d);
		System.out.println("The single vertex closeness values are: " + l);
		System.out.println(p);
	}
}
