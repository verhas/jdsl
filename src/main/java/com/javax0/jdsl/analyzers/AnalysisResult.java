package com.javax0.jdsl.analyzers;

import com.javax0.jdsl.executors.Executor;

public interface AnalysisResult {
	boolean wasSuccessful();
	SourceCode remainingSourceCode();
	Executor getExecutor();
}
