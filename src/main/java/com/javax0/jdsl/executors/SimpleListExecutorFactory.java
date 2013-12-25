package com.javax0.jdsl.executors;

/**
 * Creates a new ListExecutor each time {@code get()} is called.
 * 
 * @author Peter Verhas
 * 
 */
public class SimpleListExecutorFactory implements Factory<ListExecutor> {

	public static final Factory<ListExecutor> INSTANCE = new SimpleListExecutorFactory();

	@Override
	public ListExecutor get() {
		return new SimpleListExecutor();
	}

}
