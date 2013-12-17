package com.javax0.jdsl.analyzers;

import static com.javax0.jdsl.analyzers.MockAnalyzerGeneratorUtil.successNTimesThenFail;

import org.junit.Assert;
import org.junit.Test;

import com.javax0.jdsl.analyzers.AnalysisResult;
import com.javax0.jdsl.analyzers.Analyzer;
import com.javax0.jdsl.analyzers.ListAnalyzer;
import com.javax0.jdsl.analyzers.SourceCode;
import com.javax0.jdsl.analyzers.StringSourceCode;
import com.javax0.jdsl.executors.ListExecutor;

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
