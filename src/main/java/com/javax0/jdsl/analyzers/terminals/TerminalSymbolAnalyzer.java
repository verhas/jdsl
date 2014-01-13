package com.javax0.jdsl.analyzers.terminals;

import com.javax0.jdsl.analyzers.AnalysisResult;
import com.javax0.jdsl.analyzers.Rule;
import com.javax0.jdsl.analyzers.SimpleAnalysisResult;
import com.javax0.jdsl.analyzers.SourceCode;
import com.javax0.jdsl.executors.Executor;
import com.javax0.jdsl.log.Reporter;
import com.javax0.jdsl.log.ReporterFactory;

public class TerminalSymbolAnalyzer implements Rule {
	private final Reporter reporter = ReporterFactory.getReporter();

	public interface CharCompare {
		boolean isEqual(char a, char b);

		CharCompare caseSensitive = new CharCompare() {
			@Override
			public boolean isEqual(char a, char b) {
				return a == b;
			}
		};
		CharCompare caseInsensitive = new CharCompare() {
			@Override
			public boolean isEqual(char a, char b) {
				return Character.toLowerCase(a) == Character.toLowerCase(b);
			}
		};

	}

	private final CharCompare charCompare;

	private final String lexeme;

	public TerminalSymbolAnalyzer(final String lexeme) {
		this.lexeme = lexeme;
		charCompare = CharCompare.caseSensitive;
	}

	public TerminalSymbolAnalyzer(final String lexeme, CharCompare charCompare) {
		this.lexeme = lexeme;
		this.charCompare = charCompare;
	}

	@Override
	public AnalysisResult analyze(final SourceCode input) {
		reporter.logStart(TerminalSymbolAnalyzer.class, input, "%s?", lexeme);
		if (lexeme.length() > input.length()) {
			return SimpleAnalysisResult.failed(TerminalSymbolAnalyzer.class,
					"input short");
		}

		for (int i = 0; i < lexeme.length(); i++) {
			if (!charCompare.isEqual(lexeme.charAt(i), input.charAt(i))) {
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
