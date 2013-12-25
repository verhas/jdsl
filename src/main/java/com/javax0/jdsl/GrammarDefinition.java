package com.javax0.jdsl;

import static com.javax0.jdsl.analyzers.SequenceAnalyzer.INFINITE;

import com.javax0.jdsl.analyzers.AlternativesAnalyzer;
import com.javax0.jdsl.analyzers.AnalysisResult;
import com.javax0.jdsl.analyzers.Analyzer;
import com.javax0.jdsl.analyzers.ListAnalyzer;
import com.javax0.jdsl.analyzers.NoExecutorListAnalyzer;
import com.javax0.jdsl.analyzers.PassThroughAnalyzer;
import com.javax0.jdsl.analyzers.SequenceAnalyzer;
import com.javax0.jdsl.analyzers.SkippingAnalyzer;
import com.javax0.jdsl.analyzers.SourceCode;
import com.javax0.jdsl.analyzers.WhiteSpaceSkippingAnalyzer;
import com.javax0.jdsl.analyzers.terminals.NumberAnalyzer;
import com.javax0.jdsl.analyzers.terminals.TerminalSymbolAnalyzer;
import com.javax0.jdsl.executors.Factory;
import com.javax0.jdsl.executors.ListExecutor;
import com.javax0.jdsl.executors.SimpleListExecutor;
import com.javax0.jdsl.executors.SimpleListExecutorFactory;

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
public abstract class GrammarDefinition implements Analyzer {

	abstract void define();

	protected Analyzer grammar = null;

	public final AnalysisResult analyze(SourceCode input) {
		if (grammar == null) {
			define();
		}
		if (grammar == null) {
			throw new IllegalArgumentException(
					"'grammar' was not set in the grammar definition");
		}
		return grammar.analyze(input);
	}

	private SkippingAnalyzer skippingAnalyzer;

	public final void setSkippingAnalyzer(SkippingAnalyzer skippingAnalyzer) {
		this.skippingAnalyzer = skippingAnalyzer;
	}

	public final void skipSpaces() {
		setSkippingAnalyzer(new WhiteSpaceSkippingAnalyzer());
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
	 * <p>
	 * Note that you can not use {@code or(kw("A","B"))} instead of
	 * {@code or(kw("A"),kw("B"))}. Alternative analyzer does NOT flatten
	 * keyword lists.
	 * 
	 * @param keywords
	 * @return
	 */
	public final Analyzer kw(String... keywords) {
		if (keywords.length == 1) {
			return new TerminalSymbolAnalyzer(keywords[0]);
		}
		ListAnalyzer keywordListAnalyzer = new NoExecutorListAnalyzer();
		keywordListAnalyzer.setSkipAnalyzer(skippingAnalyzer);
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
	 * <p>
	 * The parameter {@code name} is only used in the debug logs when the
	 * grammar is debugged.
	 */
	public final PassThroughAnalyzer definedLater(String name) {
		return new PassThroughAnalyzer(name);
	}

	public final PassThroughAnalyzer definedLater() {
		return definedLater("noname");
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
	public final Analyzer list(Factory<ListExecutor> listExecutorFactory, Analyzer... analyzers) {
		final ListAnalyzer listAnalyzer = new ListAnalyzer(listExecutorFactory);
		listAnalyzer.setSkipAnalyzer(skippingAnalyzer);
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
	public final Analyzer list(Analyzer... analyzers) {
		return list(SimpleListExecutorFactory.INSTANCE, analyzers);
	}

	/**
	 * Creates an {@link AlternativesAnalyzer} with the arguments as
	 * alternatives.
	 */
	public final Analyzer or(Analyzer... analyzers) {
		AlternativesAnalyzer alternativesAnalyzer = new AlternativesAnalyzer();
		alternativesAnalyzer.add(analyzers);
		return alternativesAnalyzer;
	}

	/**
	 * Creates a {@link SequenceAnalyzer} with zero min value and one max value.
	 * This means that the underlying analyzer need not be matched, or may be
	 * matched once.
	 */
	public final Analyzer optional(ListExecutor listExecutor, Analyzer analyzer) {
		SequenceAnalyzer sequenceAnalyzer = new SequenceAnalyzer(listExecutor,
				analyzer, 0, 1);
		sequenceAnalyzer.setSkipAnalyzer(skippingAnalyzer);
		return sequenceAnalyzer;
	}

	public final Analyzer optional(ListExecutor listExecutor,
			Analyzer... analyzers) {
		return optional(listExecutor, list(analyzers));
	}

	/**
	 * Same as {@link #optional(ListExecutor, Analyzer)} except that it does not
	 * use an external executor, but rather creates a new
	 * {@link SimpleListExecutor}.
	 */
	public final Analyzer optional(Analyzer analyzer) {
		return optional(new SimpleListExecutor(), analyzer);
	}

	public final Analyzer optional(Analyzer... analyzers) {
		return optional(new SimpleListExecutor(), list(analyzers));
	}

	public final Analyzer many(ListExecutor listExecutor, Analyzer analyzer,
			int min, int max) {
		SequenceAnalyzer sequenceAnalyzer = new SequenceAnalyzer(listExecutor,
				analyzer, 1, -1);
		sequenceAnalyzer.setSkipAnalyzer(skippingAnalyzer);
		return sequenceAnalyzer;
	}

	public final Analyzer many(Analyzer analyzer, int min, int max) {
		return many(new SimpleListExecutor(), analyzer);
	}

	public final Analyzer manyOptional(ListExecutor listExecutor,
			Analyzer analyzer) {
		SequenceAnalyzer sequenceAnalyzer = new SequenceAnalyzer(listExecutor,
				analyzer, 0, INFINITE);
		sequenceAnalyzer.setSkipAnalyzer(skippingAnalyzer);
		return sequenceAnalyzer;
	}

	public final Analyzer manyOptional(Analyzer analyzer) {
		return manyOptional(new SimpleListExecutor(), analyzer);
	}

	public final Analyzer many(ListExecutor listExecutor, Analyzer analyzer) {
		SequenceAnalyzer sequenceAnalyzer = new SequenceAnalyzer(listExecutor,
				analyzer, 1, INFINITE);
		sequenceAnalyzer.setSkipAnalyzer(skippingAnalyzer);
		return sequenceAnalyzer;
	}

	public final Analyzer many(Analyzer analyzer) {
		return many(new SimpleListExecutor(), analyzer);
	}

	public final Analyzer number() {
		return new NumberAnalyzer();
	}
}
