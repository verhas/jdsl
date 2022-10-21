package com.javax0.jdsl.executors;

import java.util.List;

/**
 * Implements the {@code setList()} method in the interface {@link ListExecutor}
 * , and a method to get access to the executors.
 * <p>
 * It is recommended that list executors extend this class instead of
 * implementing the interface directly.
 * 
 *
 * 
 */
public abstract class AbstractListExecutor implements ListExecutor {

	private List<Executor> executorList;

	@Override
	public void setList(List<Executor> executorList) {
		this.executorList = executorList;
	}

	/**
	 * Get the i-th executor. Throws exception if index 'i' is out of bounds.
	 */
	public Executor getExecutor(int i) {
		return executorList.get(i);
	}

	/**
	 * @return the number of the executors in the list
	 */
	public int numberOfExecutors() {
		return executorList.size();
	}
}
