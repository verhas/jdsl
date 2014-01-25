package com.javax0.jdsl.analyzers;

import com.javax0.jdsl.GrammarDefinition;

/**
 * The {@link FailureAnalyzer} does not analyze anything. It ignores the source
 * code it gets as argument to the method {@link #analyze(SourceCode)}. What is
 * does is that it executes the {@link Closure#exec(SourceCode)} method of the
 * object that is passed to the constructor, which is private when the analyzer
 * is created.
 * <p>
 * The intended use of the analyzer is to statically import the
 * {@link #orexec(Closure)} method into the {@link GrammarDefinition} and use that
 * to have some code to be executed at certain point. That certain point is
 * usually in a list.
 * <p>
 * The {@link FailureAnalyzer} returns successful result and the state it returns
 * is the one returned by the closure. Because the return value is a successful
 * result it is counter intuitive to use it in the argument list of the method
 * {@link GrammarDefinition#oneOf(Analyzer...)} or
 * {@link GrammarDefinition#or(Analyzer...)}, since it will only be executed
 * when the argument before it did not match, but at the same time it prevents
 * the analysis of the other alternatives that are after the
 * {@link #orexec(Closure)} call.
 * 
 * @author Peter Verhas
 * 
 */
public class FailureAnalyzer implements Rule {

	private final Closure closure;

	private FailureAnalyzer(Closure closure) {
		this.closure = closure;
	}

	public static Rule orexec(Closure closure) {
		return new FailureAnalyzer(closure);
	}

	@Override
	public AnalysisResult analyze(SourceCode input) {
		closure.exec(input);
		return SimpleAnalysisResult.failed(FailureAnalyzer.class);
	}

}
