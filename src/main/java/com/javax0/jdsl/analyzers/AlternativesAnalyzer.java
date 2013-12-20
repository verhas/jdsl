package com.javax0.jdsl.analyzers;

import static com.javax0.jdsl.log.LogHelper.logStart;
import static com.javax0.jdsl.log.LogHelper.get;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;

/**
 * Implements an analyzer that accepts a code if one of the underlying analyzers
 * accept the starts of the input.
 * 
 * @author Peter Verhas
 * 
 */
public class AlternativesAnalyzer implements Analyzer {
	private static Logger LOG = get();
	private final List<Analyzer> analyzerList = new LinkedList<>();

	public void add(Analyzer... analyzers) {
		for (Analyzer analyzer : analyzers) {
			analyzerList.add(analyzer);
		}
	}

	@Override
	public AnalysisResult analyze(SourceCode input) {
		logStart(LOG, input);
		for (Analyzer analyzer : analyzerList) {
			AnalysisResult result = analyzer.analyze(input);
			if (result.wasSuccessful()) {
				LOG.debug("analysis successful");
				return result;
			}
		}
		return SimpleAnalysisResult.failed();
	}
}
