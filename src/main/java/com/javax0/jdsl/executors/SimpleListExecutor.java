package com.javax0.jdsl.executors;

import java.util.LinkedList;
import java.util.List;

/**
 * A list executor that executes the elements of the list and then returns a new
 * list of the results. If there is only one non-null executor in the list then
 * the resulting list has only one element and then the element itself is
 * returned and not the list.
 */
public class SimpleListExecutor extends AbstractListExecutor {

    @Override
    public Object execute(Context context) {
        final List<Object> resultList = new LinkedList<>();
        for (final Executor executor : executorList) {
            if (executor == null) {
                resultList.add(null);
            } else {
                resultList.add(executor.execute(context));
            }
        }
        if (resultList.size() == 1) {
            return resultList.get(0);
        } else {
            return resultList;
        }
    }

    @Override
    public String toString() {
        final StringBuilder representationBuilder = new StringBuilder();
        representationBuilder.append("[");
        for (final Executor executor : executorList) {
            representationBuilder.append(executor.toString());
        }
        representationBuilder.append("]");
        return representationBuilder.toString();
    }
}
