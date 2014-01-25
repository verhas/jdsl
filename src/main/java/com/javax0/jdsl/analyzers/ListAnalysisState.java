package com.javax0.jdsl.analyzers;

import java.util.Iterator;
import java.util.List;

public class ListAnalysisState implements State, Iterable<State> {

	final private List<State> states;

	public ListAnalysisState(final List<State> states) {
		this.states = states;
	}

	public List<State> getStates() {
		return states;
	}

	@Override
	public Iterator<State> iterator() {
		return states.iterator();
	}
	
	public int size(){
		return states.size();
	}
}
