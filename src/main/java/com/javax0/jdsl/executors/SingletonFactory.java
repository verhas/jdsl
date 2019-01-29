package com.javax0.jdsl.executors;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * A factory that returns an instance of the desired class. The class is passed to the method {@link #get(Class)}.
 * If there was no instance of the class created by the factory till this point then it will create a
 * new instance. If there was already an instance created then that instance will be returned. Thus is the name
 * singleton factory.
 * <p>
 * The instances are stored in a static weak hash map keyed with the classes. The method {@link #get(Class)} is
 * synchronized.
 * <p>
 * This
 *
 */
public class SingletonFactory {

	private static final Map<Class<?>, Factory<?>> cache = new WeakHashMap<>();

	public static synchronized <T> Factory<T> get(Class<T> klass) {
		final Factory<T> factory;
		if (cache.containsKey(klass)) {
			factory = (Factory<T>) cache.get(klass);
		} else {
			factory = new NewInstanceFactory<>(klass);
			cache.put(klass, factory);
		}
		return factory;
	}

}
