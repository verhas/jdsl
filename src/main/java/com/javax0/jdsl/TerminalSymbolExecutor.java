package com.javax0.jdsl;

public class TerminalSymbolExecutor<T> implements Executor {
	private final T object;

	public TerminalSymbolExecutor(T object) {
		this.object = object;
	}

	@Override
	public T execute() {
		return object;
	}
}
