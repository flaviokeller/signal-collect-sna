package com.signalcollect.sna;

import java.util.Map;
import java.util.TreeMap;

import com.signalcollect.sna.metrics.PageRank;

public class PageRankSignalCollectGephiConnectorImpl implements
		SignalCollectGephiConnector {

	private ExecutionResult pageRankResult;
	private GraphProperties graphProps;

	@Override
	public double getAverage() {
		return pageRankResult.compRes().average();
	}

	@Override
	public Map<String, Object> getAll() {
		TreeMap<String, Object> result = new TreeMap<String, Object>(
				new NumbersThenWordsComparator());
		result.putAll(pageRankResult.compRes().vertexMap());
		return result;

	}

	@Override
	public void executeGraph() {
		pageRankResult = PageRank.run();

	}


	@Override
	public String getGraphProperties() {
		if(pageRankResult == null){
			executeGraph();
		}
		graphProps = new GraphProperties(pageRankResult.vertexArray());
		return graphProps.toString();
	}

	public static void main(String[] args) {
		SignalCollectGephiConnector a = new PageRankSignalCollectGephiConnectorImpl();
		a.executeGraph();
		double d = a.getAverage();
		Map<String, Object> l = a.getAll();
		String p = a.getGraphProperties();

		System.out.println("The average degree is: " + d);
		System.out.println("The single vertex degrees are: " + l);
		System.out.println(p);
	}
}
