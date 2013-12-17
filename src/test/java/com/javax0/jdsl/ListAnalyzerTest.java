package com.javax0.jdsl;

import static com.javax0.jdsl.MockAnalyzerGeneratorUtil.successNTimesThenFail;

import org.junit.Assert;
import org.junit.Test;

public class ListAnalyzerTest {

    final static ListExecutor noExecutor = null;
    final static SourceCode noSourceCode = null;

    @SuppressWarnings("unused")
    public void testListAnalyzerGivenListOfNFailAtM(int n, int m) {
        final Analyzer underlyingAnalyzer;
        final ListAnalyzer analyzer;
        final SourceCode sc;
        GIVEN: {
            underlyingAnalyzer = successNTimesThenFail(m);
            analyzer = new ListAnalyzer(noExecutor);
            for (int i = 0; i < n; i++) {
                analyzer.add(underlyingAnalyzer);
            }
            sc = new StringSourceCode("");
        }
        final AnalysisResult result;
        WHEN: {
            result = analyzer.analyze(sc);
        }
        THEN: {
            if (n <= m) {
                Assert.assertTrue(result.wasSuccessful());
            } else {
                Assert.assertFalse(result.wasSuccessful());
            }
        }
    }

    private static final int MIN_MAX = 3;
    private static final int MAX_MAX = 10;

    @Test
    public void given_SequenceAnalyzen_when_AnalyzingAndThereIsSomeBetweenMinAndMax_then_AcceptsTheResult() {
        for (int min = 1; min <= MIN_MAX; min++) {
            for (int max = min; max <= MAX_MAX; max++) {
                testListAnalyzerGivenListOfNFailAtM(min, max);
            }
        }
    }
}
