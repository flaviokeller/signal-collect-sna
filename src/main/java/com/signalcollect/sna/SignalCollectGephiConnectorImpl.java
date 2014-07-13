package com.signalcollect.sna;

import java.util.List;

public class SignalCollectGephiConnectorImpl implements
		SignalCollectGephiConnector {
	
	

	@Override
	public int getAverage() {
//		Degree d = Degree;
		return 0;
	}

	@Override
	public List<Integer> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void executeGraph() {
		Degree.run();
		
	}
	
	
	public static void main(String[] args){
		SignalCollectGephiConnectorImpl a = new SignalCollectGephiConnectorImpl();
		a.executeGraph();
	}

}
