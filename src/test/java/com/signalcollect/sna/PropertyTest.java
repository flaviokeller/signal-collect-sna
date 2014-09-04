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

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.signalcollect.sna.gephiconnectors.DegreeSignalCollectGephiConnectorImpl;
import com.signalcollect.sna.gephiconnectors.SignalCollectGephiConnector;

public class PropertyTest {

	private SignalCollectGephiConnector scgc;

	private GraphProperties props;

	private String testFile = "/Users/flaviokeller/Desktop/examplegraph_separated.gml";

	@Before
	public void setUp() {
		scgc = new DegreeSignalCollectGephiConnectorImpl(testFile);
	}

	@Test
	public void propertyTest() {
		props = scgc.getGraphProperties();
		assertEquals(13, props.calcSize());
		assertEquals(0.135, props.calcDensity(), 0.01);
		assertEquals(3.0, props.calcDiameter(), 0.01);
		assertEquals(1.38, props.calcReciprocity(), 0.01);
	}

	@Test
	public void degreeDistributionTest() {
		Map<Integer,Integer> dd = scgc.getDegreeDistrbution();
		assertEquals(Integer.valueOf(4), dd.get(Integer.valueOf(2)));
		assertEquals(Integer.valueOf(1), dd.get(Integer.valueOf(6)));
		assertNull(dd.get(Integer.valueOf(1)));
	}
	
	@Test
	public void clusterDistributionTest(){
		
	}

}
