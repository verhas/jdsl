package com.javax0.jdsl.executors;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * A factory creating factories. It will return a singleton instance of a factory for each class to be created by the
 * factory that it returns.
 */
public class SingletonFactory {

    private static final Map<Class<?>, Factory<?>> cache = new WeakHashMap<>();

    /**
     * Create a factory that will return {@code klass} type objects. The returned factory will call the default
     * constructor of the class.
     * <p>
     * If a factory was already created for this class them it will be reused.
     * <p>
     * The factory instances are stored in a static weak hash map keyed with the classes. The method {@link #get(Class)}
     * is not synchronized. Because of that it may happen in a multi-thread application that one class may have two or
     * more factory objects. Should not be a big problem.
     *
     * @param klass for which the factory is needed
     * @param <T>   the type of the class
     * @return the new or already existing factory
     */
    public static <T> Factory<T> get(Class<T> klass) {
        final Factory<T> factory;
        if (cache.containsKey(klass)) {
            factory = (Factory<T>) cache.get(klass);
        } else {
            factory = new NewInstanceFactory<>(klass);
            cache.put(klass, factory);
        }
        return factory;
    }

    /**
     * The same as {@link #get(Class)}, but this class is synchronized.
     *
     * @param klass for which the factory is needed
     * @param <T>   the type of the class
     * @return the new or already existing factory
     */
    public static synchronized <T> Factory<T> synchronized_get(Class<T> klass) {
        return get(klass);
    }

}
