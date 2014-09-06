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

import static org.junit.Assert.*;

import org.junit.Test;

import com.signalcollect.sna.gephiconnectors.BetweennessSignalCollectGephiConnectorImpl;
import com.signalcollect.sna.gephiconnectors.ClosenessSignalCollectGephiConnectorImpl;
import com.signalcollect.sna.gephiconnectors.DegreeSignalCollectGephiConnectorImpl;
import com.signalcollect.sna.gephiconnectors.LocalClusterCoefficientSignalCollectGephiConnectorImpl;
import com.signalcollect.sna.gephiconnectors.PageRankSignalCollectGephiConnectorImpl;
import com.signalcollect.sna.gephiconnectors.SignalCollectGephiConnector;
import com.signalcollect.sna.gephiconnectors.TransitivitySignalCollectGephiConnectorImpl;

public class MetricsTest {

	private SignalCollectGephiConnector scgc;

	private String testFile = "/Users/flaviokeller/Desktop/examplegraph_separated.gml";

	@Test
	public void degreeTest() {
		scgc = new DegreeSignalCollectGephiConnectorImpl(testFile);
		scgc.executeGraph();
		assertEquals(3.23, scgc.getAverage(), 0.01);
		assertEquals(6, scgc.getAll().get(String.valueOf(5)));
		assertEquals(3, scgc.getAll().get(String.valueOf(3)));
		assertEquals(4, scgc.getAll().get(String.valueOf(7)));
		assertEquals(2, scgc.getAll().get(String.valueOf(20)));
	}

	@Test
	public void pageRankTest() {
		scgc = new PageRankSignalCollectGephiConnectorImpl(testFile);
		scgc.executeGraph();
		assertEquals(0.768, scgc.getAverage(), 0.01);
		assertEquals(0.15, scgc.getAll().get(String.valueOf(5)));
		assertEquals(0.481, scgc.getAll().get(String.valueOf(3)));
		assertEquals(1.68, scgc.getAll().get(String.valueOf(7)));
		assertEquals(0.946, scgc.getAll().get(String.valueOf(20)));
	}

	@Test
	public void closenessTest() {
		scgc = new ClosenessSignalCollectGephiConnectorImpl(testFile);
		scgc.executeGraph();
		assertEquals(1.37, scgc.getAverage(), 0.01);
		assertEquals(1.44, scgc.getAll().get(String.valueOf(5)));
		assertEquals(0.0, scgc.getAll().get(String.valueOf(3)));
		assertEquals(2.0, scgc.getAll().get(String.valueOf(9)));
		assertEquals(1.5, scgc.getAll().get(String.valueOf(20)));
	}

	@Test
	public void betweennessTest() {
		scgc = new BetweennessSignalCollectGephiConnectorImpl(testFile);
		scgc.executeGraph();
		assertEquals(0.0374, scgc.getAverage(), 0.01);
		assertEquals(0.0, scgc.getAll().get(String.valueOf(5)));
		assertEquals(0.0, scgc.getAll().get(String.valueOf(3)));
		assertEquals(0.0541, scgc.getAll().get(String.valueOf(1)));
		assertEquals(0.027, scgc.getAll().get(String.valueOf(20)));
	}

	@Test
	public void localClusterCoefficientTest() {
		scgc = new LocalClusterCoefficientSignalCollectGephiConnectorImpl(
				testFile);
		scgc.executeGraph();
		assertEquals(0.376, scgc.getAverage(), 0.01);
		assertEquals(0.5, scgc.getAll().get(String.valueOf(9)));
		assertEquals(0.333, scgc.getAll().get(String.valueOf(3)));
		assertEquals(0.133, scgc.getAll().get(String.valueOf(5)));
		assertEquals(0.5, scgc.getAll().get(String.valueOf(20)));
	}

	@Test
	public void triadCensusTest() {
		scgc = new TransitivitySignalCollectGephiConnectorImpl(testFile);
		scgc.executeGraph();
		assertEquals(256, scgc.getAll().get(String.valueOf(1)));
		assertEquals(1, scgc.getAll().get(String.valueOf(14)));
		assertEquals(0, scgc.getAll().get(String.valueOf(16)));
		assertEquals(10, scgc.getAll().get(String.valueOf(2)));
		assertEquals(1, scgc.getAll().get(String.valueOf(5)));
	}

}
