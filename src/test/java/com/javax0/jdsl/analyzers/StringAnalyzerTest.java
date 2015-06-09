package com.javax0.jdsl.analyzers;

import static com.javax0.jdsl.analyzers.StringSourceCode.sourceCode;
import static com.javax0.jdsl.analyzers.terminals.StringAnalyzer.string;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class StringAnalyzerTest {

	private static final String DQ = "\"";
	private static final String PQ = "'";
	private static final String DQ3 = "\"\"\"";
	private static final String PQ3 = "'''";
	private static final String[] separators = new String[] { DQ, PQ, DQ3, PQ3 };
	private static final String[] mlseparators = new String[] { DQ3, PQ3 };

	private void simpleString(String testString) {
		simpleString(testString, testString);
	}

	private void simpleString(String testString, String matchString) {
		testString(testString, matchString, separators);
	}

	private void mlString(String testString) {
		mlString(testString, testString);
	}

	private void mlString(String testString, String matchString) {
		testString(testString, matchString, mlseparators);
	}

	/**
	 * Test the string with possible string separators. Tests assert that the
	 * analysis was successful, the resulting string is the one passed in the
	 * argument, the remaining source code contains the rest of the input.
	 * 
	 * @param testString
	 *            the string to test without the opening and closing separators.
	 *            Only the characters that are between the opening and closing
	 *            ", or ' etc.
	 * @param matchString
	 *            the string that the analysis should result. This is already
	 *            with unescaped \r, \t etc.
	 * @param separators
	 *            the separators that each should be used for testing. Test are
	 *            executed for each separator. These are appended before and
	 *            after the testString before passing to the analyser
	 */
	private void testString(String testString, String matchString,
			String[] separators) {

		for (String separator : separators) {
			final AnalysisResult result = string().analyze(
					sourceCode(separator + testString + separator + "."));
			assertTrue(result.wasSuccessful());
			assertEquals(matchString,
					(String) result.getExecutor().execute(null));
			assertEquals(1, result.remainingSourceCode().length());
			assertEquals('.', result.remainingSourceCode().charAt(0));
		}
	}

	private void failUnterminatedString(String unterminated) {
		for (String separator : separators) {
			assertFalse(string().analyze(sourceCode(separator + unterminated))
					.wasSuccessful());
		}
	}

	@Test
	public void analysesSimpleString() {
		simpleString("ABC");
	}

	@Test
	public void analysesStringWithTab() {
		simpleString("A\\tC", "A\tC");
	}

	@Test
	public void analysesStringWithNewLine() {
		simpleString("A\\nC", "A\nC");
	}

	@Test
	public void analysesStringWithCarrigeReturn() {
		simpleString("A\\rC", "A\rC");
	}

	@Test
	public void analysesSimpleMultilineString() {
		mlString("A\nBC");
	}

	@Test
	public void analysesMultilineStringWithQuote() {
		mlString("A\"BC");
	}

	@Test
	public void analysesMultilineStringWithAphostrophe() {
		mlString("A'BC");
	}

	@Test
	public void analysesMultiLineStringWithNewLine() {
		mlString("AB\nC");
	}

	@Test
	public void analysesMultiLineStringWithEscapedNewLine() {
		mlString("AB\\\nC", "AB\nC");
	}

	@Test
	public void analyseStringWithEscapedQuote() {
		simpleString("A\\\"BC", "A\"BC");
	}

	@Test
	public void analyseStringWithEscapedApostrophe() {
		simpleString("A\\'BC", "A'BC");
	}

	@Test
	public void analyseApostrophedStringWithQuote() {
		testString("A\"BC", "A\"BC", new String[] { PQ });
	}

	@Test
	public void analyseQuotedStringWithApostrophe() {
		testString("A'BC", "A'BC", new String[] { DQ });
	}

	@Test
	public void emptyWithoutSeparatorsIsNotAString() {
		SourceCode sc = sourceCode("");
		AnalysisResult result = string().analyze(sc);
		assertFalse(result.wasSuccessful());
	}

	@Test
	public void stringSingleQuotedContainingNewLineFails() {
		SourceCode sc = sourceCode(DQ + "\n" + DQ);
		AnalysisResult result = string().analyze(sc);
		assertFalse(result.wasSuccessful());
	}

	@Test
	public void unterminatedStringFails() {
		failUnterminatedString("ABC");
	}

	@Test
	public void unterminatedStringWithEscapedQuoteFails() {
		failUnterminatedString("A\\\"BC");
	}

	@Test
	public void unterminatedStringWithEscapedBackslashFails() {
		failUnterminatedString("A\\\\BC");
	}

	@Test
	public void unterminatedStringWithEscapedApostropheFails() {
		failUnterminatedString("A\\'BC");
	}
}
