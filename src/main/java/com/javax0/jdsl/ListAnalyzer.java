package com.javax0.jdsl;

import java.util.LinkedList;
import java.util.List;

/**
 * Implements an analyzer that accepts a code if the underlying analyzers accept
 * the starts of the input one after the other.
 * 
 * @author Peter Verhas
 * 
 */
public class ListAnalyzer implements Analyzer {

    private final List<Analyzer> analyzerList = new LinkedList<>();

    public void add(Analyzer... analyzers) {
        for (Analyzer analyzer : analyzers) {
            analyzerList.add(analyzer);
        }
    }

    private final ListExecutor listExecutor;

    public ListAnalyzer(ListExecutor listExecutor) {
        this.listExecutor = listExecutor;
    }

    @Override
    public AnalysisResult analyze(SourceCode input) {
        SourceCode rollingInput = input;
        List<Executor> executors = new LinkedList<>();
        if (listExecutor != null) {
            listExecutor.setList(executors);
        }
        for (Analyzer analyzer : analyzerList) {
            AnalysisResult result = analyzer.analyze(rollingInput);
            if (!result.wasSuccessful()) {
                return SimpleAnalysisResult.failed();
            }
            if (result.getExecutor() != null) {
                executors.add(result.getExecutor());
            }
            rollingInput = result.remainingSourceCode();
        }
        return SimpleAnalysisResult.success(rollingInput, listExecutor);
    }

}
