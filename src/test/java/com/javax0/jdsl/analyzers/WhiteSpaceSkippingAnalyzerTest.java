package com.javax0.jdsl.analyzers;

import org.junit.Assert;
import org.junit.Test;

import com.javax0.jdsl.analyzers.AnalysisResult;
import com.javax0.jdsl.analyzers.Analyzer;
import com.javax0.jdsl.analyzers.SourceCode;
import com.javax0.jdsl.analyzers.StringSourceCode;
import com.javax0.jdsl.analyzers.WhiteSpaceSkippingAnalyzer;

public class WhiteSpaceSkippingAnalyzerTest {

    @SuppressWarnings("unused")
    @Test
    public void given_EmptySourceCode_when_CalligWhiteSpaceAnalyzer_then_ShouldReturnTheSameSourceCode() {
        final SourceCode sc;
        final Analyzer whiteSpaceAnalyzer;
        GIVEN: {
            sc = StringSourceCode.EMPTY_SOURCE;
            whiteSpaceAnalyzer = new WhiteSpaceSkippingAnalyzer();
        }
        final AnalysisResult result;
        WHEN: {
            result = whiteSpaceAnalyzer.analyze(sc);
        }
        THEN: {
            Assert.assertTrue(result.wasSuccessful());
            Assert.assertEquals(sc, result.remainingSourceCode());
        }
    }

    @SuppressWarnings("unused")
    @Test
    public void given_NonSpaceSourceCode_when_CalligWhiteSpaceAnalyzer_then_ShouldReturnTheSameSourceCode() {
        final SourceCode sc;
        final Analyzer whiteSpaceAnalyzer;
        GIVEN: {
            sc = new StringSourceCode("Abraka Dekabra");
            whiteSpaceAnalyzer = new WhiteSpaceSkippingAnalyzer();
        }
        final AnalysisResult result;
        WHEN: {
            result = whiteSpaceAnalyzer.analyze(sc);
        }
        THEN: {
            Assert.assertTrue(result.wasSuccessful());
            Assert.assertEquals(sc, result.remainingSourceCode());
        }
    }

    @SuppressWarnings("unused")
    @Test
    public void given_SpaceOnlySourceCode_when_CalligWhiteSpaceAnalyzer_then_ShouldReturnEmptySourceCode() {
        final SourceCode sc;
        final Analyzer whiteSpaceAnalyzer;
        GIVEN: {
            sc = new StringSourceCode("    ");
            whiteSpaceAnalyzer = new WhiteSpaceSkippingAnalyzer();
        }
        final AnalysisResult result;
        WHEN: {
            result = whiteSpaceAnalyzer.analyze(sc);
        }
        THEN: {
            Assert.assertTrue(result.wasSuccessful());
            Assert.assertEquals(StringSourceCode.EMPTY_SOURCE, result.remainingSourceCode());
        }
    }

    @SuppressWarnings("unused")
    @Test
    public void given_SpaceStartingSourceCode_when_CalligWhiteSpaceAnalyzer_then_ShouldEatSpaceFromStart() {
        final SourceCode sc;
        final Analyzer whiteSpaceAnalyzer;
        final String someNonSpace = "Aaaarghhh.. .";
        GIVEN: {
            sc = new StringSourceCode("    " + someNonSpace);
            whiteSpaceAnalyzer = new WhiteSpaceSkippingAnalyzer();
        }
        final AnalysisResult result;
        WHEN: {
            result = whiteSpaceAnalyzer.analyze(sc);
        }
        THEN: {
            Assert.assertTrue(result.wasSuccessful());
            Assert.assertEquals(new StringSourceCode(someNonSpace), result.remainingSourceCode());
        }
    }
}
