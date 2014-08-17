package com.signalcollect.sna.gephiconnectors;

import java.util.Map;
import java.util.TreeMap;

import com.signalcollect.Graph;
import com.signalcollect.sna.ExecutionResult;
import com.signalcollect.sna.GraphProperties;
import com.signalcollect.sna.metrics.Closeness;
import com.signalcollect.sna.parser.ParserImplementor;

public class ClosenessSignalCollectGephiConnectorImpl implements
		SignalCollectGephiConnector {

	private ExecutionResult closenessResult;
	private GraphProperties graphProps;
	private Graph closenessGraph;
	private String closenessFileName;

	public ClosenessSignalCollectGephiConnectorImpl(String fileName) {
		closenessFileName = fileName;
		closenessGraph = ParserImplementor.getGraph(fileName,
				SNAClassNames.PATH);
	}

	@Override
	public double getAverage() {
		return closenessResult.compRes().average();
	}

	@Override
	public Map<String, Object> getAll() {
		TreeMap<String, Object> result = new TreeMap<String, Object>(
				new NumbersThenWordsComparator());
		result.putAll(closenessResult.compRes().vertexMap());
		return result;
	}

	@Override
	public void executeGraph() {
		if (closenessResult == null) {
			closenessResult = Closeness.run(closenessGraph);
		}
	}

	@Override
	public String getGraphProperties() {
		if (closenessResult == null) {
			executeGraph();
		}
		graphProps = new GraphProperties(closenessResult.vertexArray(),
				closenessGraph);
		graphProps.setPathVertexArray(closenessResult.vertexArray());

		return graphProps.toString();
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		SignalCollectGephiConnector a = new ClosenessSignalCollectGephiConnectorImpl(
				"/Users/flaviokeller/Desktop/power.gml");
		a.executeGraph();
		double d = a.getAverage();
		Map<String, Object> l = a.getAll();
		String p = a.getGraphProperties();
		System.out.println("The average degree is: " + d);
		System.out.println("The single vertex closeness values are: " + l);
		System.out.println(p);
		long stopTime = System.currentTimeMillis();
		double elapsedTime = Double.valueOf(stopTime - startTime) / 1000d;
		System.out.println("elapsed Time: " + elapsedTime + " seconds");
	}
}
