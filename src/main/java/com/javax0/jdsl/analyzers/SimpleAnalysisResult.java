package com.javax0.jdsl.analyzers;

import com.javax0.jdsl.executors.Executor;

/**
 * A simple implementation of the interface {@link AnalysisResult} that stores
 * the fields initialized in the constructor values passed as argument and
 * returns them.
 * <p>
 * Use the static methods }{@link #failed()} and
 * {@link #success(SourceCode, Executor)} to get an instance of the class.
 * 
 * @author verhasp
 * 
 */
public class SimpleAnalysisResult implements AnalysisResult {
    private final boolean success;
    private final SourceCode remaining;
    private final Executor executor;

    private SimpleAnalysisResult(boolean success, SourceCode remaining, Executor executor) {
        this.success = success;
        this.remaining = remaining;
        this.executor = executor;
    }

    private static final SimpleAnalysisResult FAILED_RESULT = new SimpleAnalysisResult(false, null, null);
    
    public static SimpleAnalysisResult failed() {
        return FAILED_RESULT;
    }

    public static SimpleAnalysisResult success(SourceCode in, Executor r) {
        return new SimpleAnalysisResult(true, in, r);
    }

    @Override
    public boolean wasSuccessful() {
        return success;
    }

    @Override
    public SourceCode remainingSourceCode() {
        return remaining;
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }

}