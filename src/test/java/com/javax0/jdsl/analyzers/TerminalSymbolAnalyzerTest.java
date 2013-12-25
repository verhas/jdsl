package com.javax0.jdsl.analyzers;

import org.junit.Assert;
import org.junit.Test;

import com.javax0.jdsl.analyzers.terminals.TerminalSymbolAnalyzer;

public class TerminalSymbolAnalyzerTest {

	@SuppressWarnings("unused")
	@Test
	public void given_InputStringAndKeyword_when_CallingAnalysis_then_ReturnsSuccess() {
		final TerminalSymbolAnalyzer ka;
		SourceCode sc;
		GIVEN: {
			ka = new TerminalSymbolAnalyzer("KEYWORD");
			sc = new StringSourceCode("KEYWORD");
		}

		final AnalysisResult result;
		WHEN: {
			result = ka.analyze(sc);
		}
		THEN: {
			Assert.assertTrue(result.wasSuccessful());
		}
	}
	
	@SuppressWarnings("unused")
	@Test
	public void given_InputStringAndKeywordANdSomeExtraCharacters_when_CallingAnalysis_then_ReturnsSuccess() {
		final TerminalSymbolAnalyzer ka;
		SourceCode sc;
		GIVEN: {
			ka = new TerminalSymbolAnalyzer("KEYWORD");
			sc = new StringSourceCode("KEYWORDHAHAHA");
		}

		final AnalysisResult result;
		WHEN: {
			result = ka.analyze(sc);
		}
		THEN: {
			Assert.assertTrue(result.wasSuccessful());
		}
	}
	
	@SuppressWarnings("unused")
	@Test
	public void given_NoKeywordStartingInputStringAndKeyword_when_CallingAnalysis_then_ReturnsFailure() {
		final TerminalSymbolAnalyzer ka;
		SourceCode sc;
		GIVEN: {
			ka = new TerminalSymbolAnalyzer("KEYWORD");
			sc = new StringSourceCode("HKEYWORD");
		}

		final AnalysisResult result;
		WHEN: {
			result = ka.analyze(sc);
		}
		THEN: {
			Assert.assertFalse(result.wasSuccessful());
		}
	}
	
	
	@SuppressWarnings("unused")
	@Test
	public void given_ShortInputStringAndKeyword_when_CallingAnalysis_then_ReturnsFailure() {
		final TerminalSymbolAnalyzer ka;
		SourceCode sc;
		GIVEN: {
			ka = new TerminalSymbolAnalyzer("KEYWORD");
			sc = new StringSourceCode("KEYWOR");
		}

		final AnalysisResult result;
		WHEN: {
			result = ka.analyze(sc);
		}
		THEN: {
			Assert.assertFalse(result.wasSuccessful());
		}
	}
}
