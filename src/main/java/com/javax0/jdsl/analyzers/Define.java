package com.javax0.jdsl.analyzers;

/**
 * Interface used in the fluent API. Using this interface the you can write
 * 
 * <pre>
 *    final Define expression = or(tag,number(),identifier())
 * </pre>
 * 
 * The inner interface {@code deferred} has to be used to define recursive
 * grammar:
 * 
 * <pre>
 * Define expression = later();
 *  ...
 * expression.define( or( list(kw("("),expression,kw(")")), product)
 *  ...
 * </pre>
 * 
 *
 * 
 */
public interface Define extends Analyzer {
	void define(final Analyzer analyzer);
}
