package com.signalcollect.sna.gephiconnectors;

import java.util.Map;
import java.util.TreeMap;

import com.signalcollect.Graph;
import com.signalcollect.sna.ExecutionResult;
import com.signalcollect.sna.GraphProperties;
import com.signalcollect.sna.metrics.PageRank;
import com.signalcollect.sna.parser.ParserImplementor;

public class PageRankSignalCollectGephiConnectorImpl implements
		SignalCollectGephiConnector {

	private ExecutionResult pageRankResult;
	private GraphProperties graphProps;
	private Graph pageRankGraph;
	private Graph propertiesGraph;
	private String pageRankFileName;

	public PageRankSignalCollectGephiConnectorImpl(String fileName) {
		pageRankFileName = fileName;
		pageRankGraph = ParserImplementor.getGraph(fileName,
				SNAClassNames.PAGERANK);
	}

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
		if (pageRankResult == null) {
			pageRankResult = PageRank.run(pageRankGraph);
		}
	}

	@Override
	public String getGraphProperties() {
		if (pageRankResult == null) {
			executeGraph();
		}
		propertiesGraph = ParserImplementor.getGraph(pageRankFileName,
				SNAClassNames.PATH);
		graphProps = new GraphProperties(pageRankResult.vertexArray(),
				propertiesGraph);
		return graphProps.toString();
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		SignalCollectGephiConnector a = new PageRankSignalCollectGephiConnectorImpl(
				"/power.gml");
		a.executeGraph();
		double d = a.getAverage();
		Map<String, Object> l = a.getAll();
//		String p = a.getGraphProperties();

		System.out.println("The average degree is: " + d);
		System.out.println("The single vertex pageRank values are: " + l);
//		System.out.println(p);
		long stopTime = System.currentTimeMillis();
		double elapsedTime = Double.valueOf(stopTime - startTime) / 1000d;
		System.out.println("elapsed Time: " + elapsedTime + " seconds");
	}
}
