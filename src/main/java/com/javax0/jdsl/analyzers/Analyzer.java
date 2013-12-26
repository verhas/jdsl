package com.javax0.jdsl.analyzers;

/**
 * A syntax analyzer.
 * 
 * @author Peter Verhas
 */
public interface Analyzer {
	/**
	 * Read the input, perform the analysis and return the result of the
	 * analysis.
	 */
	AnalysisResult analyze(final SourceCode input);
}
