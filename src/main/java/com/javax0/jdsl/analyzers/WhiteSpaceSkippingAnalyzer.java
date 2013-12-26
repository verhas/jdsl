package com.javax0.jdsl.analyzers;

/**
 * Analyzer that can be used in SpaceIgnoringAnalyzers to skips white spaces.
 * 
 * @author Peter Verhas
 * 
 */
public class WhiteSpaceSkippingAnalyzer extends SkippingAnalyzer {

	@Override
	protected int countCharacters(final SourceCode input) {
		int i = 0;
		while (i < input.length() && Character.isWhitespace(input.charAt(i))) {
			i++;
		}
		return i;
	}
}
