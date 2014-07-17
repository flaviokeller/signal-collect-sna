package com.signalcollect.sna;

import java.util.Map;

public class PageRankSignalCollectGephiConnectorImpl implements
		SignalCollectGephiConnector {

	private ExecutionResult pageRankResult;

	@Override
	public double getAverage() {
		double d = pageRankResult.getAverage();
		if (d == 0.0) {
			executeGraph();
			d = pageRankResult.getAverage();
			return d;
		} else {
			return d;
		}
	}

	@Override
	public Map<String, Object> getAll() {
		Map<String, Object> l = pageRankResult.getNodeMap();
		if (l == null) {
			executeGraph();
			l = pageRankResult.getNodeMap();
			return l;
		} else {
			return l;
		}
	}

	@Override
	public void executeGraph() {
		PageRank.init();
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
