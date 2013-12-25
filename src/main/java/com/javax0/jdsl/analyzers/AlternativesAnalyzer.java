package com.javax0.jdsl.analyzers;

import static com.javax0.jdsl.log.LogHelper.logStart;

import java.util.LinkedList;
import java.util.List;

import com.javax0.jdsl.log.LogHelper;

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

	public void add(Analyzer... analyzers) {
		for (Analyzer analyzer : analyzers) {
			analyzerList.add(analyzer);
		}
	}

	@Override
	public AnalysisResult analyze(SourceCode input) {
		logStart(AlternativesAnalyzer.class, input, analyzerList);
		for (Analyzer analyzer : analyzerList) {
			AnalysisResult result = analyzer.analyze(input);
			if (result.wasSuccessful()) {
				LogHelper.logSuccess(AlternativesAnalyzer.class);
				return result;
			}
		}
		return SimpleAnalysisResult.failed(AlternativesAnalyzer.class);
	}

	@Override
	public String toString() {
		return "[" + LogHelper.toString(analyzerList, "|") + "]";
	}
}
