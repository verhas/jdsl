package com.javax0.jdsl.analyzers;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.javax0.jdsl.log.Reporter;
import com.javax0.jdsl.log.ReporterFactory;

/**
 * Implements an analyzer that accepts an input if one of the underlying
 * analyzers accept the starts of the input.
 * <p>
 * If there is any underlying analyzer that accepts the start of the input then
 * the result of that (first one) analysis is returned.
 * 
 *
 * 
 */
public class AlternativesAnalyzer implements Rule {
	private final List<Analyzer> analyzerList = new LinkedList<>();
	private final Reporter reporter = ReporterFactory.getReporter();

	public void add(final Analyzer... analyzers) {
		Collections.addAll(analyzerList, analyzers);
	}

	private boolean analyzerIsNotNullAnalyzer(Analyzer analyzer) {
		return !(analyzer instanceof NullAnalyzer);
	}

	@Override
	public AnalysisResult analyze(final SourceCode input) {
		reporter.logStart(AlternativesAnalyzer.class, input, analyzerList);
		for (final Analyzer analyzer : analyzerList) {
			final AnalysisResult result = analyzer.analyze(input);
			if (result.wasSuccessful() && analyzerIsNotNullAnalyzer(analyzer)) {
				reporter.logSuccess(AlternativesAnalyzer.class);
				return result;
			}
		}
		return SimpleAnalysisResult.failed(AlternativesAnalyzer.class);
	}

	@Override
	public String toString() {
		return "[" + reporter.toString(analyzerList, "|") + "]";
	}
}
