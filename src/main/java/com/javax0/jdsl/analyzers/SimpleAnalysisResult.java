package com.javax0.jdsl.analyzers;

import com.javax0.jdsl.executors.Executor;
import com.javax0.jdsl.log.ReporterFactory;

/**
 * A simple implementation of the interface {@link AnalysisResult} that stores
 * the fields initialized in the constructor values passed as argument and
 * returns them.
 * <p>
 * Use the static methods {@link #failed(Class)}/{@link #failed(Class, String)}
 * and {@link #success(Class, SourceCode, Executor)}/
 * {@link #success(Class, SourceCode, Executor, State)} to get an instance of
 * the class.
 * 
 * @author Peter Verhas
 * 
 */
public class SimpleAnalysisResult implements AnalysisResult {
	private final boolean success;
	private final SourceCode remaining;
	private final Executor executor;
	private final State state;

	private SimpleAnalysisResult(final boolean success,
			final SourceCode remaining, final Executor executor,
			final State state) {
		this.success = success;
		this.remaining = remaining;
		this.executor = executor;
		this.state = state;
	}

	private static final SimpleAnalysisResult FAILED_RESULT = new SimpleAnalysisResult(
			false, null, null, null);

	public static SimpleAnalysisResult failed(
			final Class<? extends Analyzer> klass) {
		ReporterFactory.getReporter().logFail(klass);
		return FAILED_RESULT;
	}

	public static SimpleAnalysisResult failed(
			final Class<? extends Analyzer> klass, final String reason) {
		ReporterFactory.getReporter().logFail(klass, reason);
		return FAILED_RESULT;
	}

	public static SimpleAnalysisResult success(
			final Class<? extends Analyzer> klass, final SourceCode in,
			final Executor r, final State state) {
		ReporterFactory.getReporter().logSuccess(klass);
		return new SimpleAnalysisResult(true, in, r, state);
	}

	public static SimpleAnalysisResult success(
			final Class<? extends Analyzer> klass, final SourceCode in,
			final Executor r) {
		return success(klass, in, r, null);
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

	@Override
	public State getState() {
		return state;
	}

}
