package com.signalcollect.sna.gephiconnectors;

import java.awt.Dimension;

import javax.swing.JFrame;

import com.signalcollect.sna.parser.ParserImplementor;

import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;

public class Plotter {// extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		//
		Graph<Integer, String> g = ParserImplementor
				.getPlotGraph("/Users/flaviokeller/Desktop/examplegraph.gml");
		// EdgeFactory<Integer, String> ef = new ClassBasedEdgeFactory<Integer,
		// String>(String.class);
		// org.jgrapht.Graph< Integer, String> g2 = new
		// DirectedMultigraph<Integer,
		// String>(ef);
		// g2.addVertex(1);
		// g2.addVertex(2);
		// g2.addVertex(3);
		// g2.addVertex(4);
		// g2.addEdge(1, 2);
		// g2.addEdge(1, 3);
		// g2.addEdge(2, 4);
		// g2.addEdge(3, 2);
		// g2.addEdge(4, 3);
		//
		// mxgr
		// // g.addVertex("a");
		// // g.addVertex("b");
		// // g.addVertex("c");
		// // // Add some edges. From above we defined these to be of type
		// String
		// // // Note that the default is for undirected edges.
		// // g.addEdge("Edge-A", "a", "b"); // Note that Java 1.5 auto-boxes
		// // // primitives
		// // g.addEdge("Edge-B", "b", "c");
		// // // Let's see what we have. Note the nice output from the
		// // // SparseMultigraph<V,E> toString() method
		// System.out.println("The graph g = " + g.toString());
		// // SimpleGraphView sgv = new SimpleGraphView(); //We create our graph
		// in
		// // here
		// // The Layout<V, E> is parameterized by the vertex and edge types
		Layout<Integer, String> layout = new ISOMLayout(g);
		layout.setSize(new Dimension(750, 450)); // sets the initial size of the
													// space

		// The BasicVisualizationServer<V,E> is parameterized by the edge types
		BasicVisualizationServer<Integer, String> vv = new BasicVisualizationServer<Integer, String>(
				layout);
		vv.setPreferredSize(new Dimension(800, 500)); // Sets the viewing area
														// size

		JFrame frame = new JFrame("Simple Graph View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}
	// public Plotter() {
	// JFrame f = new JFrame();
	// f.setSize(500, 500);
	// f.setLocation(300, 200);
	//
	// final mxGraph graph = new mxGraph();
	// mxGraphComponent graphComponent = new mxGraphComponent(graph);
	// f.getContentPane().add(BorderLayout.CENTER, graphComponent);
	// f.setVisible(true);
	//
	// Object parent = graph.getDefaultParent();
	// graph.getModel().beginUpdate();
	// try {
	//
	// graphComponent =
	// ParserImplementor.getPlotGraph("/Users/flaviokeller/Desktop/examplegraph.gml",
	// graph);
	// // Object v1 = graph.insertVertex(parent, null, "node1", 100, 100, 80,
	// // 30);
	// // Object v2 = graph.insertVertex(parent, null, "node2", 100, 100, 80,
	// // 30);
	// // Object v3 = graph.insertVertex(parent, null, "node3", 100, 100, 80,
	// // 30);
	// //
	// // graph.insertEdge(parent, null, "Edge", v1, v2);
	// // graph.insertEdge(parent, null, "Edge", v2, v3);
	//
	// } finally {
	// graph.getModel().endUpdate();
	// }
	//
	// // define layout
	// mxIGraphLayout layout = new mxFastOrganicLayout(graph);
	//
	// // layout using morphing
	// graph.getModel().beginUpdate();
	// try {
	// layout.execute(graph.getDefaultParent());
	// } finally {
	// mxMorphing morph = new mxMorphing(graphComponent, 20, 1.2, 20);
	//
	// morph.addListener(mxEvent.DONE, new mxIEventListener() {
	//
	// @Override
	// public void invoke(Object arg0, mxEventObject arg1) {
	// graph.getModel().endUpdate();
	// // fitViewport();
	// }
	//
	// });
	//
	// morph.startAnimation();
	// }
	//
	// }
	//
	// public static void main(String[] args) {
	// Plotter t = new Plotter();
	//
	// }
}