package com.javax0.jdsl;

import org.junit.Assert;
import org.junit.Test;

public class NumberAnalyzerTest {

    @SuppressWarnings("unused")
    private void testLongReturnsValue(Long expected, String input) {
        final NumberAnalyzer ka;
        final SourceCode sc;
        GIVEN: {
            ka = new NumberAnalyzer();
            sc = new StringSourceCode(input);
        }

        final Long actual;
        WHEN: {
            final AnalysisResult result = ka.analyze(sc);
            final Executor executor = result.getExecutor();
            Assert.assertNotNull(executor);
            actual = (Long) executor.execute();
        }
        THEN: {
            Assert.assertEquals(expected, actual);
        }
    }

    @SuppressWarnings("unused")
    private void testAnalyzerEatsUpCharacters(String input, int remainderLength) {
        final NumberAnalyzer ka;
        final SourceCode sc;
        GIVEN: {
            ka = new NumberAnalyzer();
            sc = new StringSourceCode(input);
        }
        final SourceCode remainder;
        WHEN: {
            final AnalysisResult result = ka.analyze(sc);
            remainder = result.remainingSourceCode();
        }
        THEN: {
            Assert.assertEquals(remainderLength, remainder.length());
        }
    }

    private static class LongTuple {
        private final Long l;
        private final String s;

        LongTuple(Long l, String s) {
            this.l = l;
            this.s = s;
        }
    }

    LongTuple ltuples[] = new LongTuple[] { new LongTuple(1230L, "1230"), new LongTuple(-1230L, "-1230"), new LongTuple(+1230L, "+1230"), };

    @Test
    public void given_IntegerNumberAtTheEndOfTheInput_when_CallingAnalysis_then_ReturnsTheLongValue() {
        for (LongTuple tuple : ltuples) {
            testLongReturnsValue(tuple.l, tuple.s);
        }
    }

    @Test
    public void given_IntegerNumberAtTheEndOfTheInput_when_CallingAnalysis_then_ItEatsSourceTotally() {
        for (LongTuple tuple : ltuples) {
            testAnalyzerEatsUpCharacters(tuple.s, 0);
        }
    }

    @Test
    public void given_IntegerNumberBeforeTheEndOfTheInput_when_CallingAnalysis_then_ItEatsSourceUntilTerminatingSymbol() {
        for (LongTuple tuple : ltuples) {
            testAnalyzerEatsUpCharacters(tuple.s + "A", 1);
        }
    }

    @Test
    public void given_IntegerNumberBeforeTheEndOfTheInput_when_CallingAnalysis_then_ReturnsTheLongValue() {
        for (LongTuple tuple : ltuples) {
            testLongReturnsValue(tuple.l, tuple.s + "A");
        }
    }

    @SuppressWarnings("unused")
    private void testDoubleReturnsValue(Double expected, String input) {
        final NumberAnalyzer ka;
        final SourceCode sc;
        GIVEN: {
            ka = new NumberAnalyzer();
            sc = new StringSourceCode(input);
        }

        final Double actual;
        WHEN: {
            final AnalysisResult result = ka.analyze(sc);
            final Executor executor = result.getExecutor();
            Assert.assertNotNull(executor);
            actual = (Double) executor.execute();
        }
        THEN: {
            Assert.assertEquals(expected, actual);
        }
    }

    private static class DoubleTuple {
        private final Double d;
        private final String s;

        DoubleTuple(Double d, String s) {
            this.d = d;
            this.s = s;
        }
    }

    DoubleTuple dtuples[] = new DoubleTuple[] { new DoubleTuple(1230.0, "1230.0"), new DoubleTuple(1230.0, "1230."), new DoubleTuple(-1230.0, "-1230.0"),
                    new DoubleTuple(-1230.0, "-123E1"), new DoubleTuple(-12300.0, "-123E2"), new DoubleTuple(-1230.0, "-123.E1"),
                    new DoubleTuple(-12300.0, "-123.E2"), new DoubleTuple(-1230.0, "-123.0E1"), new DoubleTuple(-12300.0, "-123.0E2"),
                    new DoubleTuple(-1230.0, "-123E+1"), new DoubleTuple(-12300.0, "-123E+2"), new DoubleTuple(-1230.0, "-123.E+1"),
                    new DoubleTuple(-12300.0, "-123.E+2"), new DoubleTuple(-1230.0, "-123.0E+1"), new DoubleTuple(-12300.0, "-123.0E+2"),
                    new DoubleTuple(-12.3, "-123E-1"), new DoubleTuple(-1.23, "-123E-2"), new DoubleTuple(-12.3, "-123.E-1"),
                    new DoubleTuple(-1.23, "-123.E-2"), new DoubleTuple(-12.3, "-123.0E-1"), new DoubleTuple(-1.230, "-123.0E-2"), };

    @Test
    public void given_DoubleNumberAtTheEndOfTheInput_when_CallingAnalysis_then_ReturnsTheDoubleValue() {
        for (DoubleTuple tuple : dtuples) {
            testDoubleReturnsValue(tuple.d, tuple.s);
        }
    }

    @Test
    public void given_DoubleNumberBeforeTheEndOfTheInput_when_CallingAnalysis_then_ReturnsTheDoubleValue() {
        for (DoubleTuple tuple : dtuples) {
            testDoubleReturnsValue(tuple.d, tuple.s + "A");
        }
    }

    @Test
    public void given_DoubleNumberAtTheEndOfTheInput_when_CallingAnalysis_then_ItEatsSourceTotally() {
        for (DoubleTuple tuple : dtuples) {
            testAnalyzerEatsUpCharacters(tuple.s, 0);
        }
    }

    @Test
    public void given_DoubleNumberBeforeTheEndOfTheInput_when_CallingAnalysis_then_ItEatsSourceUntilTerminatingSymbol() {
        for (DoubleTuple tuple : dtuples) {
            testAnalyzerEatsUpCharacters(tuple.s + "A", 1);
        }
    }

    @SuppressWarnings("unused")
    @Test
    public void given_SomeFalseString_when_CallingAnalysis_then_Fails() {
        final NumberAnalyzer ka;
        final SourceCode sc;
        GIVEN: {
            ka = new NumberAnalyzer();
            sc = new StringSourceCode("...");
        }

        final Double actual;
        final AnalysisResult result;
        WHEN: {
            result = ka.analyze(sc);
        }
        THEN: {
            Assert.assertFalse(result.wasSuccessful());
        }
    }
}
