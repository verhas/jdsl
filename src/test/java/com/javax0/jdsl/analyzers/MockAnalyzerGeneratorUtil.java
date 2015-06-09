package com.javax0.jdsl.analyzers;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import com.javax0.jdsl.executors.ListExecutor;

public class MockAnalyzerGeneratorUtil {
	private MockAnalyzerGeneratorUtil() {
		throw new RuntimeException(MockAnalyzerGeneratorUtil.class.toString()
				+ " is utility class, should not be instantiated");
	}

	private final static ListExecutor NO_EXECUTOR = null;
	private final static SourceCode NO_SOURCE_CODE = null;
	
	static Analyzer newFailingAnalyzer() {
		final Analyzer analyzer = mock(Analyzer.class);
		when(analyzer.analyze(any(SourceCode.class))).thenReturn(
				SimpleAnalysisResult.failed(Analyzer.class));
		return analyzer;
	}

	static Analyzer newSuccessfulAnalyzer() {
		return newSuccessfulAnalyzer(Analyzer.class);
	}

	static Analyzer newSuccessfulAnalyzer(Class<? extends Analyzer> klass) {
		final Analyzer analyzer = mock(klass);
		Mockito.when(analyzer.analyze(any(SourceCode.class))).thenReturn(
				SimpleAnalysisResult.success(Analyzer.class, null, null));
		return analyzer;
	}

	static void fillFailingAnalyzers(int start, int end, Analyzer[] analyzers,
			AlternativesAnalyzer alternativesAnalyzer) {
		for (int i = start; i < end; i++) {
			analyzers[i] = newFailingAnalyzer();
			alternativesAnalyzer.add(analyzers[i]);
		}
	}

	static void verifyAnalyzersWereInvoked(int start, int end,
			Analyzer[] analyzers) {
		for (int i = start; i < end; i++) {
			verify(analyzers[i]).analyze(any(SourceCode.class));
		}
	}

	static void verifyAnalyzersWereNotInvoked(int start, int end,
			Analyzer[] analyzers) {
		for (int i = start; i < end; i++) {
			verify(analyzers[i], never()).analyze(any(SourceCode.class));
		}
	}

	static void verifyAnalyzerWasInvoked(Analyzer analyzer) {
		verify(analyzer).analyze(any(SourceCode.class));
	}
	static Analyzer successNTimesThenFail(int n) {
		Analyzer analyzer = Mockito.mock(Analyzer.class);
		OngoingStubbing<AnalysisResult> stub = Mockito.when(analyzer
				.analyze(Matchers.any(SourceCode.class)));
		for (int i = 0; i < n; i++) {
			stub = stub.thenReturn(SimpleAnalysisResult.success(Analyzer.class,
					NO_SOURCE_CODE, NO_EXECUTOR));
		}
		stub.thenReturn(SimpleAnalysisResult.failed(Analyzer.class));
		return analyzer;
	}
}
