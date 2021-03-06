package com.javax0.jdsl.analyzers;

/**
 * A syntax analyzer.
 * 
 */
@FunctionalInterface
public interface Analyzer {

	/**
	 * Read the input, perform the analysis and return the result of the
	 * analysis.
	 */
	AnalysisResult analyze(final SourceCode input);
}
