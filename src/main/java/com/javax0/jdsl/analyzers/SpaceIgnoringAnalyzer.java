package com.javax0.jdsl.analyzers;

import com.javax0.jdsl.executors.Executor;
import com.javax0.jdsl.executors.Factory;
import com.javax0.jdsl.executors.ListExecutor;
import com.javax0.jdsl.executors.SimpleListExecutor;

import java.util.List;

/**
 * SpaceIgnoringAnalyzer(s) are analyzers that ignore parts of the input,
 * typically white space. The parts to ignore is defined providing a
 * {@link SkippingAnalyzer}, typically an instance of the
 * {@link WhiteSpaceSkippingAnalyzer}.
 *
 * @author Peter Verhas
 */
public abstract class SpaceIgnoringAnalyzer implements Rule {
    private SkippingAnalyzer skippingAnalyzer = null;

    public void setSkipAnalyzer(final SkippingAnalyzer skipAnalyzer) {
        this.skippingAnalyzer = skipAnalyzer;
    }

    private SourceCode skip(final SourceCode sc) {
        final SourceCode skippedSourceCode;
        if (skippingAnalyzer == null) {
            skippedSourceCode = sc;
        } else {
            final AnalysisResult result = skippingAnalyzer.analyze(sc);
            skippedSourceCode = result.remainingSourceCode();
        }
        return skippedSourceCode;
    }

    abstract protected AnalysisResult analyze();

    @Override
    public final AnalysisResult analyze(final SourceCode input) {
        setInput(input);
        return analyze();
    }

    private SourceCode input;

    protected void setInput(final SourceCode input) {
        this.input = skip(input);
    }

    protected SourceCode getInput() {
        return input;
    }

    protected SpaceIgnoringAnalyzer(final Factory<? extends ListExecutor> listExecutorFactory) {
        this.listExecutorFactory = listExecutorFactory;
    }

    private final Factory<? extends ListExecutor> listExecutorFactory;

    /**
     * If the executor is not null in the result then add that to the executors.
     * <p>
     * Add the state from the result to the states.
     * <p>
     * Advance the input to the remaining source code.
     *
     * @param result    that comes from some analyzer.
     * @param executors list of executors for some list.
     * @param states    list of states for some list.
     */
    protected void advanceList(final AnalysisResult result,
                               final List<Executor> executors, final List<State> states) {
        if (result.getExecutor() != null) {
            executors.add(result.getExecutor());
        }
        states.add(result.getState());
        setInput(result.remainingSourceCode());
    }

    /**
     * Create a new executor. If possible to make simplification flattening
     * simple executor structure then flatten.
     */
    protected Executor createExecutor(final List<Executor> executors) {
        final Executor executor;
        if (listExecutorFactory != null && executors.size() > 0) {
            final ListExecutor listExecutor = listExecutorFactory.get();
            if (listExecutor instanceof SimpleListExecutor
                    && executors.size() == 1) {
                executor = executors.get(0);
            } else {
                listExecutor.setList(executors);
                executor = listExecutor;
            }
        } else {
            executor = null;
        }
        return executor;
    }
}
