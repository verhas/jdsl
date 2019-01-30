package com.javax0.jdsl.executors;

/**
 * A factory that returns an instance of {@code T} when {@code #get()} is called.
 * 
 *
 * @param <T>
 */
public interface Factory<T> {
	T get();
}
