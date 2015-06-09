package com.javax0.jdsl.analyzers;

import static com.javax0.jdsl.analyzers.MockAnalyzerGeneratorUtil.successNTimesThenFail;

import org.junit.Assert;
import org.junit.Test;

import com.javax0.jdsl.executors.Factory;
import com.javax0.jdsl.executors.ListExecutor;

public class ListAnalyzerTest {

	final static Factory<ListExecutor> NO_EXECUTOR = null;

	public void acceptsTheResultWhen_M_IsBiggerOrEqThan_N(int n, int m) {
		final ListAnalyzer analyzer;
		final Analyzer underlyingAnalyzer = successNTimesThenFail(m);
		analyzer = new ListAnalyzer(NO_EXECUTOR);
		for (int i = 0; i < n; i++) {
			analyzer.add(underlyingAnalyzer);
		}
		final SourceCode sc = new StringSourceCode("");
		final AnalysisResult result = analyzer.analyze(sc);
		Assert.assertEquals(n <= m, result.wasSuccessful());
	}

	private static final int MIN_MAX = 3;
	private static final int MAX_MAX = 10;

	@Test
	public void acceptsTheResultWhenThereIsNoFailingInTheList() {
		for (int min = 1; min <= MIN_MAX; min++) {
			for (int max = min; max <= MAX_MAX; max++) {
				acceptsTheResultWhen_M_IsBiggerOrEqThan_N(min, max);
			}
		}
	}
}
