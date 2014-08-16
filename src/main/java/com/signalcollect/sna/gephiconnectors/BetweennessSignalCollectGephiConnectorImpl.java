package com.signalcollect.sna.gephiconnectors;

import java.util.Map;
import java.util.TreeMap;

import com.signalcollect.Graph;
import com.signalcollect.sna.ExecutionResult;
import com.signalcollect.sna.GraphProperties;
import com.signalcollect.sna.metrics.Betweenness;
import com.signalcollect.sna.parser.ParserImplementor;

public class BetweennessSignalCollectGephiConnectorImpl implements
		SignalCollectGephiConnector {

	private ExecutionResult betweennessResult;
	private GraphProperties graphProps;
	private String graphFileName;
	private Graph betweennessGraph;

	
	public BetweennessSignalCollectGephiConnectorImpl(String fileName){
		graphFileName = fileName;
		betweennessGraph = ParserImplementor.getGraph(fileName, SNAClassNames.PATH);
	}
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
			betweennessResult = Betweenness.run(betweennessGraph);
		}
	}

	@Override
	public String getGraphProperties() {
		if (betweennessResult == null) {
			executeGraph();
		}
		graphProps = new GraphProperties(betweennessResult.vertexArray(),betweennessGraph);
		graphProps.setPathVertexArray(betweennessResult.vertexArray());
		return graphProps.toString();
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		SignalCollectGephiConnector a = new BetweennessSignalCollectGephiConnectorImpl("/power.gml");
		a.executeGraph();
		double d = a.getAverage();
		Map<String, Object> l = a.getAll();
//		String p = a.getGraphProperties();
		System.out.println("The average betweenness is: " + d);
		System.out.println("The single vertex betweenness values are: " + l);
//		System.out.println(p);
		long stopTime = System.currentTimeMillis();
		double elapsedTime = Double.valueOf(stopTime-startTime)/1000d;
		System.out.println("elapsed Time: " + elapsedTime + " seconds");
	}
}
