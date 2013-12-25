package com.javax0.jdsl;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.javax0.jdsl.analyzers.AnalysisResult;
import com.javax0.jdsl.analyzers.Analyzer;
import com.javax0.jdsl.analyzers.PassThroughAnalyzer;
import com.javax0.jdsl.analyzers.StringSourceCode;
import com.javax0.jdsl.executors.Executor;
import com.javax0.jdsl.executors.Factory;
import com.javax0.jdsl.executors.ListExecutor;

public class GrammarTest {

	@Test
	public void given_SimpleGrammarAndMatchingSource_when_Analyzing_then_Success() {
		Analyzer ifGrammar = new GrammarDefinition() {
			@Override
			void define() {
//				skipSpaces();
				PassThroughAnalyzer command = definedLater("command");
				PassThroughAnalyzer expression = definedLater("expression");
				Analyzer ifStatement = list(new IfExecutorFactory(),
						kw("if", "("), expression, kw(")", "{"), command,
						kw("}"), optional(kw("else", "{"), command, kw("}")));
				expression.define(number());
				command.define(or(ifStatement, kw("A", ";"), kw("B", ";"),
						list(number(), kw(";")),
						list(kw("{"), many(command), kw("}"))));
				grammar = many(command);
			}
		};
		AnalysisResult result;
		Long res; 
//		result = ifGrammar.analyze(new StringSourceCode(
//				"if(1){55;} else{33;}"));
//		res = (Long)result.getExecutor().execute();
//		Assert.assertEquals((Long)55L, res);
//		result = ifGrammar.analyze(new StringSourceCode(
//				"if(1){55;}"));
//		res = (Long)result.getExecutor().execute();
//		Assert.assertEquals((Long)55L, res);
		result = ifGrammar.analyze(new StringSourceCode(
				"if(1){if(0){1;}else{55;}}"));
		Object resO = result.getExecutor().execute();
		
//		Assert.assertEquals((Long)55L, res);

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
			Object condition = executorList.get(0).execute();
			Long one = (Long) condition;
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
		public void setList(List<Executor> executorList) {
			this.executorList = executorList;
		}
	}
}
