package com.signalcollect.sna.gephiconnectors;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.signalcollect.Graph;
import com.signalcollect.sna.DegreeDistribution;
import com.signalcollect.sna.ExecutionResult;
import com.signalcollect.sna.GraphProperties;
import com.signalcollect.sna.metrics.Transitivity;
import com.signalcollect.sna.parser.ParserImplementor;

public class TransitivitySignalCollectGephiConnectorImpl implements
		SignalCollectGephiConnector {

	private ExecutionResult transitivityResult;
	private GraphProperties graphProps;
	private String transitivityFileName;
	private Graph transitivityGraph;
	private Graph degreeGraph;
	private Graph propertiesGraph;
	private DegreeDistribution degreeDistribution;

	public TransitivitySignalCollectGephiConnectorImpl(String fileName) {
		transitivityFileName = fileName;
		transitivityGraph = ParserImplementor.getGraph(fileName,
				SNAClassNames.TRANSITIVITY);
	}

	@Override
	public void executeGraph() {
		if (transitivityResult == null) {
			transitivityResult = Transitivity.run(transitivityGraph);
		}
	}

	@Override
	public double getAverage() {
		return transitivityResult.compRes().average();
	}

	@Override
	public Map<Integer, Integer> getDegreeDistrbution() {
		if (transitivityResult == null) {
			executeGraph();
		}
		degreeDistribution = new DegreeDistribution(transitivityFileName);
		return degreeDistribution.gatherDegreeeDistribution();
	}

	@Override
	public Map<String, Object> getAll() {
		TreeMap<String, Object> result = new TreeMap<String, Object>(
				new NumbersThenWordsComparator());
		result.putAll(transitivityResult.compRes().vertexMap());
		return result;
	}

	@Override
	public JFreeChart createImageFile(Map<Integer, Integer> degreeDistribution)
			throws IOException {
		XYSeries dSeries = new XYSeries("number of occurences");
		for (Iterator it = degreeDistribution.entrySet().iterator(); it
				.hasNext();) {
			Map.Entry d = (Map.Entry) it.next();
			Number x = (Number) d.getKey();
			Number y = (Number) d.getValue();
			dSeries.add(x, y);
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(dSeries);
		// dataset.setAutoWidth(true);
		// XYBarDataset dset = new XYBarDataset(dataset, 10.0);

		JFreeChart chart = ChartFactory.createHistogram("Degree Distribution",
				"degree value", "number of occurences", dataset,
				PlotOrientation.VERTICAL, true, true, true);

		XYPlot plot = chart.getXYPlot();
		plot.setDataset(0, dataset);
		XYBarRenderer renderer0 = new XYBarRenderer();
		plot.setRenderer(0, renderer0);
		plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0,
				Color.BLUE);
		ChartUtilities.saveChartAsPNG(new File("hello.png"), chart, 750, 450);
		return chart;
	}

	@Override
	public GraphProperties getGraphProperties() {
		if (transitivityResult == null) {
			executeGraph();
		}
		graphProps = new GraphProperties(transitivityResult.vertexArray(),
				transitivityFileName);
		return graphProps;
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		SignalCollectGephiConnector a = new TransitivitySignalCollectGephiConnectorImpl(
				"/Users/flaviokeller/Desktop/examplegraph.gml");
		a.executeGraph();
//		double d = a.getAverage();
//		Map<String, Object> l = a.getAll();
		long intermediate = System.currentTimeMillis();
		double intermediateTime = Double.valueOf(intermediate - startTime) / 1000d;
		System.out.println("execution time: " + intermediateTime + " seconds");
		// GraphProperties p = a.getGraphProperties();
//		Map<Integer, Integer> dd = a.getDegreeDistrbution();
//		System.out.println("The average transitivity is: " + d);
//		System.out.println("The single vertex transitivity values are: " + l);
		// System.out.println(p);
		long intermediate2 = System.currentTimeMillis();
		double intermediateTime2 = Double.valueOf(intermediate2 - intermediate) / 1000d;
		System.out
				.println("properties time: " + intermediateTime2 + " seconds");
//		System.out.println("Degree distribution: " + dd);
		long stopTime = System.currentTimeMillis();
		double elapsedTime = Double.valueOf(stopTime - startTime) / 1000d;
		System.out.println("elapsed time until image creation: " + elapsedTime
				+ " seconds");

//		try {
//			a.createImageFile(dd);
//			long stopTime2 = System.currentTimeMillis();
//			elapsedTime = Double.valueOf(stopTime2 - startTime) / 1000d;
//			System.out
//					.println("full elapsed time: " + elapsedTime + " seconds");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

}
