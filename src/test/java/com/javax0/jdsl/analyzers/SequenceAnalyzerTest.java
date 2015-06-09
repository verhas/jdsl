package com.javax0.jdsl.analyzers;

import static com.javax0.jdsl.analyzers.MockAnalyzerGeneratorUtil.newFailingAnalyzer;
import static com.javax0.jdsl.analyzers.MockAnalyzerGeneratorUtil.successNTimesThenFail;
import static com.javax0.jdsl.analyzers.SequenceAnalyzer.analyzer;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.javax0.jdsl.executors.Factory;
import com.javax0.jdsl.executors.ListExecutor;

public class SequenceAnalyzerTest {

	private final static Factory<ListExecutor> noExecutorFactory = null;
	private final static ListExecutor noExecutor = null;
	private final static int optionalMin = 0;
	private final static int optionalMax = 1;
	private final static SourceCode MODIFIED_INPUT = new StringSourceCode("");
	private final static SourceCode SOURCE_CODE = new StringSourceCode("");
	private static final int MIN_MAX = 3;
	private static final int MAX_MAX = 10;

	@Test
	public void given_SequenceAnalyzerZeroOne_when_AnalyzingAndThereIsNone_then_AcceptsTheInput() {
		final Analyzer underlyingAnalyzer = newFailingAnalyzer();
		final Analyzer analyzer = analyzer(noExecutorFactory,
				underlyingAnalyzer, optionalMin, optionalMax);
		final AnalysisResult result = analyzer.analyze(SOURCE_CODE);
		verify(underlyingAnalyzer).analyze(SOURCE_CODE);
		assertTrue(result.wasSuccessful());
	}

	@Test
	public void given_SequenceAnalyzerZeroOne_when_AnalyzingAndThereIsNone_then_ReturnsTheInputUnchanged() {
		final Analyzer underlyingAnalyzer = newFailingAnalyzer();
		final Analyzer analyzer = analyzer(noExecutorFactory,
				underlyingAnalyzer, optionalMin, optionalMax);
		final AnalysisResult result = analyzer.analyze(SOURCE_CODE);
		verify(underlyingAnalyzer).analyze(SOURCE_CODE);
		assertEquals(SOURCE_CODE, result.remainingSourceCode());
	}

	@Test
	public void oneElementSequenceIsAccepted() {
		final AnalysisResult result = whenThereIsAOneElementSequence();
		assertTrue(result.wasSuccessful());
	}

	@Test
	public void returnsModifiedInputForOneElementSequence() {
		final AnalysisResult result = whenThereIsAOneElementSequence();
		Assert.assertEquals(MODIFIED_INPUT, result.remainingSourceCode());
	}

	private AnalysisResult whenThereIsAOneElementSequence() {
		final Analyzer underlyingAnalyzer = Mockito.mock(Analyzer.class);
		when(underlyingAnalyzer.analyze(SOURCE_CODE)).thenReturn(
				SimpleAnalysisResult.success(Analyzer.class, MODIFIED_INPUT,
						noExecutor));
		final Analyzer analyzer = analyzer(noExecutorFactory,
				underlyingAnalyzer, optionalMin, optionalMax);
		final AnalysisResult result = analyzer.analyze(SOURCE_CODE);
		verify(underlyingAnalyzer).analyze(SOURCE_CODE);
		return result;
	}

	private void testMinMaxAccepted(int min, int max, int loop) {
		final Analyzer underlyingAnalyzer;
		final Analyzer analyzer;
		underlyingAnalyzer = successNTimesThenFail(loop);
		analyzer = analyzer(noExecutorFactory, underlyingAnalyzer, min, max);
		final AnalysisResult result = analyzer.analyze(SOURCE_CODE);
		assertTrue(result.wasSuccessful());
	}

	@Test
	public void successfulForSequenceHavingElementsBetweenMinAndMaxNumbers() {
		for (int min = 1; min <= MIN_MAX; min++) {
			for (int max = min; max <= MAX_MAX; max++) {
				for (int loop = min; loop <= max; loop++) {
					testMinMaxAccepted(min, max, loop);
				}
			}
		}
	}
}
