package com.javax0.jdsl.analyzers;

import org.junit.Assert;
import org.junit.Test;

import static com.javax0.jdsl.analyzers.terminals.StringAnalyzer.string;

public class StringAnalyzerTest {

	private static final String DQ = "\"";
	private static final String PQ = "'";
	private static final String DQ3 = "\"\"\"";
	private static final String PQ3 = "'''";
	private static final String[] separators = new String[] { DQ, PQ, DQ3, PQ3 };
	private static final String[] mlseparators = new String[] { DQ3, PQ3 };

	private void testStringSuccessful(String testString) {
		testStringSuccessful(testString, testString);
	}

	private void testStringSuccessful(String testString, String matchString) {
		testStringSuccessful(testString, matchString, separators);
	}

	private void testMlStringSuccessful(String testString) {
		testMlStringSuccessful(testString, testString);
	}

	private void testMlStringSuccessful(String testString, String matchString) {
		testStringSuccessful(testString, matchString, mlseparators);
	}

	private void testStringSuccessful(String testString, String matchString,
			String[] separators) {
		SourceCode sc;
		final Analyzer analyzer = string();
		AnalysisResult result;

		for (String separator : separators) {
			sc = new StringSourceCode(separator + testString + separator + ".");
			result = analyzer.analyze(sc);
			Assert.assertTrue(result.wasSuccessful());
			Assert.assertEquals(matchString, (String) result.getExecutor()
					.execute(null));
			Assert.assertEquals(1, result.remainingSourceCode().length());
			Assert.assertEquals('.', result.remainingSourceCode().charAt(0));
		}
	}

	@Test
	public void given_AString_when_Analyzing_then_TheResultIsOk() {
		testStringSuccessful("ABC");
		testStringSuccessful("A\\tC", "A\tC");
		testStringSuccessful("A\\nC", "A\nC");
		testStringSuccessful("A\\rC", "A\rC");
		testMlStringSuccessful("A\nBC");
	}

	@Test
	public void given_MultilineStringWithEmbeddedSingleSeparator_when_Analyzing_then_Success() {
		testMlStringSuccessful("A\"BC");
		testMlStringSuccessful("A'BC");
	}

	@Test
	public void given_MultilineString_when_Analyzing_then_Success() {
		testMlStringSuccessful("AB\nC");
		testMlStringSuccessful("AB\\\nC","AB\nC");
	}

	@Test
	public void given_StringWithEscapedEmbeddedSingleSeparator_when_Analyzing_then_Success() {
		testStringSuccessful("A\\\"BC", "A\"BC");
		testStringSuccessful("A\\'BC", "A'BC");
	}

	@Test
	public void given_StringWithNonEscapedEmbeddedSingleSeparator_when_Analyzing_then_Success() {
		testStringSuccessful("A\"BC", "A\"BC", new String[] { PQ });
		testStringSuccessful("A'BC", "A'BC", new String[] { DQ });
	}

	@Test
	public void given_EmptyString_when_Analyzing_then_Failure() {
		SourceCode sc = new StringSourceCode("");
		AnalysisResult result = string().analyze(sc);
		Assert.assertFalse(result.wasSuccessful());
	}

	@Test
	public void given_MultiLineStringWithSingleTerminator_when_Analyzing_then_Failure() {
		SourceCode sc = new StringSourceCode(DQ + "\n" + DQ);
		AnalysisResult result = string().analyze(sc);
		Assert.assertFalse(result.wasSuccessful());
	}

	private void testUnterminatedString(String unterminated) {
		SourceCode sc;
		final Analyzer analyzer = string();
		AnalysisResult result;

		for (String separator : separators) {
			sc = new StringSourceCode(separator + unterminated);
			result = analyzer.analyze(sc);
			Assert.assertFalse(result.wasSuccessful());
		}
	}

	@Test
	public void given_UnterminatedString_when_Analyzing_then_Failure() {
		testUnterminatedString("ABC");
		testUnterminatedString("A\\\"BC");
		testUnterminatedString("A\\\\BC");
		testUnterminatedString("A\\'BC");
	}
}
