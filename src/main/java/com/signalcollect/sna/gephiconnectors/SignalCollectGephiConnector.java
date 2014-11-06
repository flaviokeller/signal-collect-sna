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
import java.awt.Font;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.StackedBarRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import com.signalcollect.Graph;
import com.signalcollect.sna.ClusterDistribution;
import com.signalcollect.sna.DegreeDistribution;
import com.signalcollect.sna.GraphProperties;
import com.signalcollect.sna.constants.SNAClassNames;
import com.signalcollect.sna.metrics.LabelPropagation;
import com.signalcollect.sna.parser.ParserImplementor;

/**
 * Abstract, generic implementation for outside access to all Social Network
 * Analysis implementations of Signal/Collect
 * 
 * provides all needed functions to get the SNA metric values of a graph and the
 * network properties as well
 * 
 * -> mainly designed for the Gephi plugin, but may be used otherwise as well
 * 
 */
public abstract class SignalCollectGephiConnector {

	/** The Distribution of degrees in a graph */
	protected DegreeDistribution degreeDistribution;

	/** The Distribution of cluster coefficients in a graph */
	protected ClusterDistribution clusterDistribution;

	/** The file path which is used to parse the graph */
	private String signalCollectFileName;

	/** Represents the graph, which is currently viewed */
	private Graph graph;

	/**
	 * The number of signal and collect steps that should be done (only used for
	 * Label Propagation)
	 */
	scala.Option<Integer> signalSteps;

	/**
	 * Constructor
	 * 
	 * @param fileName
	 * @param className
	 */
	public SignalCollectGephiConnector(String fileName, SNAClassNames className) {
		signalCollectFileName = fileName;
		graph = ParserImplementor.getGraph(fileName, className, null);
	}

	/**
	 * Constructor which is used by the Label Propagation algorithm
	 * 
	 * @param fileName
	 * @param className
	 * @param steps
	 */
	protected SignalCollectGephiConnector(String fileName,
			SNAClassNames className, scala.Option<Integer> steps) {
		signalCollectFileName = fileName;
		graph = ParserImplementor.getGraph(fileName, className, steps);
		signalSteps = steps;
	}

	/** Executes an algorithm on the parsed Graph */
	public abstract void executeGraph();

	/**
	 * Gets the calculated Average of a Social Network Analysis method in the
	 * current graph
	 * 
	 * @return the Average value
	 */
	public abstract double getAverage();

	/**
	 * Gets all calculated values of a Social Network Analysis method in the
	 * current graph
	 * 
	 * @return {@link Map} with all values (key = vertex id, value = vertex
	 *         value)
	 */
	public abstract Map<String, Object> getAll();

	/**
	 * Gets the GraphProperties instance
	 * 
	 * @return the properties of the graph
	 */
	public abstract GraphProperties getGraphProperties();

	/**
	 * Gets the name (the actual path) of the current File
	 * 
	 * @return the file name
	 */
	public String getFileName() {
		return signalCollectFileName;
	}

	/**
	 * Gets the degree distribution in a graph
	 * 
	 * @return {@link Map} object (key = degree value, value = number of
	 *         occurences)
	 */
	public Map<Integer, Integer> getDegreeDistribution() {
		degreeDistribution = new DegreeDistribution(signalCollectFileName);
		return degreeDistribution.gatherDegreeeDistribution();
	}

	/**
	 * Gets the cluster distribution in a graph
	 * 
	 * @return {@link Map} object (key = degree value, value = number of
	 *         occurences)
	 */
	public Map<Double, Integer> getClusterDistribution() {
		clusterDistribution = new ClusterDistribution(signalCollectFileName);
		return clusterDistribution.gatherClusterDistribution();
	}

	/**
	 * Creates the Chart of the Degree Distribution according to the calculated
	 * distribution
	 * 
	 * @param degreeDistribution
	 * @return a {@link JFreeChart} containing the distribution of degrees in
	 *         the graph
	 * @throws IOException
	 */
	public JFreeChart createDegreeDistributionChart(
			Map<Integer, Integer> degreeDistribution) throws IOException {
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
		dataset.setAutoWidth(true);

		JFreeChart chart = ChartFactory.createHistogram("Degree Distribution",
				"degree value", "number of occurences", dataset,
				PlotOrientation.VERTICAL, true, true, true);

		XYPlot plot = chart.getXYPlot();
		XYBarRenderer renderer0 = new XYBarRenderer();
		Font font = new Font("Font", 0, 14);
		renderer0.setMargin(0.2);
		renderer0.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
		renderer0.setBaseItemLabelsVisible(true);
		renderer0.setBaseItemLabelFont(font);
		plot.setDataset(0, dataset);
		plot.setRenderer(0, renderer0);
		plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0,
				Color.BLUE);
		return chart;
	}

	/**
	 * Creates the Chart of the Clustering Distribution according to the
	 * calculated distribution
	 * 
	 * @param clusterDistribution
	 * @return a {@link JFreeChart} containing the distribution of local cluster
	 *         coefficients in the graph
	 * @throws IOException
	 */
	public JFreeChart createClusterDistributionChart(
			Map<Double, Integer> clusterDistribution) throws IOException {
		XYSeries dSeries = new XYSeries("number of occurences");
		for (Iterator it = clusterDistribution.entrySet().iterator(); it
				.hasNext();) {
			Map.Entry d = (Map.Entry) it.next();
			Number x = (Number) d.getKey();
			Number y = (Number) d.getValue();
			dSeries.add(x, y);
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(dSeries);
		dataset.setAutoWidth(true);

		JFreeChart chart = ChartFactory.createHistogram(
				"Cluster Coefficient Distribution",
				"cluster coefficient value", "number of occurences", dataset,
				PlotOrientation.VERTICAL, true, true, true);

		XYPlot plot = chart.getXYPlot();
		XYBarRenderer renderer0 = new XYBarRenderer();
		Font font = new Font("Font", 0, 14);
		renderer0.setMargin(0.2);
		renderer0.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
		renderer0.setBaseItemLabelsVisible(true);
		renderer0.setBaseItemLabelFont(font);
		plot.setDataset(0, dataset);
		plot.setRenderer(0, renderer0);

		plot.getRendererForDataset(plot.getDataset(0)).setSeriesPaint(0,
				Color.BLUE);

		return chart;
	}

	/**
	 * Gets the Label Propagation in the graph and creates a chart out of it
	 * 
	 * @throws IOException
	 */
	public void getLabelPropagation() throws IOException {

		Map<Integer, Map<String, Integer>> m = LabelPropagation.run(graph,
				signalSteps.get());

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		final JFreeChart chart = ChartFactory.createStackedBarChart(
				"Evolving Label Propagation", "Signal Step", null, dataset,
				PlotOrientation.VERTICAL, false, false, false);

		CategoryPlot plot = (CategoryPlot) chart.getPlot();

		StackedBarRenderer renderer = new StackedBarRenderer();

		plot.setDataset(dataset);
		plot.setRenderer(renderer);
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(
				"{0} {2} {3}", NumberFormat.getInstance()));
		renderer.setBaseItemLabelsVisible(true);
		if (signalSteps.get() > 10) {
			long stepInterval = Math.round(new Double(signalSteps.get()
					.doubleValue() / 10d));
			for (int i = (int) stepInterval; i <= signalSteps.get(); i += stepInterval) {
				Set<Map.Entry<String, Integer>> entrySet = m
						.get(new Integer(i)).entrySet();
				for (Map.Entry<String, Integer> subentry : entrySet) {
					dataset.addValue(subentry.getValue(), subentry.getKey(),
							new Integer(i));
				}
			}
		} else {
			for (Map.Entry<Integer, Map<String, Integer>> entry : m.entrySet()) {
				for (Map.Entry<String, Integer> subentry : entry.getValue()
						.entrySet()) {
					dataset.addValue(subentry.getValue(), subentry.getKey(),
							entry.getKey());
				}
			}
		}
		renderer.setRenderAsPercentages(true);
		NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setNumberFormatOverride(NumberFormat.getPercentInstance());

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(1200, 600));
		ApplicationFrame f = new ApplicationFrame("Label Propagation");
		f.setContentPane(chartPanel);
		f.pack();
		f.setVisible(true);
	}

	/**
	 * Gets the {@link Graph} instance
	 * 
	 * @return
	 */
	public Graph getGraph() {
		return graph;
	}

	// public static void main(String[] args) {
	// SignalCollectGephiConnector a = new
	// LabelPropagationSignalCollectGephiConnectorImpl(
	// "/Users/flaviokeller/Desktop/examplegraph.gml",
	// scala.Option.apply(100));
	// try {
	// a.getLabelPropagation();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
}
