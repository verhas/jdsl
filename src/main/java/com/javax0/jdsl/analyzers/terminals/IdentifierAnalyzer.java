package com.javax0.jdsl.analyzers.terminals;

import com.javax0.jdsl.analyzers.AnalysisResult;
import com.javax0.jdsl.analyzers.Analyzer;
import com.javax0.jdsl.analyzers.SimpleAnalysisResult;
import com.javax0.jdsl.analyzers.SourceCode;
import com.javax0.jdsl.executors.TerminalSymbolExecutor;
import com.javax0.jdsl.log.Reporter;
import com.javax0.jdsl.log.ReporterFactory;

/**
 * Analyzes an identifier.
 * 
 * An identifier is something that can be used in languages to denote a
 * variable, function, method, class. Something that the user identifies. An
 * identifier should start with an alpha character, with the character {@code $}
 * or with the character {@code _} and may continue with these character and
 * also may contain numbers. This is a very simple and usual identifier
 * definition.
 * <p>
 * If a grammar needs a different definition of identifier then a new analyzer
 * matching the appropriate definition should be created and the static method
 * {@code identifier()} from that class is to be included to the grammar
 * definition.
 * 
 * @author Peter Verhas
 * 
 */
public class IdentifierAnalyzer implements Analyzer {
	private final Reporter reporter = ReporterFactory.getReporter();

	private boolean isIndexInRange(final int i, final SourceCode input) {
		return i < input.length();
	}

	private boolean isDigit(final int i, final SourceCode input) {
		return isIndexInRange(i, input) && Character.isDigit(input.charAt(i));
	}

	private boolean isStartChar(final int i, final SourceCode input) {
		return isIndexInRange(i, input)
				&& Character.isAlphabetic(input.charAt(i));
	}

	private boolean isIdentifierCharacter(final int i, final SourceCode input) {
		return isStartChar(i, input) || isDigit(i, input);
	}

	@Override
	public AnalysisResult analyze(final SourceCode input) {
		reporter.logStart(IdentifierAnalyzer.class, input);
		if (isStartChar(0, input)) {
			final StringBuilder sb = new StringBuilder();
			int i = 0;
			while (isIdentifierCharacter(i, input)) {
				sb.append(input.charAt(i));
				i++;
			}
			return SimpleAnalysisResult.success(IdentifierAnalyzer.class,
					input.rest(i),
					new TerminalSymbolExecutor<>(sb.toString()));
		} else {
			return SimpleAnalysisResult.failed(IdentifierAnalyzer.class);
		}
	}

	public static Analyzer identifier() {
		return new IdentifierAnalyzer();
	}

	@Override
	public String toString() {
		return "identifier";
	}
}
