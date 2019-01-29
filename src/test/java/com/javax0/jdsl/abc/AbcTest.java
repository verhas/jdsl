package com.javax0.jdsl.abc;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.javax0.jdsl.GrammarDefinition;
import com.javax0.jdsl.analyzers.AnalysisResult;
import com.javax0.jdsl.analyzers.State;
import com.javax0.jdsl.analyzers.Analyzer;
import com.javax0.jdsl.analyzers.ListAnalysisState;
import com.javax0.jdsl.analyzers.ListAnalyzer;
import com.javax0.jdsl.analyzers.Rule;
import com.javax0.jdsl.analyzers.SimpleAnalysisResult;
import com.javax0.jdsl.analyzers.SourceCode;
import com.javax0.jdsl.analyzers.StringSourceCode;
import com.javax0.jdsl.executors.SimpleListExecutorFactory;

public class AbcTest {

	private class MyGrammar extends GrammarDefinition {

		private class EqLen implements Rule {
			private final ListAnalyzer listAnalyzer;

			private EqLen(final List<Rule> rules) {
				listAnalyzer = new ListAnalyzer(
						SimpleListExecutorFactory.INSTANCE);
				for (Rule rule : rules) {
					listAnalyzer.add(rule);
				}
			}

			@Override
			public AnalysisResult analyze(final SourceCode input) {
				final AnalysisResult result = listAnalyzer.analyze(input);
				if (result.wasSuccessful()) {
					final ListAnalysisState listState = (ListAnalysisState) result
							.getState();
					int previousLen = 0;
					boolean first = true;
					for (final State state : listState ) {
						final ListAnalysisState thisListState = (ListAnalysisState) state;
						final int thisLen = thisListState.size();
						if (!first) {
							if (previousLen != thisLen) {
								return SimpleAnalysisResult.failed(this
										.getClass());
							}
						}
						previousLen = thisLen;
						first = false;
					}

				}
				return result;
			}
		}

		@Override
		protected Analyzer define() {
			final Rule a = many(kw("a"));
			final Rule b = many(kw("b"));
			final Rule c = many(kw("c"));
			final List<Rule> rules = new LinkedList<>();
			rules.add(a);
			rules.add(b);
			rules.add(c);
			return new EqLen(rules);
		}

	}

	@Test
	public void testAaabbbccc() {
		final Analyzer analizer = new MyGrammar();
		final SourceCode sc = new StringSourceCode("aaabbbccc");
		final AnalysisResult result = analizer.analyze(sc);
		Assert.assertEquals(true, result.wasSuccessful());
	}

	@Test
	public void testAaabbccc() {
		final Analyzer analizer = new MyGrammar();
		final SourceCode sc = new StringSourceCode("aaabbccc");
		final AnalysisResult result = analizer.analyze(sc);
		Assert.assertEquals(false, result.wasSuccessful());
	}
}
