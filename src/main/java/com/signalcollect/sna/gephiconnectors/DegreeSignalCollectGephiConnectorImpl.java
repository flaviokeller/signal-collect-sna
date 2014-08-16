package com.signalcollect.sna.gephiconnectors;

import java.util.Map;
import java.util.Timer;
import java.util.TreeMap;

import com.signalcollect.Graph;
import com.signalcollect.sna.ExecutionResult;
import com.signalcollect.sna.GraphProperties;
import com.signalcollect.sna.metrics.Degree;
import com.signalcollect.sna.parser.ParserImplementor;

public class DegreeSignalCollectGephiConnectorImpl implements
		SignalCollectGephiConnector {

	private ExecutionResult degreeResult;
	private GraphProperties graphProps;
	private String graphFileName;
	private Graph degreeGraph;
	private Graph propertiesGraph;

	public DegreeSignalCollectGephiConnectorImpl(String fileName) {
		graphFileName = fileName;
		degreeGraph = ParserImplementor.getGraph(fileName, SNAClassNames.DEGREE);
	}

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
			degreeResult = Degree.run(degreeGraph);
		}
	}

	@Override
	public String getGraphProperties() {
		if (degreeResult == null) {
			executeGraph();
		}
		propertiesGraph = ParserImplementor.getGraph(graphFileName, SNAClassNames.PATH);
		graphProps = new GraphProperties(degreeResult.vertexArray(),
				propertiesGraph);
		return graphProps.toString();
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		SignalCollectGephiConnector a = new DegreeSignalCollectGephiConnectorImpl(
				"/power.gml");
		a.executeGraph();
		double d = a.getAverage();
		Map<String, Object> l = a.getAll();
		String p = a.getGraphProperties();
		System.out.println("The average degree is: " + d);
		System.out.println("The single vertex degrees are: " + l);
		System.out.println(p);
		long stopTime = System.currentTimeMillis();
		double elapsedTime = Double.valueOf(stopTime - startTime) / 1000d;
		System.out.println("elapsed time: " + elapsedTime + " seconds");
	}
}
