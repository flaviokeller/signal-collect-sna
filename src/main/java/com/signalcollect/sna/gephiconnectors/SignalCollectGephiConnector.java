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
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
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
import com.signalcollect.sna.metrics.StepLabelPropagation;
import com.signalcollect.sna.parser.ParserImplementor;

public abstract class SignalCollectGephiConnector {

	private DegreeDistribution degreeDistribution;
	private ClusterDistribution clusterDistribution;
	private String signalCollectFileName;
	private Graph graph;
	scala.Option<Integer> signalSteps;

	public SignalCollectGephiConnector(String fileName, SNAClassNames className) {
		signalCollectFileName = fileName;
		graph = ParserImplementor.getGraph(fileName, className, null);
	}

	protected SignalCollectGephiConnector(String fileName,
			SNAClassNames className, scala.Option<Integer> steps) {
		signalCollectFileName = fileName;
		graph = ParserImplementor.getGraph(fileName, className, steps);
		signalSteps = steps;
	}

	public abstract void executeGraph();

	public abstract double getAverage();

	public abstract Map<String, Object> getAll();

	public abstract GraphProperties getGraphProperties();

	public String getFileName(){
		return signalCollectFileName;
	}
	
	public Map<Integer, Integer> getDegreeDistribution() {
		degreeDistribution = new DegreeDistribution(signalCollectFileName);
		return degreeDistribution.gatherDegreeeDistribution();
	}

	public Map<Double, Integer> getClusterDistribution() {
		clusterDistribution = new ClusterDistribution(signalCollectFileName);
		return clusterDistribution.gatherClusterDistribution();
	}

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
		ChartUtilities.saveChartAsPNG(new File(fileName), chart, 800, 600);
		return chart;
	}

	public JFreeChart createClusterDistributionImageFile(
			Map<Double, Integer> clusterDistribution, String fileName)
			throws IOException {
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
		ChartUtilities.saveChartAsPNG(new File(fileName), chart, 800, 600);

		return chart;
	}

	public void getLabelPropagation() throws IOException {

		Map<Integer, Map<String, Integer>> m = StepLabelPropagation.run(graph,
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

	public Graph getGraph() {
		return graph;
	}

	public static void main(String[] args) {
		SignalCollectGephiConnector a = new LabelPropagationSignalCollectGephiConnectorImpl(
				"/Users/flaviokeller/Desktop/examplegraph.gml",
				scala.Option.apply(100));
		try {
			a.getLabelPropagation();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
