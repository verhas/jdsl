package com.javax0.jdsl.executors;

import java.util.LinkedList;
import java.util.List;

/**
 * A list executor that executes the elements of the list and then returns a
 * new list of the results. If there is only one non-null executor in the list
 * then the resulting list has only one element and then the element itself is
 * returned and not the list.
 * 
 * @author Peter Verhas
 * 
 */
public class SimpleListExecutor implements ListExecutor {

	@Override
	public Object execute() {
		final List<Object> resultList = new LinkedList<>();
		for (Executor executor : executorList) {
			if (executor == null) {
				resultList.add(null);
			} else {
				resultList.add(executor.execute());
			}
		}
		if (resultList.size() == 1) {
			return resultList.get(0);
		} else {
			return resultList;
		}
	}

	private List<Executor> executorList;

	@Override
	public void setList(List<Executor> executorList) {
		this.executorList = executorList;
	}
}
