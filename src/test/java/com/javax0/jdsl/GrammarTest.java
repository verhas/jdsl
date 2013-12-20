package com.javax0.jdsl;

import java.util.List;

import org.junit.Test;

import com.javax0.jdsl.analyzers.AnalysisResult;
import com.javax0.jdsl.analyzers.Analyzer;
import com.javax0.jdsl.analyzers.PassThroughAnalyzer;
import com.javax0.jdsl.analyzers.StringSourceCode;
import com.javax0.jdsl.executors.Executor;
import com.javax0.jdsl.executors.ListExecutor;

public class GrammarTest {

	@Test
	public void given_SimpleGrammarAndMatchingSource_when_Analyzing_then_Success() {
		Analyzer ifGrammar = new GrammarDefinition() {
			@Override
			void define() {
				skipSpaces();
				PassThroughAnalyzer command = definedLater();
				PassThroughAnalyzer expression = definedLater();
				Analyzer ifStatement = list(new IfExecutor(), kw("if", "("),
						expression, kw(")", "{"), command, kw("}"),
						optional(kw("else", "{"), command, kw("}")));
				expression.define(number());
				command.define(or(ifStatement, kw("A", "B"),
						list(kw("{"), many(command), kw("}"))));
				grammar = many(command);
			}
		};
		AnalysisResult result = ifGrammar.analyze(new StringSourceCode(
				"if(1){A}else{B}"));
		result.getExecutor().execute();
	}

	private static class IfExecutor implements ListExecutor {

		@Override
		public Object execute() {
			Object condition = executorList.get(0).execute();
			return null;
		}

		private List<Executor> executorList;

		@Override
		public void setList(List<Executor> executorList) {
			this.executorList = executorList;
		}
	}
}
