package com.javax0.jdsl.analyzers;

import java.util.List;

public class ListAnalysisState implements AnalysisState {

	final private List<AnalysisState> states;

	public ListAnalysisState(final List<AnalysisState> states) {
		this.states = states;
	}

	public List<AnalysisState> getStates() {
		return states;
	}

}
