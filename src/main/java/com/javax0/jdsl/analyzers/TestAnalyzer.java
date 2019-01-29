package com.javax0.jdsl.analyzers;

import com.javax0.jdsl.GrammarDefinition;

/**
 * An analyzer that can be used to test that an analyzer would be successful at
 * a certain point.
 * <p>
 * The test will result success/failed if the underlying analyzer is successful
 * and fails if that fail. The actual return success/fail is inverted if the
 * constructor argument {@code invert} is true. When the returned result is
 * success it contains the original source code and {@code null} state and
 * executor.
 * <p>
 * To use this analyzer statically import the method {@link #test(Analyzer)}
 * and/or {@link #not(Analyzer)} and use them in the {@link GrammarDefinition}
 * implementation.
 * <p>
 * The method {@link #testNot(Analyzer)} is a complimentary method and can be
 * used instead of {@link #not(Analyzer)} when the word "testNot" is better for
 * the readability.
 * 
 * @author Peter Verhas
 * 
 */
public class TestAnalyzer implements Rule {

	private final Analyzer analyzerToTest;
	private final boolean invert;

	private TestAnalyzer(final Analyzer analyzerToTest, final boolean invert) {
		this.analyzerToTest = analyzerToTest;
		this.invert = invert;
	}

	public static Rule test(final Analyzer analyzerToTest) {
		return new TestAnalyzer(analyzerToTest, false);
	}

	public static Rule testNot(final Analyzer analyzerToTest) {
		return not(analyzerToTest);
	}

	public static Rule not(final Analyzer analyzerToTest) {
		return new TestAnalyzer(analyzerToTest, true);
	}

	@Override
	public AnalysisResult analyze(final SourceCode input) {
		final AnalysisResult result = analyzerToTest.analyze(input);
		final boolean success = invert != result
                .wasSuccessful();
		if (success) {
			return SimpleAnalysisResult.success(TestAnalyzer.class, input,
					null, null);
		} else {
			return SimpleAnalysisResult.failed(TestAnalyzer.class,
					"underlying analyzer "
							+ analyzerToTest.getClass().getName()
							+ " was successful");
		}
	}
}
