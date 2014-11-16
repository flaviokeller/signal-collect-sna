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

import java.util.Comparator;
import java.util.TreeMap;

/**
 * This comparator sorts {@link TreeMap} keys with ascending numbers followed by words
 * @author flaviokeller
 *
 */
public class NumbersThenWordsComparator implements Comparator<String> {
	
	/**
	 * @param a string object
	 * @return the numeric integer value of a string
	 */
	private static Integer intValue(String s) {
		try {
			return Integer.valueOf(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(String s1, String s2) {
		Integer i1 = intValue(s1);
		Integer i2 = intValue(s2);
		if (i1 == null && i2 == null) {
			return s1.compareTo(s2);
		} else if (i1 == null) {
			return -1;
		} else if (i2 == null) {
			return 1;
		} else {
			return i1.compareTo(i2);
		}
	}
}
