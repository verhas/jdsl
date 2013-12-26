package com.javax0.jdsl;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javax0.jdsl.analyzers.AnalysisResult;
import com.javax0.jdsl.analyzers.Analyzer;
import com.javax0.jdsl.analyzers.PassThroughAnalyzer;
import com.javax0.jdsl.analyzers.StringSourceCode;
import com.javax0.jdsl.executors.Executor;
import com.javax0.jdsl.executors.Factory;
import com.javax0.jdsl.executors.ListExecutor;
import com.javax0.jdsl.log.NullReporter;
import com.javax0.jdsl.log.ReporterFactory;

public class GrammarTest {
	private static final Logger LOG = LoggerFactory
			.getLogger(GrammarTest.class);

	private Analyzer defineMyGrammar() {
		final Analyzer myGrammar = new GrammarDefinition() {
			@Override
			void define() {
				ReporterFactory.setReporter(new NullReporter());
				skipSpaces();
				final PassThroughAnalyzer expression = definedLater();
				final Analyzer ifStatement = list(new IfExecutorFactory(),
						kw("if", "("), expression, kw(")", "{"), expression,
						kw("}"), optional(kw("else", "{"), expression, kw("}")));
				expression.define(or(ifStatement, number(),
						list(kw("{"), many(expression), kw("}"))));
				grammar = many(expression);
			}
		};
		return myGrammar;
	}

	@Test
	public void given_SimpleGrammarAndMatchingSource_when_Analyzing_then_Success() {
		final Analyzer myGrammar = defineMyGrammar();
		AnalysisResult result;
		Long res;
		result = myGrammar.analyze(new StringSourceCode("if(1){55}else{33}"));
		LOG.debug(result.getExecutor().toString());
		res = (Long) result.getExecutor().execute();
		Assert.assertEquals((Long) 55L, res);

		result = myGrammar.analyze(new StringSourceCode("if(0){55}else{33}"));
		LOG.debug(result.getExecutor().toString());
		res = (Long) result.getExecutor().execute();
		Assert.assertEquals((Long) 33L, res);

		result = myGrammar.analyze(new StringSourceCode("if(1){55}"));
		LOG.debug(result.getExecutor().toString());
		res = (Long) result.getExecutor().execute();
		Assert.assertEquals((Long) 55L, res);

		result = myGrammar.analyze(new StringSourceCode(
				"if(1){if(0){1}else{55}}"));
		LOG.debug(result.getExecutor().toString());
		res = (Long) result.getExecutor().execute();
		Assert.assertEquals((Long) 55L, res);

	}

	private static class IfExecutorFactory implements Factory<ListExecutor> {

		@Override
		public ListExecutor get() {
			return new IfExecutor();
		}

	}

	private static class IfExecutor implements ListExecutor {

		@Override
		public Object execute() {
			final Object condition = executorList.get(0).execute();
			final Long one = (Long) condition;
			if (one != 0) {
				if (executorList.size() > 1) {
					return executorList.get(1).execute();
				} else {
					return null;
				}
			} else {
				if (executorList.size() > 2) {
					return executorList.get(2).execute();
				} else {
					return null;
				}
			}
		}

		private List<Executor> executorList;

		@Override
		public void setList(final List<Executor> executorList) {
			this.executorList = executorList;
		}

		@Override
		public String toString() {
			String res = "if(" + executorList.get(0).toString() + ")";
			if (executorList.size() > 1) {
				res += "{" + executorList.get(1).toString() + "}";
			}
			if (executorList.size() > 2) {
				res += "else{" + executorList.get(2).toString() + "}";
			}
			return res;
		}
	}
}
