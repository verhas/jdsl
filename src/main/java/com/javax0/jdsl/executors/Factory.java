package com.javax0.jdsl.executors;

/**
 * A factory that returns a {@code T} when {@code #get()} is called.
 * 
 * @author Peter Verhas
 * 
 * @param <T>
 */
public interface Factory<T> {
	T get();
	Factory<?> NONE = null;
}
