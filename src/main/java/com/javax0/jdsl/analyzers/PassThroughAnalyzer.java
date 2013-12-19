package com.javax0.jdsl.analyzers;

/**
 * PassThroughAnalyzer only invokes the underlying analyzer. This is used to
 * build grammar structure, which is recursive (usually grammars are).
 * 
 * @author Peter Verhas
 * 
 */
public class PassThroughAnalyzer implements Analyzer {

	private Analyzer underlyingAnalyzer;

	public void define(Analyzer analyzer) {
		this.underlyingAnalyzer = analyzer;
	}

	@Override
	public AnalysisResult analyze(SourceCode input) {
		if (underlyingAnalyzer == null) {
			throw new RuntimeException(
					PassThroughAnalyzer.class.toString()
							+ " can not analyze until the underlying analyzer was not set");
		}
		return underlyingAnalyzer.analyze(input);
	}

}
