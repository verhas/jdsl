package com.javax0.jdsl.analyzers;

import com.javax0.jdsl.log.Reporter;
import com.javax0.jdsl.log.ReporterFactory;

/**
 * PassThroughAnalyzer only invokes the underlying analyzer. This is used to
 * build grammar structure, which is recursive (usually grammars are).
 * 
 *
 * 
 */
public class PassThroughAnalyzer implements Define {
	private final Reporter reporter = ReporterFactory.getReporter();

	private final String name;

	public PassThroughAnalyzer(final String name) {
		this.name = name;
	}

	private Analyzer underlyingAnalyzer = null;

	public boolean isDefined() {
		return underlyingAnalyzer != null;
	}

	public void define(final Analyzer analyzer) {
		if (analyzer == null) {
			throw new IllegalArgumentException(
					PassThroughAnalyzer.class.getName()
							+ ".define(null) is invalid");
		}
		this.underlyingAnalyzer = analyzer;
	}

	@Override
	public AnalysisResult analyze(final SourceCode input) {
		reporter.logStart(PassThroughAnalyzer.class, input);
		if (underlyingAnalyzer == null) {
			throw new RuntimeException(
					PassThroughAnalyzer.class.toString()
							+ " can not analyze until the underlying analyzer was not set");
		}
		final AnalysisResult result = underlyingAnalyzer.analyze(input);
		if (result.wasSuccessful()) {
			reporter.logSuccess(PassThroughAnalyzer.class);
		} else {
			reporter.logFail(PassThroughAnalyzer.class);
		}
		return result;
	}

	@Override
	public String toString() {
		return name;
	}
}
