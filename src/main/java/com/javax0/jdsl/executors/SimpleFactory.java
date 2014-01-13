package com.javax0.jdsl.executors;

public class SimpleFactory<T> implements Factory<T> {

	private final Class<T> klass;

	public SimpleFactory(Class<T> klass) {
		this.klass = klass;
	}

	@Override
	public T get() {
		try {
			return klass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalArgumentException(
					"SimpleFactory can not be applied for the class "
							+ klass.getName(), e);
		}
	}

}
