package com.javax0.jdsl;

public interface AnalysisResult {
	boolean wasSuccessful();
	SourceCode remainingSourceCode();
	Executor getExecutor();
}
