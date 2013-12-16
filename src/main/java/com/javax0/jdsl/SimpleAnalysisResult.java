package com.javax0.jdsl;

public class SimpleAnalysisResult implements AnalysisResult {
	private final boolean success;
	private final SourceCode remaining;
	private final Executor executor;

	private SimpleAnalysisResult(boolean success, SourceCode remaining,
			Executor executor) {
		this.success = success;
		this.remaining = remaining;
		this.executor = executor;
	}

	public static SimpleAnalysisResult failed() {
		return new SimpleAnalysisResult(false, null, null);
	}

	public static SimpleAnalysisResult success(SourceCode in, Executor r) {
		return new SimpleAnalysisResult(true, in, r);
	}

	@Override
	public boolean wasSuccessful() {
		return success;
	}

	@Override
	public SourceCode remainingSourceCode() {
		return remaining;
	}

	@Override
	public Executor getExecutor() {
		return executor;
	}

}
