package com.javax0.jdsl;

import static com.javax0.jdsl.analyzers.SequenceAnalyzer.INFINITE;

import com.javax0.jdsl.analyzers.AlternativesAnalyzer;
import com.javax0.jdsl.analyzers.Analyzer;
import com.javax0.jdsl.analyzers.ListAnalyzer;
import com.javax0.jdsl.analyzers.NoExecutorListAnalyzer;
import com.javax0.jdsl.analyzers.PassThroughAnalyzer;
import com.javax0.jdsl.analyzers.SequenceAnalyzer;
import com.javax0.jdsl.analyzers.TerminalSymbolAnalyzer;
import com.javax0.jdsl.executors.ListExecutor;
import com.javax0.jdsl.executors.SimpleListExecutor;

/**
 * This class is a singleton to ease the build of grammar. To use it you can
 * import the field {@code is} statically and then use the utility functions.
 * 
 * <pre>
 * import static com.javax0.jdsl.Grammar.is;
 * ListExecutor ifExecutor = new IfExecutor();
 * PassThroughAnalyzer command = definedLater();
 * Analyzer ifCommand = list(ifExecutor
 *                ,kw("if","("),expression,kw(")"),command
 *                ,is.optional(is.list(kw("else"),command,kw("endif"))));
 * command.define( or(ifCommand,whileCommand,letCommand,printCommand) );
 * Analyzer programAnalyzer = is.many(command);
 */
public class Grammar {

	private Grammar() {
	}

	/**
	 * Create a {@link TerminalSymbolAnalyzer} if there is only one argument or
	 * a {@link NoExecutorListAnalyzer} containing the
	 * {@link TerminalSymbolAnalyzer}s created for the arguments.
	 * <p>
	 * Note that using
	 * 
	 * <pre>
	 * list(kw(&quot;A&quot;), kw(&quot;B&quot;))
	 * </pre>
	 * 
	 * is equivalent to
	 * 
	 * <pre>
	 * list(kw(&quot;A&quot;, &quot;B&quot;))
	 * </pre>
	 * 
	 * when calling {@link #list(Analyzer...)} or
	 * {@link #list(ListExecutor, Analyzer...)} because the
	 * {@link NoExecutorListAnalyzer}s get flattened into the list.
	 * 
	 * @param keywords
	 * @return
	 */
	public static Analyzer kw(String... keywords) {
		if (keywords.length == 1) {
			return new TerminalSymbolAnalyzer(keywords[0]);
		}
		ListAnalyzer keywordListAnalyzer = new NoExecutorListAnalyzer();
		for (String keyword : keywords) {
			keywordListAnalyzer.add(new TerminalSymbolAnalyzer(keyword));
		}
		return keywordListAnalyzer;
	}

	/**
	 * Returns a new {@link PassThroughAnalyzer} analyzer. This way a grammar
	 * element can be defined later than it is used. This is needed when there
	 * is some recursive (circular) definition in the grammar. (And usually
	 * there is.)
	 */
	public static PassThroughAnalyzer definedLater() {
		return new PassThroughAnalyzer();
	}

	private static void addAnalyzerFlattened(final ListAnalyzer listAnalyzer,
			final Analyzer analyzer) {
		if (analyzer instanceof NoExecutorListAnalyzer) {
			for (Analyzer subAnalyzer : ((NoExecutorListAnalyzer) analyzer)
					.getAnalyzerList()) {
				addAnalyzerFlattened(listAnalyzer, subAnalyzer);
			}
		} else {
			listAnalyzer.add(analyzer);
		}
	}

	/**
	 * Create a {@link ListAnalyzer} that uses the executor {@code listExecutor}
	 * and contains the {@code analyzers} in a list.
	 * <p>
	 * If the {@code analyzers} contain {@link NoExecutorListAnalyzer} instances
	 * then these are replaced by the elements of their lists. The flattening is
	 * done recursively so long as long there are {@link NoExecutorListAnalyzer}
	 * in any of the lists. This means that the method {@link #kw(String...)}
	 * can be used with many arguments and when used in the argument list of a
	 * {@link #list(ListExecutor, Analyzer...)} then they will have the same
	 * effect as if the strings were used individually to define terminal
	 * symbols.
	 */
	public static Analyzer list(ListExecutor listExecutor, Analyzer... analyzers) {
		final ListAnalyzer listAnalyzer = new ListAnalyzer(listExecutor);
		for (Analyzer analyzer : analyzers) {
			addAnalyzerFlattened(listAnalyzer, analyzer);
		}
		return listAnalyzer;
	}

	/**
	 * Same as {@link #list(ListExecutor, Analyzer...)} except it does not use
	 * an external executor, but rather creates a new {@link SimpleListExecutor}
	 * . This is quite handy in many cases when the underlying elements in the
	 * list should simply be executed one after the other, or when there is
	 * nothing to execute in a list.
	 */
	public static Analyzer list(Analyzer... analyzers) {
		return list(new SimpleListExecutor(), analyzers);
	}

	/**
	 * Creates an {@lin AlternativesAnalyzer} with the arguments as
	 * alternatives.
	 */
	public static Analyzer or(Analyzer... analyzers) {
		AlternativesAnalyzer alternativesAnalyzer = new AlternativesAnalyzer();
		alternativesAnalyzer.add(analyzers);
		return alternativesAnalyzer;
	}

	/**
	 * Creates a {@link SequenceAnalyzer} with zero min value and one max value.
	 * This means that the underlying analyzer need not be matched, or may be
	 * matched once.
	 */
	public static Analyzer optional(ListExecutor listExecutor, Analyzer analyzer) {
		return new SequenceAnalyzer(listExecutor, analyzer, 0, 1);
	}

	public static Analyzer optional(ListExecutor listExecutor, Analyzer... analyzers) {
		return optional(listExecutor, list(analyzers));
	}

	/**
	 * Same as {@link #optional(ListExecutor, Analyzer)} except that it does not
	 * use an external executor, but rather creates a new
	 * {@link SimpleListExecutor}.
	 */
	public static Analyzer optional(Analyzer analyzer) {
		return optional(new SimpleListExecutor(), analyzer);
	}

	public static Analyzer optional(Analyzer... analyzers) {
		return optional(new SimpleListExecutor(), list(analyzers));
	}

	public static Analyzer many(ListExecutor listExecutor, Analyzer analyzer, int min,
			int max) {
		return new SequenceAnalyzer(listExecutor, analyzer, 1, -1);
	}

	public static Analyzer many(Analyzer analyzer, int min, int max) {
		return many(new SimpleListExecutor(), analyzer);
	}

	public static Analyzer manyOptional(ListExecutor listExecutor, Analyzer analyzer) {
		return new SequenceAnalyzer(listExecutor, analyzer, 0, INFINITE);
	}

	public static Analyzer manyOptional(Analyzer analyzer) {
		return manyOptional(new SimpleListExecutor(), analyzer);
	}

	public static Analyzer many(ListExecutor listExecutor, Analyzer analyzer) {
		return new SequenceAnalyzer(listExecutor, analyzer, 1, INFINITE);
	}

	public static Analyzer many(Analyzer analyzer) {
		return many(new SimpleListExecutor(), analyzer);
	}
}
