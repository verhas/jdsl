package com.javax0.jdsl.executors;

import java.lang.reflect.InvocationTargetException;

/**
 * A factory that simply returns a new instance whenever the method {@link #get()} is invoked.
 * The class to be instantiated in passed as argument to the constructor of the {@link NewInstanceFactory}.
 *
 * @param <T> generic type defining the type of the objects that are created calling the {@link #get()} method.
 */
public class NewInstanceFactory<T> implements Factory<T> {

    private final Class<T> klass;

    public NewInstanceFactory(Class<T> klass) {
        this.klass = klass;
    }

    @Override
    public T get() {
        try {
            return klass.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException |
                InstantiationException |
                IllegalAccessException |
                InvocationTargetException e) {
            throw new IllegalArgumentException(
                    "NewInstanceFactory can not be applied for the class "
                            + klass.getName(), e);
        }
    }

}
