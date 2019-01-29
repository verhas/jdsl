package com.javax0.jdsl.analyzers;

@FunctionalInterface
public interface Closure {
	State exec(SourceCode input);
}
