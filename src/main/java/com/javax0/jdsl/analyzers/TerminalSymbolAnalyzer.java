package com.javax0.jdsl.analyzers;

import com.javax0.jdsl.executors.Executor;

public class TerminalSymbolAnalyzer implements Analyzer {

	private final String keyword;

	public TerminalSymbolAnalyzer(final String keyword) {
		this.keyword = keyword;
	}

	@Override
	public AnalysisResult analyze(final SourceCode input) {
		if (keyword.length() > input.length()) {
			return SimpleAnalysisResult.failed();
		}

		for (int i = 0; i < keyword.length(); i++) {
			if (keyword.charAt(i) != input.charAt(i)) {
				return SimpleAnalysisResult.failed();
			}
		}
		return SimpleAnalysisResult.success(input.rest(keyword.length()), Executor.NONE);
	}

}
