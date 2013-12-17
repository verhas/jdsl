package com.javax0.jdsl;

import java.util.List;

/**
 * An executor that executes a code using the underlying executors provided in
 * the form of list.
 * 
 * @author Peter Verhas
 * 
 */
public interface ListExecutor extends Executor {
    void setList(List<Executor> executorList);
}
