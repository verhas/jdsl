package com.javax0.jdsl.analyzers;

import com.javax0.jdsl.executors.Executor;

/**
 * Type to return by an analyzer.
 * @author Peter Verhas
 *
 */
public interface AnalysisResult {
	/**
	 * True if the analysis was successful.
	 */
	boolean wasSuccessful();
	/**
	 * Returns the source code not used by the analyzer.
	 */
	SourceCode remainingSourceCode();
	/**
	 * The executor to be used when the analyzed segment is to be executed.
	 */
	Executor getExecutor();
	
	/**
	 * Get a state of the analysis
	 */
	State getState();
}
