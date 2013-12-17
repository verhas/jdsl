package com.javax0.jdsl.analyzers;

import java.util.LinkedList;
import java.util.List;

/**
 * Implements an analyzer that accepts a code if one of the underlying analyzers
 * accept the starts of the input.
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
        for (Analyzer analyzer : analyzerList) {
            AnalysisResult result = analyzer.analyze(input);
            if (result.wasSuccessful()) {
                return result;
            }
        }
        return SimpleAnalysisResult.failed();
    }
}
