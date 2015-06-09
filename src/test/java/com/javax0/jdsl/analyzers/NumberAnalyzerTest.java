package com.javax0.jdsl.analyzers;

import static com.javax0.jdsl.analyzers.StringSourceCode.sourceCode;

import org.junit.Assert;
import org.junit.Test;

import com.javax0.jdsl.analyzers.Tuples.DoubleStringTuple;
import com.javax0.jdsl.analyzers.Tuples.LongStringTuple;
import com.javax0.jdsl.analyzers.terminals.NumberAnalyzer;
import com.javax0.jdsl.executors.Context;
import com.javax0.jdsl.executors.Executor;

public class NumberAnalyzerTest {
	private static final Context nullContext = null;

	private static LongStringTuple tuple(Long l, String s) {
		return new LongStringTuple(l, s);
	}

	private static DoubleStringTuple tuple(Double d, String s) {
		return new DoubleStringTuple(d, s);
	}

	private static LongStringTuple[] tuples(LongStringTuple... tuple) {
		return tuple;
	}

	private static DoubleStringTuple[] tuples(DoubleStringTuple... tuple) {
		return tuple;
	}

	private void parsesStringContainingLongAndExtraCharacter_ValueIsReturned(
			Long expected, String input) {
		final NumberAnalyzer ka = new NumberAnalyzer();
		final SourceCode sc = sourceCode(input);

		final AnalysisResult result = ka.analyze(sc);
		final Executor executor = result.getExecutor();
		Assert.assertNotNull(executor);
		final Long actual = (Long) executor.execute(nullContext);
		Assert.assertEquals(expected, actual);
	}

	private void analyzerEatsUpCharacters(String input, int remainderLength) {
		final NumberAnalyzer ka = new NumberAnalyzer();
		final SourceCode sc = sourceCode(input);
		final AnalysisResult result = ka.analyze(sc);
		final SourceCode remainder = result.remainingSourceCode();
		Assert.assertEquals(remainderLength, remainder.length());
	}

	LongStringTuple ltuples[] = tuples(tuple(1230L, "1230"),
			tuple(-1230L, "-1230"), tuple(+1230L, "+1230"));

	@Test
	public void stringContainingLongIsAnalyzed_ValueIsReturned() {
		for (LongStringTuple tuple : ltuples) {
			parsesStringContainingLongAndExtraCharacter_ValueIsReturned(
					tuple.l, tuple.s);
		}
	}

	@Test
	public void parserConsumesTotallyStringContainingLongOnly() {
		for (LongStringTuple tuple : ltuples) {
			analyzerEatsUpCharacters(tuple.s, 0);
		}
	}

	@Test
	public void parserDoesNotEatExtraCharacterAfterLong() {
		for (LongStringTuple tuple : ltuples) {
			analyzerEatsUpCharacters(tuple.s + "A", 1);
		}
	}

	@Test
	public void parsesStringContainingLongAndExtraCharacter_ValueIsReturned() {
		for (LongStringTuple tuple : ltuples) {
			parsesStringContainingLongAndExtraCharacter_ValueIsReturned(
					tuple.l, tuple.s + "A");
		}
	}

	private void stringContainingDoubleIsAnalyzed_ValueIsReturned(
			Double expected, String input) {
		final NumberAnalyzer ka = new NumberAnalyzer();
		final SourceCode sc = sourceCode(input);

		final AnalysisResult result = ka.analyze(sc);
		final Executor executor = result.getExecutor();
		Assert.assertNotNull(executor);
		final Double actual = (Double) executor.execute(nullContext);
		Assert.assertEquals(expected, actual);
	}

	DoubleStringTuple dtuples[] = tuples(tuple(1230.0, "1230.0"),
			tuple(1230.0, "1230."), tuple(-1230.0, "-1230.0"),
			tuple(-1230.0, "-123E1"), tuple(-12300.0, "-123E2"),
			tuple(-1230.0, "-123.E1"), tuple(-12300.0, "-123.E2"),
			tuple(-1230.0, "-123.0E1"), tuple(-12300.0, "-123.0E2"),
			tuple(-1230.0, "-123E+1"), tuple(-12300.0, "-123E+2"),
			tuple(-1230.0, "-123.E+1"), tuple(-12300.0, "-123.E+2"),
			tuple(-1230.0, "-123.0E+1"), tuple(-12300.0, "-123.0E+2"),
			tuple(-12.3, "-123E-1"), tuple(-1.23, "-123E-2"),
			tuple(-12.3, "-123.E-1"), tuple(-1.23, "-123.E-2"),
			tuple(-12.3, "-123.0E-1"), tuple(-1.230, "-123.0E-2"));

	@Test
	public void stringContainingDoubleIsAnalyzed_ValueIsReturned() {
		for (DoubleStringTuple tuple : dtuples) {
			stringContainingDoubleIsAnalyzed_ValueIsReturned(tuple.d, tuple.s);
		}
	}

	@Test
	public void stringContainingDoubleAndExtraCharacterIsAnalyzed_ValueIsReturned() {
		for (DoubleStringTuple tuple : dtuples) {
			stringContainingDoubleIsAnalyzed_ValueIsReturned(tuple.d, tuple.s
					+ "A");
		}
	}

	@Test
	public void parserConsumesTotallyStringContainingDoubleOnly() {
		for (DoubleStringTuple tuple : dtuples) {
			analyzerEatsUpCharacters(tuple.s, 0);
		}
	}

	@Test
	public void parserDoesNotEatExtraCharacterAfterDouble() {
		for (DoubleStringTuple tuple : dtuples) {
			analyzerEatsUpCharacters(tuple.s + "A", 1);
		}
	}

	private void numberParserFails(final String s) {
		final NumberAnalyzer ka = new NumberAnalyzer();
		final SourceCode sc = sourceCode("...");

		final AnalysisResult result = ka.analyze(sc);
		Assert.assertFalse(result.wasSuccessful());
	}

	@Test
	public void nonNumberStringIsNotParsedByNumberParser() {
		numberParserFails("...");
	}

	@Test
	public void emptyStringIsNotParsedByNumberParser() {
		numberParserFails("");
	}

	@Test
	public void aSignCharacterIsNotParsedAsNumber() {
		numberParserFails("+");
	}
}
