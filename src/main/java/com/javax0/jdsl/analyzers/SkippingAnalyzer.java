package com.javax0.jdsl.analyzers;

import com.javax0.jdsl.executors.Executor;
import com.javax0.jdsl.log.Reporter;
import com.javax0.jdsl.log.ReporterFactory;

/**
 * A skipping analyzer is an analyzer that eats up character from the start of
 * the input but does not produce any executor. The best example is the
 * {@link WhiteSpaceSkippingAnalyzer} that eats up white spaces.
 * <p>
 * SkippingAnalyzers can be used in {@link SpaceIgnoringAnalyzer}s.
 * <p>
 * Skipping analyzers are always successful. If there is no character to ignore
 * then the skipping analyzers return in their result the original input.
 * 
 *
 * 
 */
public abstract class SkippingAnalyzer implements Rule {
	private final Reporter reporter = ReporterFactory.getReporter();
	private final static Executor NO_EXECUTOR = null;

	protected abstract int countCharacters(final SourceCode input);

	@Override
	public final AnalysisResult analyze(final SourceCode input) {
		final int numberOfSkippedCharactets = countCharacters(input);
		reporter.logStart(SkippingAnalyzer.class, input, " skipping %d chars",
				numberOfSkippedCharactets);
		final AnalysisResult result;
		if (numberOfSkippedCharactets < input.length()) {
			if (numberOfSkippedCharactets == 0) {
				result = SimpleAnalysisResult.success(SkippingAnalyzer.class,
						input, NO_EXECUTOR);
			} else {
				result = SimpleAnalysisResult.success(SkippingAnalyzer.class,
						input.rest(numberOfSkippedCharactets), NO_EXECUTOR);
			}
		} else {
			result = SimpleAnalysisResult.success(SkippingAnalyzer.class,
					StringSourceCode.EMPTY_SOURCE, NO_EXECUTOR);
		}
		return result;
	}
}
