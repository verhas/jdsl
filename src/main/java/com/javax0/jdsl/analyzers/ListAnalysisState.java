package com.javax0.jdsl.analyzers;

import java.util.Iterator;
import java.util.List;

public class ListAnalysisState implements AnalysisState, Iterable<AnalysisState> {

	final private List<AnalysisState> states;

	public ListAnalysisState(final List<AnalysisState> states) {
		this.states = states;
	}

	public List<AnalysisState> getStates() {
		return states;
	}

	@Override
	public Iterator<AnalysisState> iterator() {
		return states.iterator();
	}
	
	public int size(){
		return states.size();
	}
}
