package com.signalcollect.sna;

import java.util.Map;
import java.util.TreeMap;

public class PageRankSignalCollectGephiConnectorImpl implements
		SignalCollectGephiConnector {

	private ExecutionResult pageRankResult;

	@Override
	public double getAverage() {
		return pageRankResult.getAverage();
	}

	@Override
	public Map<String, Object> getAll() {
		TreeMap<String, Object> result = new TreeMap<String, Object>(
				new NumbersThenWordsComparator());
		result.putAll(pageRankResult.getNodeMap());
		return result;

	}

	@Override
	public void executeGraph() {
		pageRankResult = PageRank.run();

	}

	public static void main(String[] args) {
		SignalCollectGephiConnector a = new PageRankSignalCollectGephiConnectorImpl();
		a.executeGraph();
		double d = a.getAverage();
		Map<String, Object> l = a.getAll();
		System.out.println("The average degree is: " + d);
		System.out.println("The single vertex degrees are: " + l);
	}

}
