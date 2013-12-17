package com.javax0.jdsl;

import org.junit.Assert;
import org.junit.Test;

public class KeywordAnalyzerTest {

	private static enum Keywords {
		IF, THEN, ELSE, GOTO, ABSTRACT
	}

	@SuppressWarnings("unused")
	@Test
	public void given_InputStringAndKeyWordSet_when_CallingAnalysis_then_ReturnsTheKeyWords() {
		final KeywordAnalyzer ka;
		SourceCode sc;
		GIVEN: {
			ka = new KeywordAnalyzer();
			for (Keywords keyword : Keywords.values()) {
				ka.keyword(keyword.name(), keyword.ordinal());
			}
			sc = new StringSourceCode("IFTHENELSEGOTOABSTRACT");
		}

		final Integer[] tokens = new Integer[Keywords.values().length];
		WHEN: {
			int i = 0;
			while (sc.length() > 0) {
				final AnalysisResult result = ka.analyze(sc);
				final Executor executor = result.getExecutor();
				Assert.assertNotNull(executor);
				tokens[i++] = (Integer) executor.execute();
				sc = result.remainingSourceCode();
			}
		}
		THEN: {
			int i = 0;
			for (int token : tokens) {
				Assert.assertEquals(i++, token);
			}
		}
	}
}
