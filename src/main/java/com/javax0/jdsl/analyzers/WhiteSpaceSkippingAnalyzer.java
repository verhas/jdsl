package com.javax0.jdsl.analyzers;

/**
 * Analyzer that can be used in SpaceIgnoringAnalyzers to skips white spaces.
 * <p>
 * To use this analyzer in the grammar definition import static the method
 * {@code spaces()}.
 * <p>
 * Usual usage does not need the direct use of this analyzer.
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

	private static final SkippingAnalyzer INSTANCE = new WhiteSpaceSkippingAnalyzer();

	public static SkippingAnalyzer spaces() {
		return INSTANCE;
	}
}
