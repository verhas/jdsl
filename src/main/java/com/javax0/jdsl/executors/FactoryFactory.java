package com.javax0.jdsl.executors;
import java.util.WeakHashMap;


public class FactoryFactory {

	private static final WeakHashMap<Class<? extends ListExecutor>, Factory<? extends ListExecutor>> cache = new WeakHashMap<>();

	public static synchronized <T extends ListExecutor> Factory<T> produce(Class<T> klass) {
		final Factory<T> factory;
		if (cache.containsKey(klass)) {
			factory = (Factory<T>) cache.get(klass);
		} else {
			factory = new SimpleFactory<>(klass);
			cache.put(klass, factory);
		}
		return factory;

	}

}
