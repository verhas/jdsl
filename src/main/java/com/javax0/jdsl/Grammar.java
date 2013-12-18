package com.javax0.jdsl;

import com.javax0.jdsl.analyzers.AlternativesAnalyzer;
import com.javax0.jdsl.analyzers.Analyzer;
import com.javax0.jdsl.analyzers.ListAnalyzer;
import com.javax0.jdsl.analyzers.PassThroughAnalyzer;
import com.javax0.jdsl.analyzers.SequenceAnalyzer;
import com.javax0.jdsl.analyzers.TerminalSymbolAnalyzer;
import com.javax0.jdsl.executors.ListExecutor;
import com.javax0.jdsl.executors.SimpleListExecutor;
import static com.javax0.jdsl.analyzers.SequenceAnalyzer.INFINITE;

/**
 * This class is a singleton to ease the build of grammar. To use it you can
 * import the field {@code is} statically and then use the utility functions.
 * 
 * <pre>
 * import static com.javax0.jdsl.Grammar.is;
 * ListExecutor ifExecutor = new IfExecutor();
 * PassThroughAnalyzer command = is.definedLater();
 * Analyzer ifCommand = is.list(ifExecutor
 *                ,kw("if"),kw("("),expression,kw(")"),command
 *                ,is.optional(is.list(kw("else"),command,kw("endif"))));
 * command.define( or(ifCommand,whileCommand,letCommand,printCommand) );
 * Analyzer programAnalyzer = is.many(command);
 */
public class Grammar {
	public static Grammar is = new Grammar();

	private Grammar() {
	}

	public Analyzer kw(String keyword) {
		return new TerminalSymbolAnalyzer(keyword);
	}

	public PassThroughAnalyzer definedLater() {
		return new PassThroughAnalyzer();
	}

	public Analyzer list(ListExecutor listExecutor, Analyzer... analyzers) {
		final ListAnalyzer listAnalyzer = new ListAnalyzer(listExecutor);
		listAnalyzer.add(analyzers);
		return listAnalyzer;
	}

	public Analyzer list(Analyzer... analyzers) {
		final ListExecutor listExecutor = new SimpleListExecutor();
		final ListAnalyzer listAnalyzer = new ListAnalyzer(listExecutor);
		listAnalyzer.add(analyzers);
		return listAnalyzer;
	}
	
	public Analyzer or(Analyzer... analyzers) {
		AlternativesAnalyzer alternativesAnalyzer = new AlternativesAnalyzer();
		alternativesAnalyzer.add(analyzers);
		return alternativesAnalyzer;
	}

	public Analyzer optional(ListExecutor listExecutor, Analyzer analyzer) {
		return new SequenceAnalyzer(listExecutor, analyzer, 0, 1);
	}

	public Analyzer optional(Analyzer analyzer) {
		final ListExecutor listExecutor = new SimpleListExecutor();
		return new SequenceAnalyzer(listExecutor, analyzer, 0, 1);
	}

	public Analyzer many(ListExecutor listExecutor, Analyzer analyzer, int min,
			int max) {
		return new SequenceAnalyzer(listExecutor, analyzer, 1, -1);
	}

	public Analyzer many(Analyzer analyzer, int min, int max) {
		final ListExecutor listExecutor = new SimpleListExecutor();
		return new SequenceAnalyzer(listExecutor, analyzer, 1, INFINITE);
	}

	public Analyzer manyOptional(ListExecutor listExecutor, Analyzer analyzer) {
		return new SequenceAnalyzer(listExecutor, analyzer, 0, INFINITE);
	}

	public Analyzer manyOptional(Analyzer analyzer) {
		final ListExecutor listExecutor = new SimpleListExecutor();
		return new SequenceAnalyzer(listExecutor, analyzer, 0, INFINITE);
	}

	public Analyzer many(ListExecutor listExecutor, Analyzer analyzer) {
		return new SequenceAnalyzer(listExecutor, analyzer, 1, INFINITE);
	}

	public Analyzer many(Analyzer analyzer) {
		final ListExecutor listExecutor = new SimpleListExecutor();
		return new SequenceAnalyzer(listExecutor, analyzer, 1, INFINITE);
	}
}
