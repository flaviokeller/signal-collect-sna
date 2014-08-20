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
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.signalcollect.Graph;
import com.signalcollect.sna.DegreeDistribution;
import com.signalcollect.sna.ExecutionResult;
import com.signalcollect.sna.GraphProperties;
import com.signalcollect.sna.metrics.Betweenness;
import com.signalcollect.sna.parser.ParserImplementor;

public class BetweennessSignalCollectGephiConnectorImpl implements
		SignalCollectGephiConnector {

	private ExecutionResult betweennessResult;
	private GraphProperties graphProps;
	private String betweennessFileName;
	private Graph betweennessGraph;
	private Graph degreeGraph;
	private DegreeDistribution degreeDistribution;


	public BetweennessSignalCollectGephiConnectorImpl(String fileName) {
		betweennessFileName = fileName;
		betweennessGraph = ParserImplementor.getGraph(fileName,
				SNAClassNames.PATH);
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
	public GraphProperties getGraphProperties() {
		if (betweennessResult == null) {
			executeGraph();
		}
		graphProps = new GraphProperties(betweennessResult.vertexArray(),
				betweennessGraph);
		graphProps.setPathVertexArray(betweennessResult.vertexArray());
		graphProps.calcProperties();
		return graphProps;
	}

	@Override
	public DegreeDistribution getDegreeDistrbution() {
		if (betweennessResult == null) {
			executeGraph();
		}
		degreeGraph = ParserImplementor.getGraph(betweennessFileName,
				SNAClassNames.DEGREE);
		degreeDistribution = new DegreeDistribution(degreeGraph);
		degreeDistribution.calcDistribution();
		return degreeDistribution;

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

		JFreeChart chart = ChartFactory.createHistogram("Degree Distribution",
				"degree value", "number of occurences", dataset,
				PlotOrientation.VERTICAL, true, true, true);

		XYPlot plot = chart.getXYPlot();
		plot.setDataset(0, dataset);
		XYLineAndShapeRenderer renderer0 = new XYLineAndShapeRenderer();
		plot.setRenderer(0, renderer0);
		plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0,
				Color.BLUE);
		ChartUtilities.saveChartAsPNG(new File("degreeDistribution.png"), chart, 750, 450);
		return chart;
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		SignalCollectGephiConnector a = new BetweennessSignalCollectGephiConnectorImpl(
				"/Users/flaviokeller/Desktop/power.gml");
		a.executeGraph();
		double d = a.getAverage();
		Map<String, Object> l = a.getAll();
		GraphProperties p = a.getGraphProperties();
		DegreeDistribution dd = a.getDegreeDistrbution();
		System.out.println("The average closeness is: " + d);
		System.out.println("The single vertex closeness values are: " + l);
		System.out.println(p);
		System.out.println(dd);
		long stopTime = System.currentTimeMillis();
		double elapsedTime = Double.valueOf(stopTime - startTime) / 1000d;
		System.out.println("elapsed time until image creation: " + elapsedTime + " seconds");
		try {
			a.createImageFile(dd.gatherDegreeeDistribution());
			long stopTime2 = System.currentTimeMillis();
			elapsedTime = Double.valueOf(stopTime2 - startTime) / 1000d;
			System.out.println("full elapsed time: " + elapsedTime + " seconds");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
