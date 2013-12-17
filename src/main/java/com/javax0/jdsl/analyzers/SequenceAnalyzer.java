package com.javax0.jdsl.analyzers;

import java.util.LinkedList;
import java.util.List;

import com.javax0.jdsl.executors.Executor;
import com.javax0.jdsl.executors.ListExecutor;

/**
 * Implements an analyzer that accepts a code if the underlying analyzers accept
 * the starts of the input a few times. The number of times the underlying
 * analyzer is used can be between {@code minRepetition} to
 * {@code maxRepetition}. The minimum for {@code minRepetition} is zero. The
 * {@code maxRepetition} can be -1 for infinite number of allowed repetition, or
 * some positive number that is not less than the {@code minRepetition}.
 * <p>
 * {@code maxRepetition} can not be zero.
 * 
 * @author Peter Verhas
 * 
 */
public class SequenceAnalyzer extends SpaceIgnoringAnalyzer {
    private final Analyzer analyzer;
    private final ListExecutor listExecutor;
    private final int minRepetition;
    private final int maxRepetition;

    public SequenceAnalyzer(ListExecutor listExecutor, Analyzer analyzer, int minRepetition, int maxRepetition) {
        if (minRepetition < 0) {
            throw new IllegalArgumentException("minRepetition is " + minRepetition + " should not be negative");
        }
        if (maxRepetition == 0) {
            throw new IllegalArgumentException("maxRepetition should not be zero");
        }
        if (maxRepetition != -1 && maxRepetition < minRepetition) {
            throw new IllegalArgumentException("maxRepetition " + maxRepetition
                            + " can be -1 for infinite max, or can be larger than or equal to minRepetition " + minRepetition);
        }
        this.listExecutor = listExecutor;
        this.analyzer = analyzer;
        this.minRepetition = minRepetition;
        this.maxRepetition = maxRepetition;
    }

    @Override
    public AnalysisResult analyze() {
        final List<Executor> executors = new LinkedList<>();
        if (listExecutor != null) {
            listExecutor.setList(executors);
        }
        
        int i = 0;
        while (i < minRepetition) {
            AnalysisResult result = analyzer.analyze(getInput());
            if (!result.wasSuccessful()) {
                return SimpleAnalysisResult.failed();
            }
            if (result.getExecutor() != null) {
                executors.add(result.getExecutor());
            }
            setInput(result.remainingSourceCode());
            i++;
        }
        while (maxRepetition == -1 || i < maxRepetition) {
            AnalysisResult result = analyzer.analyze(getInput());
            if (!result.wasSuccessful()) {
                return SimpleAnalysisResult.success(getInput(), listExecutor);
            }
            if (result.getExecutor() != null) {
                executors.add(result.getExecutor());
            }
            setInput(result.remainingSourceCode());
            i++;
        }
        return SimpleAnalysisResult.success(getInput(), listExecutor);
    }
}
