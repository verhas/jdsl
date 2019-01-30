package com.javax0.jdsl.analyzers;

import java.util.LinkedList;
import java.util.List;

import com.javax0.jdsl.executors.Executor;
import com.javax0.jdsl.executors.Factory;
import com.javax0.jdsl.executors.ListExecutor;

/**
 * Implements an analyzer that accepts a code if the underlying analyzers accept
 * the starts of the input a few times. The number of times the underlying
 * analyzer is used can be between {@code minRepetition} to
 * {@code maxRepetition}. The minimum for {@code minRepetition} is zero. The
 * {@code maxRepetition} can be -1 for infinite number of allowed repetition, or
 * some positive number that is not less than the {@code minRepetition}.
 * <p>
 * {@code maxRepetition} can not be zero.
 * 
 *
 * 
 */
public class SequenceAnalyzer extends SpaceIgnoringAnalyzer {
	private final Analyzer analyzer;
	private final int minRepetition;
	private final int maxRepetition;
	public static final int INFINITE = -1;

	public static Analyzer analyzer(
			final Factory<ListExecutor> listExecutorFactory,
			final Analyzer analyzer, final int minRepetition,
			final int maxRepetition) {
		return new SequenceAnalyzer(listExecutorFactory, analyzer,
				minRepetition, maxRepetition);
	}

	public SequenceAnalyzer(final Factory<ListExecutor> listExecutorFactory,
			final Analyzer analyzer, final int minRepetition,
			final int maxRepetition) {
		super(listExecutorFactory);
		if (minRepetition < 0) {
			throw new IllegalArgumentException("minRepetition is "
					+ minRepetition + " should not be negative");
		}
		if (maxRepetition == 0) {
			throw new IllegalArgumentException(
					"maxRepetition should not be zero");
		}
		if (maxRepetition != INFINITE && maxRepetition < minRepetition) {
			throw new IllegalArgumentException(
					"maxRepetition "
							+ maxRepetition
							+ " can be -1 for infinite max, or can be larger than or equal to minRepetition "
							+ minRepetition);
		}
		this.analyzer = analyzer;
		this.minRepetition = minRepetition;
		this.maxRepetition = maxRepetition;
	}

	@Override
	public AnalysisResult analyze() {
		final List<Executor> executors = new LinkedList<>();
		final List<State> states = new LinkedList<>();

		int i = 0;
		while (i < minRepetition) {
			final AnalysisResult result = analyzer.analyze(getInput());
			if (!result.wasSuccessful()) {
				return SimpleAnalysisResult.failed(SequenceAnalyzer.class);
			}
			advanceList(result, executors, states);
			i++;
		}
		while (maxRepetition == INFINITE || i < maxRepetition) {
			final AnalysisResult result = analyzer.analyze(getInput());
			if (!result.wasSuccessful()) {
				return SimpleAnalysisResult.success(SequenceAnalyzer.class,
						getInput(), createExecutor(executors),
						new ListAnalysisState(states));
			}
			advanceList(result, executors, states);
			i++;
		}
		return SimpleAnalysisResult.success(SequenceAnalyzer.class, getInput(),
				createExecutor(executors), new ListAnalysisState(states));
	}

	@Override
	public String toString() {
		String repString = null;
		if (minRepetition == 0 && maxRepetition == 1) {
			repString = "?";
		}
		if (minRepetition == 0 && maxRepetition == -1) {
			repString = "?";
		}
		if (minRepetition == 1 && maxRepetition == -1) {
			repString = "+";
		}
		if (repString == null) {
			repString = "{" + minRepetition + "," + maxRepetition + "}";
		}
		return analyzer.toString() + repString;

	}
}
