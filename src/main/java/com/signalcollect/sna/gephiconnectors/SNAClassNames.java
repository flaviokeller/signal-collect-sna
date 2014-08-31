package com.signalcollect.sna.gephiconnectors;

public enum SNAClassNames {
	
	DEGREE("Degree"),
	PAGERANK("PageRank"),
	PATH("Path"),
	LOCALCLUSTERCOEFFICIENT("LocalClusterCoefficient"),
	TRANSITIVITY("Transitivty");

	
	private final String className;
	SNAClassNames(String name) {
		this.className = name;
	}
}
