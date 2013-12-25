package com.javax0.jdsl.analyzers.terminals;

import com.javax0.jdsl.analyzers.AnalysisResult;
import com.javax0.jdsl.analyzers.Analyzer;
import com.javax0.jdsl.analyzers.SimpleAnalysisResult;
import com.javax0.jdsl.analyzers.SourceCode;
import com.javax0.jdsl.executors.Executor;
import com.javax0.jdsl.log.LogHelper;

public class TerminalSymbolAnalyzer implements Analyzer {
	private final String lexeme;

	public TerminalSymbolAnalyzer(final String lexeme) {
		this.lexeme = lexeme;
	}

	@Override
	public AnalysisResult analyze(final SourceCode input) {
		LogHelper.logStart(TerminalSymbolAnalyzer.class, input, "%s?", lexeme);
		if (lexeme.length() > input.length()) {
			return SimpleAnalysisResult.failed(TerminalSymbolAnalyzer.class,
					"input short");
		}

		for (int i = 0; i < lexeme.length(); i++) {
			if (lexeme.charAt(i) != input.charAt(i)) {
				return SimpleAnalysisResult
						.failed(TerminalSymbolAnalyzer.class);
			}
		}
		return SimpleAnalysisResult.success(TerminalSymbolAnalyzer.class,
				input.rest(lexeme.length()), Executor.NONE);
	}

	@Override
	public String toString() {
		return (lexeme);
	}
}
