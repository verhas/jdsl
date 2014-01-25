package com.javax0.jdsl.analyzers;

import static com.javax0.jdsl.analyzers.TestAnalyzer.not;
import static com.javax0.jdsl.analyzers.TestAnalyzer.test;

import org.junit.Assert;
import org.junit.Test;

import com.javax0.jdsl.executors.SimpleListExecutor;

public class TestAnalyzerTest {

	@SuppressWarnings("unused")
	@Test
	public void given_AFailingUnderlyingAnalyzer_when_CallingTestAnalyzer_then_TestAnalyzerAlsoFails() {
		final Analyzer failingAnalyzer;
		final Rule testAnalyzer;
		GIVEN: {
			testAnalyzer = test(new Analyzer() {
				@Override
				public AnalysisResult analyze(final SourceCode input) {
					return SimpleAnalysisResult.failed(null);
				}
			});
		}
		final AnalysisResult result;
		WHEN: {
			result = testAnalyzer.analyze(null);
		}
		THEN: {
			Assert.assertFalse(result.wasSuccessful());
		}
	}

	@SuppressWarnings("unused")
	@Test
	public void given_ASuccessfulUnderlyingAnalyzer_when_CallingTestAnalyzer_then_TestAnalyzerAlsoSuccessfulAndHasNullExecutorAndNullState() {
		final Analyzer failingAnalyzer;
		final Rule testAnalyzer;
		GIVEN: {
			testAnalyzer = test(new Analyzer() {
				@Override
				public AnalysisResult analyze(final SourceCode input) {
					return SimpleAnalysisResult.success(null, null,
							new SimpleListExecutor(), new State() {
							});
				}
			});
		}
		final AnalysisResult result;
		WHEN: {
			result = testAnalyzer.analyze(null);
		}
		THEN: {
			Assert.assertTrue(result.wasSuccessful());
			Assert.assertNull(result.getState());
			Assert.assertNull(result.getExecutor());
		}
	}

	@SuppressWarnings("unused")
	@Test
	public void given_AFailingUnderlyingAnalyzer_when_CallingTestNotAnalyzer_then_TestAnalyzerAlsoSuccessfulAndHasNullExecutorAndNullState() {
		final Analyzer failingAnalyzer;
		final Rule testAnalyzer;
		GIVEN: {
			testAnalyzer = not(new Analyzer() {
				@Override
				public AnalysisResult analyze(final SourceCode input) {
					return SimpleAnalysisResult.failed(null);
				}
			});
		}
		final AnalysisResult result;
		WHEN: {
			result = testAnalyzer.analyze(null);
		}
		THEN: {
			Assert.assertTrue(result.wasSuccessful());
			Assert.assertNull(result.getState());
			Assert.assertNull(result.getExecutor());
		}
	}

	@SuppressWarnings("unused")
	@Test
	public void given_ASuccessfulUnderlyingAnalyzer_when_CallingTestNotAnalyzer_then_TestAnalyzerFails() {
		final Analyzer failingAnalyzer;
		final Rule testAnalyzer;
		GIVEN: {
			testAnalyzer = not(new Analyzer() {
				@Override
				public AnalysisResult analyze(final SourceCode input) {
					return SimpleAnalysisResult.success(null, null,
							new SimpleListExecutor(), new State() {
							});
				}
			});
		}
		final AnalysisResult result;
		WHEN: {
			result = testAnalyzer.analyze(null);
		}
		THEN: {
			Assert.assertFalse(result.wasSuccessful());
		}
	}
}
