package com.javax0.jdsl.analyzers;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class AlternativesAnalyzerTest {

	@SuppressWarnings("unused")
	private void given_AlternativesAnalyzerNFailing1SuccessFullMFailing_then_OnlyUpToSuccessFulIsCalled(
			int n, int m) {
		final AlternativesAnalyzer analyzer;
		final SourceCode sc = null;
		GIVEN: {
			analyzer = new AlternativesAnalyzer();
		}
		final AnalysisResult result;
		final Analyzer[] analyzers = new Analyzer[n + m];
		final Analyzer successfulAnalyzer;
		WHEN: {
			for (int i = 0; i < n; i++) {
				analyzers[i] = Mockito.mock(Analyzer.class);
				Mockito.when(
						analyzers[i].analyze(Mockito.any(SourceCode.class)))
						.thenReturn(SimpleAnalysisResult.failed(Analyzer.class));
				analyzer.add(analyzers[i]);
			}
			successfulAnalyzer = Mockito.mock(Analyzer.class);
			Mockito.when(
					successfulAnalyzer.analyze(Mockito.any(SourceCode.class)))
					.thenReturn(
							SimpleAnalysisResult.success(Analyzer.class, null,
									null));
			analyzer.add(successfulAnalyzer);
			for (int i = n; i < n + m; i++) {
				analyzers[i] = Mockito.mock(Analyzer.class);
				Mockito.when(
						analyzers[i].analyze(Mockito.any(SourceCode.class)))
						.thenReturn(SimpleAnalysisResult.failed(Analyzer.class));
				analyzer.add(analyzers[i]);
			}
			result = analyzer.analyze(sc);
		}
		THEN: {
			for (int i = 0; i < n; i++) {
				Mockito.verify(analyzers[i]).analyze(
						Mockito.any(SourceCode.class));
			}
			Mockito.verify(successfulAnalyzer).analyze(
					Mockito.any(SourceCode.class));
			for (int i = n; i < n + m; i++) {
				Mockito.verify(analyzers[i], Mockito.never()).analyze(
						Mockito.any(SourceCode.class));
			}
			Assert.assertTrue(result.wasSuccessful());
		}
	}

	@Test
	public void given_AnAlternativesAnalyzer_when_FirstAlternativeMatches_then_ReturnsSuccessful() {
		for (int n = 0; n < 3; n++) {
			for (int m = 0; m < 3; m++) {
				given_AlternativesAnalyzerNFailing1SuccessFullMFailing_then_OnlyUpToSuccessFulIsCalled(
						n, m);
			}
		}
	}

	@SuppressWarnings("unused")
	private void given_AlternativesAnalyzerNFailing_then_AllCalledAndFail(int n) {
		final AlternativesAnalyzer analyzer;
		final SourceCode sc = null;
		GIVEN: {
			analyzer = new AlternativesAnalyzer();
		}
		final AnalysisResult result;
		final Analyzer[] analyzers = new Analyzer[n];
		WHEN: {
			for (int i = 0; i < n; i++) {
				analyzers[i] = Mockito.mock(Analyzer.class);
				Mockito.when(
						analyzers[i].analyze(Mockito.any(SourceCode.class)))
						.thenReturn(SimpleAnalysisResult.failed(Analyzer.class));
				analyzer.add(analyzers[i]);
			}
			result = analyzer.analyze(sc);
		}
		THEN: {
			for (int i = 0; i < n; i++) {
				Mockito.verify(analyzers[i]).analyze(
						Mockito.any(SourceCode.class));
			}
			Assert.assertFalse(result.wasSuccessful());
		}
	}

	@Test
	public void given_AnAlternativesAnalyzer_when_NoneOfTheAlternativesMatch_then_ReturnsFailed() {
		for (int n = 0; n < 10; n++) {
			given_AlternativesAnalyzerNFailing_then_AllCalledAndFail(n);
		}
	}

	@SuppressWarnings("unused")
	private void given_AlternativesAnalyzerNFailing1NullAnalyzerBetween1SuccessFullMFailing_then_OnlyUpToSuccessFulIsCalled(
			int n, int m, int k) {
		final AlternativesAnalyzer analyzer;
		final SourceCode sc = null;
		GIVEN: {
			analyzer = new AlternativesAnalyzer();
		}
		final AnalysisResult result;
		final Analyzer[] analyzers = new Analyzer[n + m];
		final Analyzer successfulAnalyzer;
		WHEN: {
			for (int i = 0; i < n; i++) {
				if (i == k) {
					analyzers[i] = Mockito.mock(NullAnalyzer.class);
					Mockito.when(
							analyzers[i].analyze(Mockito.any(SourceCode.class)))
							.thenReturn(
									SimpleAnalysisResult.success(
											Analyzer.class, null, null, null));
				} else {
					analyzers[i] = Mockito.mock(Analyzer.class);
					Mockito.when(
							analyzers[i].analyze(Mockito.any(SourceCode.class)))
							.thenReturn(
									SimpleAnalysisResult.failed(Analyzer.class));
				}
				analyzer.add(analyzers[i]);
			}
			successfulAnalyzer = Mockito.mock(Analyzer.class);
			Mockito.when(
					successfulAnalyzer.analyze(Mockito.any(SourceCode.class)))
					.thenReturn(
							SimpleAnalysisResult.success(Analyzer.class, null,
									null));
			analyzer.add(successfulAnalyzer);
			for (int i = n; i < n + m; i++) {
				analyzers[i] = Mockito.mock(Analyzer.class);
				Mockito.when(
						analyzers[i].analyze(Mockito.any(SourceCode.class)))
						.thenReturn(SimpleAnalysisResult.failed(Analyzer.class));
				analyzer.add(analyzers[i]);
			}
			result = analyzer.analyze(sc);
		}
		THEN: {
			for (int i = 0; i < n; i++) {
				Mockito.verify(analyzers[i]).analyze(
						Mockito.any(SourceCode.class));
			}
			Mockito.verify(successfulAnalyzer).analyze(
					Mockito.any(SourceCode.class));
			for (int i = n; i < n + m; i++) {
				Mockito.verify(analyzers[i], Mockito.never()).analyze(
						Mockito.any(SourceCode.class));
			}
			Assert.assertTrue(result.wasSuccessful());
		}
	}

	@Test
	public void given_AnAlternativesAnalyzerWithNullAnalyzer_when_FirstAlternativeMatches_then_ReturnsSuccessful() {
		for (int n = 0; n < 3; n++) {
			for (int m = 0; m < 3; m++) {
				for (int k = 0; k <= n; k++) {
					given_AlternativesAnalyzerNFailing1NullAnalyzerBetween1SuccessFullMFailing_then_OnlyUpToSuccessFulIsCalled(
							n, m, k);
				}
			}
		}
	}
}
