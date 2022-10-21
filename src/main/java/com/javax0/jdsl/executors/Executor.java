package com.javax0.jdsl.executors;

/**
 * An executor executes some part of the code it gets in the context. The context is arbitrary and very much depends
 * on the structure of the application. Individual executors can get access to the built-up AST. An executor can
 * calculate something, but as well it can also create come generated code. It is called executor as the original aim
 * of JDSL is to create Java based Domain Specific Languages, which are usually interpreted.
 */
public interface Executor {
	Executor NONE = null;
	Object execute(Context context);
}
