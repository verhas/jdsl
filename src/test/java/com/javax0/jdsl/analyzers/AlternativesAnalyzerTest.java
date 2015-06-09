package com.javax0.jdsl.analyzers;

import static com.javax0.jdsl.analyzers.MockAnalyzerGeneratorUtil.fillFailingAnalyzers;
import static com.javax0.jdsl.analyzers.MockAnalyzerGeneratorUtil.newFailingAnalyzer;
import static com.javax0.jdsl.analyzers.MockAnalyzerGeneratorUtil.newSuccessfulAnalyzer;
import static com.javax0.jdsl.analyzers.MockAnalyzerGeneratorUtil.verifyAnalyzerWasInvoked;
import static com.javax0.jdsl.analyzers.MockAnalyzerGeneratorUtil.verifyAnalyzersWereInvoked;
import static com.javax0.jdsl.analyzers.MockAnalyzerGeneratorUtil.verifyAnalyzersWereNotInvoked;

import org.junit.Assert;
import org.junit.Test;

public class AlternativesAnalyzerTest {



	private void callsUntilTheFirstSuccesfulSubanalyzer(int n, int m) {
		final SourceCode sc = null;
		final AlternativesAnalyzer alternativesAnalyzer = new AlternativesAnalyzer();
		final Analyzer[] analyzers = new Analyzer[n + m];
		fillFailingAnalyzers(0, n, analyzers, alternativesAnalyzer);
		final Analyzer successfulAnalyzer = newSuccessfulAnalyzer();
		alternativesAnalyzer.add(successfulAnalyzer);
		fillFailingAnalyzers(n, n + m, analyzers, alternativesAnalyzer);

		final AnalysisResult result = alternativesAnalyzer.analyze(sc);

		verifyAnalyzersWereInvoked(0, n, analyzers);
		verifyAnalyzerWasInvoked(successfulAnalyzer);
		verifyAnalyzersWereNotInvoked(n, n + m, analyzers);
		Assert.assertTrue(result.wasSuccessful());
	}

	@Test
	public void callsUntilTheFirstSuccesfulSubanalyzer() {
		for (int n = 0; n < 3; n++) {
			for (int m = 0; m < 3; m++) {
				callsUntilTheFirstSuccesfulSubanalyzer(n, m);
			}
		}
	}

	private void allFailingSubanalyzersAreInvoked(int n) {
		final SourceCode sc = null;
		final AlternativesAnalyzer alternativesAnalyzer = new AlternativesAnalyzer();
		final Analyzer[] analyzers = new Analyzer[n];
		fillFailingAnalyzers(0, n, analyzers, alternativesAnalyzer);

		final AnalysisResult result = alternativesAnalyzer.analyze(sc);
		verifyAnalyzersWereInvoked(0, n, analyzers);
		Assert.assertFalse(result.wasSuccessful());
	}

	@Test
	public void allFailingSubanalyzersAreInvoked() {
		for (int n = 0; n < 10; n++) {
			allFailingSubanalyzersAreInvoked(n);
		}
	}

	private void callsUntilTheFirstSuccesfulSubanalyzerEvenAfterANullAnalyzer(
			int n, int m, int k) {
		final SourceCode sc = null;
		final AlternativesAnalyzer alternativesAnalyzer = new AlternativesAnalyzer();
		final Analyzer[] analyzers = new Analyzer[n + m];
		for (int i = 0; i < n; i++) {
			if (i == k) {
				analyzers[i] = newSuccessfulAnalyzer(NullAnalyzer.class);
			} else {
				analyzers[i] = newFailingAnalyzer();
			}
			alternativesAnalyzer.add(analyzers[i]);
		}
		final Analyzer successfulAnalyzer = newSuccessfulAnalyzer();
		alternativesAnalyzer.add(successfulAnalyzer);
		for (int i = n; i < n + m; i++) {
			analyzers[i] = newFailingAnalyzer();
			alternativesAnalyzer.add(analyzers[i]);
		}
		final AnalysisResult result = alternativesAnalyzer.analyze(sc);

		verifyAnalyzersWereInvoked(0, n, analyzers);
		verifyAnalyzerWasInvoked(successfulAnalyzer);
		verifyAnalyzersWereNotInvoked(n, n + m, analyzers);
		Assert.assertTrue(result.wasSuccessful());
	}

	@Test
	public void callsUntilTheFirstSuccesfulSubanalyzerEvenAfterANullAnalyzer() {
		for (int n = 0; n < 3; n++) {
			for (int m = 0; m < 3; m++) {
				for (int k = 0; k <= n; k++) {
					callsUntilTheFirstSuccesfulSubanalyzerEvenAfterANullAnalyzer(
							n, m, k);
				}
			}
		}
	}
}
