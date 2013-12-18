package com.javax0.jdsl.analyzers;

import org.junit.Assert;
import org.junit.Test;

public class KeywordAnalyzerTest {

	@SuppressWarnings("unused")
	@Test
	public void given_InputStringAndKeyword_when_CallingAnalysis_then_ReturnsSuccess() {
		final KeywordAnalyzer ka;
		SourceCode sc;
		GIVEN: {
			ka = new KeywordAnalyzer("KEYWORD");
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
		final KeywordAnalyzer ka;
		SourceCode sc;
		GIVEN: {
			ka = new KeywordAnalyzer("KEYWORD");
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
		final KeywordAnalyzer ka;
		SourceCode sc;
		GIVEN: {
			ka = new KeywordAnalyzer("KEYWORD");
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
		final KeywordAnalyzer ka;
		SourceCode sc;
		GIVEN: {
			ka = new KeywordAnalyzer("KEYWORD");
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
