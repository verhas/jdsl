package com.javax0.jdsl.analyzers;

import java.util.List;

/**
 * This list analyzer has no executor. In addition to that the list of the
 * underlying analyzers can be fetched from the analyzer and thus these lists
 * can be flattened when such an analyzer is a member of a list.
 * <p>
 * This feature is used by the method
 * {@link com.javax0.jdsl.GrammarDefinition#list(Analyzer...)} when there are keyword
 * lists in the argument. This type of list analyzer is returned by the method
 * {@link com.javax0.jdsl.GrammarDefinition#kw(String...)} when there are more than one
 * arguments, and having that passed to the method {@code list()} the latter one
 * is able to flatten it to a single list. This way
 * 
 * <pre>
 * list( kw("A","B"), kw("C") )
 * </pre>
 * 
 * will have the same result as
 * 
 * <pre>
 * list( kw("A"),kw("B"),kw("C") )
 * </pre>
 * 
 * @author Peter Verhas
 * 
 */
public class NoExecutorListAnalyzer extends ListAnalyzer {

	public NoExecutorListAnalyzer() {
		super(null);
	}

	@Override
	public List<Analyzer> getAnalyzerList() {
		return super.getAnalyzerList();
	}
}
