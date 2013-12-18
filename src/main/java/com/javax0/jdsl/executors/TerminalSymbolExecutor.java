package com.javax0.jdsl.executors;


/**
 * Terminal symbols do not have much to calculate when they are executed. They
 * are represented by some Java object, like a {@code Long}, {@code Double} that
 * are created from the source code during the analysis.
 * <p>
 * Terminal symbol analyzers create instances of this class with an appropriate
 * type for {@code <T>} and set the value of type {@code T} to return when the
 * symbol is executed during run time.
 * 
 * @author Peter Verhas
 */
public class TerminalSymbolExecutor<T> implements Executor {
    private final T object;

    public TerminalSymbolExecutor(T object) {
        this.object = object;
    }

    @Override
    public T execute() {
        return object;
    }
}
