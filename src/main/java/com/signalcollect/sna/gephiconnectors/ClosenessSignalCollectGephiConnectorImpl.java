/*
 *  @author Flavio Keller
 *
 *  Copyright 2014 University of Zurich
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.signalcollect.sna.gephiconnectors;

import java.awt.Color;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.signalcollect.Graph;
import com.signalcollect.sna.ClusterDistribution;
import com.signalcollect.sna.DegreeDistribution;
import com.signalcollect.sna.ExecutionResult;
import com.signalcollect.sna.GraphProperties;
import com.signalcollect.sna.constants.SNAClassNames;
import com.signalcollect.sna.metrics.Closeness;
import com.signalcollect.sna.parser.ParserImplementor;

public class ClosenessSignalCollectGephiConnectorImpl implements
		SignalCollectGephiConnector {

	private ExecutionResult closenessResult;
	private GraphProperties graphProps;
	private Graph closenessGraph;
	private String closenessFileName;
	private DegreeDistribution degreeDistribution;
	private ClusterDistribution clusterDistribution;

	public ClosenessSignalCollectGephiConnectorImpl(String fileName) {
		closenessFileName = fileName;
		closenessGraph = ParserImplementor.getGraph(fileName,
				SNAClassNames.PATH);
	}

	@Override
	public void executeGraph() {
		if (closenessResult == null) {
			closenessResult = Closeness.run(closenessGraph);
		}
	}

	@Override
	public double getAverage() {
		if (closenessResult == null) {
			executeGraph();
		}
		return closenessResult.compRes().average();
	}

	@Override
	public Map<String, Object> getAll() {
		if (closenessResult == null) {
			executeGraph();
		}
		TreeMap<String, Object> result = new TreeMap<String, Object>(
				new NumbersThenWordsComparator());
		result.putAll(closenessResult.compRes().vertexMap());
		return result;
	}

	@Override
	public GraphProperties getGraphProperties() {
		if (closenessResult == null) {
			executeGraph();
		}
		graphProps = new GraphProperties(closenessResult.vertexArray(),
				closenessFileName);
		// graphProps.setPathVertexMap(closenessResult.compRes().vertexMap());
		return graphProps;
	}

	@Override
	public Map<Integer, Integer> getDegreeDistribution() {
		degreeDistribution = new DegreeDistribution(closenessFileName);
		return degreeDistribution.gatherDegreeeDistribution();

	}

	@Override
	public Map<Double, Integer> getClusterDistribution() {
		clusterDistribution = new ClusterDistribution(closenessFileName);
		return clusterDistribution.gatherClusterDistribution();
	}

	@Override
	public JFreeChart createDegreeDistributionImageFile(
			Map<Integer, Integer> degreeDistribution, String fileName)
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
		return chart;
	}

	@Override
	public JFreeChart createClusterDistributionImageFile(
			Map<Double, Integer> degreeDistribution, String fileName)
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

		JFreeChart chart = ChartFactory.createHistogram(
				"Cluster Coefficient Distribution",
				"cluster coefficient value", "number of occurences", dataset,
				PlotOrientation.VERTICAL, true, true, true);

		XYPlot plot = chart.getXYPlot();
		plot.setDataset(0, dataset);
		XYLineAndShapeRenderer renderer0 = new XYLineAndShapeRenderer();
		plot.setRenderer(0, renderer0);
		plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0,
				Color.BLUE);
		return chart;
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		SignalCollectGephiConnector a = new ClosenessSignalCollectGephiConnectorImpl(
				"/Users/flaviokeller/Desktop/power.gml");
		a.executeGraph();
		double d = a.getAverage();
		Map<String, Object> l = a.getAll();
		long intermediate = System.currentTimeMillis();
		double intermediateTime = Double.valueOf(intermediate - startTime) / 1000d;
		System.out.println("execution time: " + intermediateTime + " seconds");

		GraphProperties p = a.getGraphProperties();
		p.toString();
		long intermediate2 = System.currentTimeMillis();
		intermediateTime = Double.valueOf(intermediate2 - intermediate) / 1000d;
		System.out.println("properties time: " + intermediateTime + " seconds");

		Map<Integer, Integer> dd = a.getDegreeDistribution();
		Map<Double, Integer> cd = a.getClusterDistribution();

		long intermediate3 = System.currentTimeMillis();
		intermediateTime = Double.valueOf(intermediate3 - startTime) / 1000d;
		System.out.println("elapsed time until image creation: "
				+ intermediateTime + " seconds");

		try {
			a.createDegreeDistributionImageFile(dd, "degreeDistr.png");
			a.createClusterDistributionImageFile(cd, "clusterdistr.png");
			long stopTime = System.currentTimeMillis();
			double elapsedTime = Double.valueOf(stopTime - startTime) / 1000d;
			System.out
					.println("full elapsed time: " + elapsedTime + " seconds\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("The average closeness is: " + d);
		System.out.println("The single vertex closeness values are: " + l);
		System.out.println(p);
		System.out.println("The degree distribution is: " + dd);
		System.out.println("The local cluster coefficient distribution is: " + cd);
	}
}
