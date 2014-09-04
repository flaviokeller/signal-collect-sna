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

public class MetricsTest {

	private SignalCollectGephiConnector scgc;

	private GraphProperties props;

	private String testFile = "/Users/flaviokeller/Desktop/examplegraph_separated.gml";

	public void setUp() {

	}

	@Test
	public void degreeTest() {
		scgc = new DegreeSignalCollectGephiConnectorImpl(testFile);
		scgc.executeGraph();
		assertEquals(3.23, scgc.getAverage(), 0.01);
		assertEquals(String.valueOf(6), scgc.getAll().get(String.valueOf(5)));
		assertEquals(String.valueOf(3), scgc.getAll().get(String.valueOf(3)));
		assertEquals(String.valueOf(4), scgc.getAll().get(String.valueOf(7)));
		assertEquals(String.valueOf(2), scgc.getAll().get(String.valueOf(20)));
	}
	@Test
	public void pageRankTest() {
		scgc = new PageRankSignalCollectGephiConnectorImpl(testFile);
		scgc.executeGraph();
		assertEquals(1.06, scgc.getAverage(), 0.01);
		assertEquals(String.valueOf(0.85), scgc.getAll().get(String.valueOf(5)));
		assertEquals(String.valueOf(0.658), scgc.getAll().get(String.valueOf(3)));
		assertEquals(String.valueOf(2.51), scgc.getAll().get(String.valueOf(7)));
		assertEquals(String.valueOf(0.943), scgc.getAll().get(String.valueOf(20)));
	}
	@Test
	public void closenessTest() {
		scgc = new ClosenessSignalCollectGephiConnectorImpl(testFile);
		scgc.executeGraph();
		assertEquals(3.23, scgc.getAverage(), 0.01);
		assertEquals(String.valueOf(6), scgc.getAll().get(String.valueOf(5)));
		assertEquals(String.valueOf(3), scgc.getAll().get(String.valueOf(3)));
		assertEquals(String.valueOf(4), scgc.getAll().get(String.valueOf(7)));
		assertEquals(String.valueOf(2), scgc.getAll().get(String.valueOf(20)));
	}
	@Test
	public void betweennessTest() {
		scgc = new BetweennessSignalCollectGephiConnectorImpl(testFile);
		scgc.executeGraph();
		assertEquals(3.23, scgc.getAverage(), 0.01);
		assertEquals(String.valueOf(6), scgc.getAll().get(String.valueOf(5)));
		assertEquals(String.valueOf(3), scgc.getAll().get(String.valueOf(3)));
		assertEquals(String.valueOf(4), scgc.getAll().get(String.valueOf(7)));
		assertEquals(String.valueOf(2), scgc.getAll().get(String.valueOf(20)));
	}
	@Test
	public void localClusterCoefficientTest() {
		scgc = new LocalClusterCoefficientSignalCollectGephiConnectorImpl(testFile);
		scgc.executeGraph();
		assertEquals(3.23, scgc.getAverage(), 0.01);
		assertEquals(String.valueOf(6), scgc.getAll().get(String.valueOf(5)));
		assertEquals(String.valueOf(3), scgc.getAll().get(String.valueOf(3)));
		assertEquals(String.valueOf(4), scgc.getAll().get(String.valueOf(7)));
		assertEquals(String.valueOf(2), scgc.getAll().get(String.valueOf(20)));
	}

}
