package com.javax0.jdsl;

import java.util.LinkedList;
import java.util.List;

/**
 * Implements an analyzer that accepts a code if the underlying analyzers accept
 * the starts of the input one after the other.
 * <p>
 * If there is any skipping analyzer defined then it is used before the list,
 * between the list elements and after the list.
 * 
 * @author Peter Verhas
 * 
 */
public class ListAnalyzer extends SpaceIgnoringAnalyzer {

    private final List<Analyzer> analyzerList = new LinkedList<>();

    /**
     * Add one or more analyzers to the list of the analyzers that are used to
     * analyze the source.
     * 
     * @param analyzers
     */
    public void add(Analyzer... analyzers) {
        for (Analyzer analyzer : analyzers) {
            analyzerList.add(analyzer);
        }
    }

    private final ListExecutor listExecutor;

    /**
     * Set the executor that will be returned by the analysis. During the
     * analysis this executor will be furnished with the underlying executors
     * from the result of the analysis performed by the list elements.
     * 
     * @param listExecutor
     */
    public ListAnalyzer(ListExecutor listExecutor) {
        this.listExecutor = listExecutor;
    }

    @Override
    public AnalysisResult analyze() {
        final List<Executor> executors = new LinkedList<>();
        if (listExecutor != null) {
            listExecutor.setList(executors);
        }
        
        for (Analyzer analyzer : analyzerList) {
            AnalysisResult result = analyzer.analyze(getInput());
            if (!result.wasSuccessful()) {
                return SimpleAnalysisResult.failed();
            }
            if (result.getExecutor() != null) {
                executors.add(result.getExecutor());
            }
            setInput(result.remainingSourceCode());
        }
        return SimpleAnalysisResult.success(getInput(), listExecutor);
    }

}
