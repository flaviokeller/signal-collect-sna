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
import com.signalcollect.sna.DegreeDistribution;
import com.signalcollect.sna.ExecutionResult;
import com.signalcollect.sna.GraphProperties;
import com.signalcollect.sna.constants.SNAClassNames;
import com.signalcollect.sna.metrics.PageRank;
import com.signalcollect.sna.parser.ParserImplementor;

public class PageRankSignalCollectGephiConnectorImpl implements
		SignalCollectGephiConnector {

	private ExecutionResult pageRankResult;
	private GraphProperties graphProps;
	private Graph pageRankGraph;
	private String pageRankFileName;
	private DegreeDistribution degreeDistribution;

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
	public GraphProperties getGraphProperties() {
		if (pageRankResult == null) {
			executeGraph();
		}
		graphProps = new GraphProperties(pageRankResult.vertexArray(),
				pageRankFileName);
		return graphProps;
	}

	@Override
	public Map<Integer, Integer> getDegreeDistrbution() {
		if (pageRankResult == null) {
			executeGraph();
		}
		degreeDistribution = new DegreeDistribution(pageRankFileName);
		return degreeDistribution.gatherDegreeeDistribution();
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
		return chart;
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		SignalCollectGephiConnector a = new PageRankSignalCollectGephiConnectorImpl(
				"/Users/flaviokeller/Desktop/examplegraph_separated.gml");
		a.executeGraph();
		double d = a.getAverage();
		Map<String, Object> l = a.getAll();
		GraphProperties p = a.getGraphProperties();
		Map<Integer, Integer> dd = a.getDegreeDistrbution();
		System.out.println("The average pageRank is: " + d);
		System.out.println("The single vertex closeness values are: " + l);
		System.out.println("diameter: " + p.calcDiameter());
		System.out.println(dd);
		try {
			a.createImageFile(dd);
			long stopTime2 = System.currentTimeMillis();
			double elapsedTime = Double.valueOf(stopTime2 - startTime) / 1000d;
			System.out
					.println("full elapsed time: " + elapsedTime + " seconds");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
