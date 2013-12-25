package com.javax0.jdsl.analyzers;

import com.javax0.jdsl.log.LogHelper;

/**
 * PassThroughAnalyzer only invokes the underlying analyzer. This is used to
 * build grammar structure, which is recursive (usually grammars are).
 * 
 * @author Peter Verhas
 * 
 */
public class PassThroughAnalyzer implements Analyzer {

	private String name;

	public PassThroughAnalyzer(String name) {
		this.name = name;
	}

	private Analyzer underlyingAnalyzer;

	public void define(Analyzer analyzer) {
		this.underlyingAnalyzer = analyzer;
	}

	@Override
	public AnalysisResult analyze(SourceCode input) {
		LogHelper.logStart(PassThroughAnalyzer.class, input);
		if (underlyingAnalyzer == null) {
			throw new RuntimeException(
					PassThroughAnalyzer.class.toString()
							+ " can not analyze until the underlying analyzer was not set");
		}
		AnalysisResult result = underlyingAnalyzer.analyze(input);
		if (result.wasSuccessful()) {
			LogHelper.logSuccess(PassThroughAnalyzer.class);
		} else {
			LogHelper.logFail(PassThroughAnalyzer.class);
		}
		return result;
	}

	@Override
	public String toString() {
		return name;
	}
}
