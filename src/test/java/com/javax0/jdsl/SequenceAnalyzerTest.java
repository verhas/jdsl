package com.javax0.jdsl;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.OngoingStubbing;

public class SequenceAnalyzerTest {

    final static ListExecutor noExecutor = null;
    final static SourceCode noSourceCode = null;
    final static int optionalMin = 0;
    final static int optionalMax = 1;

    protected void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @SuppressWarnings("unused")
    @Test
    public void given_SequenceAnalyzerZeroOne_when_AnalyzingAndThereIsNone_then_AcceptsTheInput() {
        final Analyzer underlyingAnalyzer = Mockito.mock(Analyzer.class);
        final Analyzer analyzer = new SequenceAnalyzer(noExecutor, underlyingAnalyzer, optionalMin, optionalMax);
        final SourceCode sc = new StringSourceCode("");
        GIVEN: {
            Mockito.when(underlyingAnalyzer.analyze(sc)).thenReturn(SimpleAnalysisResult.failed());
        }
        final AnalysisResult result;
        WHEN: {
            result = analyzer.analyze(sc);
        }
        THEN: {
            Mockito.verify(underlyingAnalyzer).analyze(sc);
            Assert.assertTrue(result.wasSuccessful());
        }
    }

    @SuppressWarnings("unused")
    @Test
    public void given_SequenceAnalyzerZeroOne_when_AnalyzingAndThereIsNone_then_ReturnsTheInputUnchanged() {
        final Analyzer underlyingAnalyzer = Mockito.mock(Analyzer.class);
        final Analyzer analyzer = new SequenceAnalyzer(noExecutor, underlyingAnalyzer, optionalMin, optionalMax);
        final SourceCode sc = new StringSourceCode("");
        GIVEN: {
            Mockito.when(underlyingAnalyzer.analyze(sc)).thenReturn(SimpleAnalysisResult.failed());
        }
        final AnalysisResult result;
        WHEN: {
            result = analyzer.analyze(sc);
        }
        THEN: {
            Mockito.verify(underlyingAnalyzer).analyze(sc);
            Assert.assertEquals(sc, result.remainingSourceCode());
        }
    }

    @SuppressWarnings("unused")
    @Test
    public void given_SequenceAnalyzerOptional_when_AnalyzingAndThereIsOne_then_AcceptsTheResult() {
        final Analyzer underlyingAnalyzer = Mockito.mock(Analyzer.class);
        final Analyzer analyzer = new SequenceAnalyzer(noExecutor, underlyingAnalyzer, optionalMin, optionalMax);
        final SourceCode sc = new StringSourceCode("");
        final SourceCode modified = new StringSourceCode("");
        GIVEN: {
            Mockito.when(underlyingAnalyzer.analyze(sc)).thenReturn(SimpleAnalysisResult.success(modified, null));
        }
        final AnalysisResult result;
        WHEN: {
            result = analyzer.analyze(sc);
        }
        THEN: {
            Mockito.verify(underlyingAnalyzer).analyze(sc);
            Assert.assertTrue(result.wasSuccessful());
        }
    }

    @SuppressWarnings("unused")
    @Test
    public void given_SequenceAnalyzerOptional_when_AnalyzingAndThereIsOne_then_ReturnsTheInputModified() {
        final Analyzer underlyingAnalyzer = Mockito.mock(Analyzer.class);
        final Analyzer analyzer = new SequenceAnalyzer(noExecutor, underlyingAnalyzer, optionalMin, optionalMax);
        final SourceCode sc = new StringSourceCode("");
        final SourceCode modified = new StringSourceCode("");
        GIVEN: {
            Mockito.when(underlyingAnalyzer.analyze(sc)).thenReturn(SimpleAnalysisResult.success(modified, noExecutor));
        }
        final AnalysisResult result;
        WHEN: {
            result = analyzer.analyze(sc);
        }
        THEN: {
            Mockito.verify(underlyingAnalyzer).analyze(sc);
            Assert.assertEquals(modified, result.remainingSourceCode());
        }
    }

    private Analyzer successNTimesThenFail(int n) {
        Analyzer analyzer = Mockito.mock(Analyzer.class);
        OngoingStubbing<AnalysisResult> stub = Mockito.when(analyzer.analyze(Matchers.any(SourceCode.class)));
        for (int i = 0; i < n; i++) {
            stub = stub.thenReturn(SimpleAnalysisResult.success(noSourceCode, noExecutor));
        }
        stub.thenReturn(SimpleAnalysisResult.failed());
        return analyzer;
    }

    @SuppressWarnings("unused")
    public void testMinMaxAccepted(int min, int max, int loop) {
        final Analyzer underlyingAnalyzer;
        final Analyzer analyzer;
        final SourceCode sc;
        GIVEN: {
            underlyingAnalyzer = successNTimesThenFail(loop);
            analyzer = new SequenceAnalyzer(noExecutor, underlyingAnalyzer, min, max);
            sc = new StringSourceCode("");
        }
        final AnalysisResult result;
        WHEN: {
            result = analyzer.analyze(sc);
        }
        THEN: {
            Assert.assertTrue(result.wasSuccessful());
        }
    }

    private static final int MIN_MAX = 3;
    private static final int MAX_MAX = 10;

    @Test
    public void given_SequenceAnalyzen_when_AnalyzingAndThereIsSomeBetweenMinAndMax_then_AcceptsTheResult() {
        for (int min = 1; min <= MIN_MAX; min++) {
            for (int max = min; max <= MAX_MAX; max++) {
                for (int loop = min; loop <= max; loop++) {
                    testMinMaxAccepted(min, max, loop);
                }
            }
        }
    }
}
