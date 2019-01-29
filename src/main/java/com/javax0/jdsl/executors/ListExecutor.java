package com.javax0.jdsl.executors;

import java.util.List;

/**
 * An executor that executes a code using the underlying executors provided in
 * the form of list.
 * <p>
 * A list executor can do many things. An IF statement can be executed implementing and using a list executor. In that
 * case the IF statement is compiled to a list of three executor objects: one for the condition, one for the
 * true branch and one for the false branch. The IF executor executes the condition (element number zero) and based
 * that the second (number one) or third (number two) element of the list of the executors and finally returns the
 * result of the final one.
 *
 * @author Peter Verhas
 */
public interface ListExecutor extends Executor {
    void setList(final List<Executor> executorList);
}
