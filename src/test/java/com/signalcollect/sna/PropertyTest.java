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

package com.signalcollect.sna;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Map;

import org.junit.Test;

import com.signalcollect.sna.gephiconnectors.DegreeSignalCollectGephiConnectorImpl;
import com.signalcollect.sna.gephiconnectors.SignalCollectGephiConnector;

/**
 * 
 * @author flaviokeller
 *
 */
public class PropertyTest {

	/**
	 * The generic Signal/Collect Social Network Analysis metrics execution instance
	 */
	private SignalCollectGephiConnector scgc;

	/**
	 * Instance to store the Graph's properties
	 */
	private GraphProperties props;

	/**
	 * File path for the graph tested
	 */
	private String testFile = "/Users/flaviokeller/Documents/Uni/Bachelorarbeit/Datasets/gml/examplegraph_separated.gml";

	/**
	 * Test case for the properties in Signal/Collect
	 */
	@Test
	public void propertyTest() {
		if (scgc == null) {
			scgc = new DegreeSignalCollectGephiConnectorImpl(testFile);
		}
		scgc.executeGraph();
		props = scgc.getGraphProperties();
		assertEquals(13, props.calcSize());
		assertEquals(0.135, props.calcDensity(), 0.01);
		assertEquals(3.0, props.calcDiameter(), 0.01);
		assertEquals(0.243, props.calcReciprocity(), 0.01);
	}

	/**
	 * Test case for the degree distribution in Signal/Collect
	 */
	@Test
	public void degreeDistributionTest() {
		if (scgc == null) {
			scgc = new DegreeSignalCollectGephiConnectorImpl(testFile);
		}
		scgc.executeGraph();
		Map<Integer, Integer> dd = scgc.getDegreeDistribution();
		assertEquals(Integer.valueOf(4), dd.get(Integer.valueOf(2)));
		assertEquals(Integer.valueOf(1), dd.get(Integer.valueOf(6)));
		assertNull(dd.get(Integer.valueOf(1)));
	}

	/**
	 * Test case for the cluster distribution in Signal/Collect
	 */
	@Test
	public void clusterDistributionTest() {
		if (scgc == null) {
			scgc = new DegreeSignalCollectGephiConnectorImpl(testFile);
		}
		scgc.executeGraph();
		Map<Double, Integer> cd = scgc.getClusterDistribution();
		assertEquals(6, cd.get(0.333).intValue());
		assertEquals(5, cd.get(0.5).intValue());
		assertEquals(1, cd.get(0.133).intValue());
		assertEquals(1, cd.get(0.25).intValue());
	}
}
