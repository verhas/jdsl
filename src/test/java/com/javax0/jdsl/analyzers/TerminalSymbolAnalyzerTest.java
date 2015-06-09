package com.javax0.jdsl.analyzers;

import static com.javax0.jdsl.analyzers.StringSourceCode.sourceCode;
import static com.javax0.jdsl.analyzers.terminals.TerminalSymbolAnalyzer.analyzer;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TerminalSymbolAnalyzerTest {

	@Test
	public void analysesKeywordSource() {
		assertTrue(analyzer("KEYWORD").analyze(sourceCode("KEYWORD"))
				.wasSuccessful());
	}

	@Test
	public void analysesSourceStartingWithKeyword() {

		assertTrue(analyzer("KEYWORD").analyze(sourceCode("KEYWORDHAHAHA"))
				.wasSuccessful());
	}

	@Test
	public void failsSourceContainingButNotStartingWithTheKeyword() {
		assertFalse(analyzer("KEYWORD").analyze(sourceCode("HKEYWORD"))
				.wasSuccessful());
	}

	@Test
	public void failsSourceContainingOnlyPrefixOfKeyword() {
		assertFalse(analyzer("KEYWORD").analyze(sourceCode("KEYWOR"))
				.wasSuccessful());
	}
}
