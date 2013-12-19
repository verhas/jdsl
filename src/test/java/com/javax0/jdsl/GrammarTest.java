package com.javax0.jdsl;

import static com.javax0.jdsl.Grammar.definedLater;
import static com.javax0.jdsl.Grammar.kw;
import static com.javax0.jdsl.Grammar.list;
import static com.javax0.jdsl.Grammar.many;
import static com.javax0.jdsl.Grammar.optional;
import static com.javax0.jdsl.Grammar.or;

import org.junit.Test;

import com.javax0.jdsl.analyzers.Analyzer;
import com.javax0.jdsl.analyzers.PassThroughAnalyzer;
import com.javax0.jdsl.analyzers.StringSourceCode;

public class GrammarTest {

	@Test
	public void given_SimpleGrammarAndMatchingSource_when_Analyzing_then_Success() {
		PassThroughAnalyzer command = definedLater();
		PassThroughAnalyzer expression = definedLater();
		Analyzer ifStatement = list(kw("if", "("), expression, kw(")", "{"),
				command, kw("}"), optional(kw("else", "{"), command, kw("}")));
		expression.define(or(kw("A"), kw("B")));
		command.define(or(ifStatement));
		Analyzer grammar = many(command);
		grammar.analyze(new StringSourceCode("if( 1 ){ A }else{ B }"));
	}
}
