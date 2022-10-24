package com.javax0.jdsl.executors;

/**
 * A factory that returns a {@code T} when {@code #get()} is called.
 * 
 * @author Peter Verhas
 * 
 * @param <T>
 */
@FunctionalInterface
public interface Factory<T> {
	T get();
	Factory<?> NONE = null;

	/**
	 * Create a new factory, which will produce all the time the instance {@code it}.
	 * @param it the instance that the factory will produce
	 * @return the new factory
	 * @param <K> the type of the object that the factory will return
	 */
	static  <K> Factory<K> create(K it){
		return new Factory.get<>(it);
	}

	class get<T> implements Factory<T> {

		private final T it;

		public get(final T it) {
			this.it = it;
		}

		@Override
		public T get() {
			return it;
		}
	}
}
