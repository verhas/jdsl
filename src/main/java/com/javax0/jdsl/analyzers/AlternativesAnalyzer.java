package com.javax0.jdsl.analyzers;

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
 * @author Peter Verhas
 * 
 */
public class AlternativesAnalyzer implements Analyzer {
	private final List<Analyzer> analyzerList = new LinkedList<>();
	private final Reporter reporter = ReporterFactory.getReporter();

	public void add(final Analyzer... analyzers) {
		for (final Analyzer analyzer : analyzers) {
			analyzerList.add(analyzer);
		}
	}

	@Override
	public AnalysisResult analyze(final SourceCode input) {
		reporter.logStart(AlternativesAnalyzer.class, input, analyzerList);
		for (final Analyzer analyzer : analyzerList) {
			final AnalysisResult result = analyzer.analyze(input);
			if (result.wasSuccessful()) {
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
