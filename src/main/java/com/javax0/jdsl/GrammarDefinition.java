package com.javax0.jdsl;

import static com.javax0.jdsl.analyzers.SequenceAnalyzer.INFINITE;

import java.util.LinkedList;
import java.util.List;

import com.javax0.jdsl.analyzers.AlternativesAnalyzer;
import com.javax0.jdsl.analyzers.AnalysisResult;
import com.javax0.jdsl.analyzers.Analyzer;
import com.javax0.jdsl.analyzers.ListAnalyzer;
import com.javax0.jdsl.analyzers.NoExecutorListAnalyzer;
import com.javax0.jdsl.analyzers.PassThroughAnalyzer;
import com.javax0.jdsl.analyzers.SequenceAnalyzer;
import com.javax0.jdsl.analyzers.SimpleAnalysisResult;
import com.javax0.jdsl.analyzers.SkippingAnalyzer;
import com.javax0.jdsl.analyzers.SourceCode;
import com.javax0.jdsl.analyzers.WhiteSpaceSkippingAnalyzer;
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

	abstract Analyzer define();

	private Analyzer grammar = null;
	private List<PassThroughAnalyzer> delayedDefinitionAnalyzers = new LinkedList<>();

	private void assertAllDelayedAnalyzersAreDefined() {
		boolean failed = false;
		StringBuilder sb = new StringBuilder();
		for (PassThroughAnalyzer pta : delayedDefinitionAnalyzers) {
			if (!pta.isDefined()) {
				failed = true;
				sb.append(pta.toString()).append("\n");
			}
		}
		if (failed) {
			throw new IllegalArgumentException(
					"Delayed defined analyzer(s) were not defined: "
							+ sb.toString());
		}
	}

	public final AnalysisResult analyze(final SourceCode input) {
		if (grammar == null) {
			grammar = define();
		}
		if (grammar == null) {
			throw new IllegalArgumentException(
					"'grammar' was not set in the grammar definition");
		}
		assertAllDelayedAnalyzersAreDefined();
		AnalysisResult result = grammar.analyze(input);
		if (result.remainingSourceCode().length() > 0) {
			result = SimpleAnalysisResult.failed(this.getClass(),
					"there are trailing characters");
		}
		return result;
	}

	private SkippingAnalyzer skippingAnalyzer;

	public final void setSkippingAnalyzer(
			final SkippingAnalyzer skippingAnalyzer) {
		this.skippingAnalyzer = skippingAnalyzer;
	}

	public final void skipSpaces() {
		setSkippingAnalyzer(new WhiteSpaceSkippingAnalyzer());
	}

	private TerminalSymbolAnalyzer.CharCompare charCompare = TerminalSymbolAnalyzer.CharCompare.caseSensitive;

	public final void caseInsensitive() {
		charCompare = TerminalSymbolAnalyzer.CharCompare.caseInsensitive;
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
	 * <p>
	 * Note that this version of themethod creates
	 * {@link TerminalSymbolAnalyzer} that compares the keywords case sensitive.
	 * If you want case insensitive keywords, then use
	 * 
	 */
	public final Analyzer kw(final String... keywords) {
		if (keywords.length == 1) {
			return new TerminalSymbolAnalyzer(keywords[0], charCompare);
		}
		final ListAnalyzer keywordListAnalyzer = new NoExecutorListAnalyzer();
		keywordListAnalyzer.setSkipAnalyzer(skippingAnalyzer);
		for (final String keyword : keywords) {
			keywordListAnalyzer.add(new TerminalSymbolAnalyzer(keyword,
					charCompare));
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
	public final PassThroughAnalyzer later(final String name) {
		final PassThroughAnalyzer delayedDefinitionAnalyzer = new PassThroughAnalyzer(
				name);
		return delayedDefinitionAnalyzer;
	}

	public final PassThroughAnalyzer later() {
		return later("noname");
	}

	private static void addAnalyzerFlattened(final ListAnalyzer listAnalyzer,
			final Analyzer analyzer) {
		if (analyzer instanceof NoExecutorListAnalyzer) {
			for (final Analyzer subAnalyzer : ((NoExecutorListAnalyzer) analyzer)
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
	public final Analyzer list(final Factory<ListExecutor> listExecutorFactory,
			final Analyzer... analyzers) {
		final ListAnalyzer listAnalyzer = new ListAnalyzer(listExecutorFactory);
		listAnalyzer.setSkipAnalyzer(skippingAnalyzer);
		for (final Analyzer analyzer : analyzers) {
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
	public final Analyzer list(final Analyzer... analyzers) {
		return list(SimpleListExecutorFactory.INSTANCE, analyzers);
	}

	/**
	 * Creates an {@link AlternativesAnalyzer} with the arguments as
	 * alternatives.
	 */
	public final Analyzer or(final Analyzer... analyzers) {
		final AlternativesAnalyzer alternativesAnalyzer = new AlternativesAnalyzer();
		alternativesAnalyzer.add(analyzers);
		return alternativesAnalyzer;
	}

	/**
	 * Creates a {@link SequenceAnalyzer} with zero min value and one max value.
	 * This means that the underlying analyzer need not be matched, or may be
	 * matched once.
	 */
	public final Analyzer optional(
			final Factory<ListExecutor> listExecutorFactory,
			final Analyzer analyzer) {
		return many(listExecutorFactory, analyzer, 0, 1);
	}

	public final Analyzer optional(
			final Factory<ListExecutor> listExecutorFactory,
			final Analyzer... analyzers) {
		return optional(listExecutorFactory, list(analyzers));
	}

	/**
	 * Same as {@link #optional(ListExecutor, Analyzer)} except that it does not
	 * use an external executor, but rather creates a new
	 * {@link SimpleListExecutor}.
	 */
	public final Analyzer optional(final Analyzer analyzer) {
		return optional(SimpleListExecutorFactory.INSTANCE, analyzer);
	}

	public final Analyzer optional(final Analyzer... analyzers) {
		return optional(SimpleListExecutorFactory.INSTANCE, analyzers);
	}

	/**
	 * Creates a sequence analyzer that uses the {@code analyzer} to at least
	 * {@code min} and at most {@code max} times. Each time the analyzer is
	 * successful it creates a new {@link ListExecutor} using the
	 * {@code listExecutorFactory}.
	 */
	public final Analyzer many(final Factory<ListExecutor> listExecutorFactory,
			final Analyzer analyzer, final int min, final int max) {
		final SequenceAnalyzer sequenceAnalyzer = new SequenceAnalyzer(
				listExecutorFactory, analyzer, min, max);
		sequenceAnalyzer.setSkipAnalyzer(skippingAnalyzer);
		return sequenceAnalyzer;
	}

	/**
	 * Same as {@link #many(Factory, Analyzer, int, int)} but it does not accept
	 * any factory. Instead it uses a {@link SimpleListExecutorFactory}.
	 */
	public final Analyzer many(final Analyzer analyzer, final int min,
			final int max) {
		return many(SimpleListExecutorFactory.INSTANCE, analyzer, min, max);
	}

	/**
	 * Creates a sequence analyzer that analyzes zero or more times. On the
	 * argument see {@link #many(Factory, Analyzer, int, int)} with {@code min}
	 * and {@code max} set to zero, and infinite.
	 */
	public final Analyzer manyOptional(
			final Factory<ListExecutor> listExecutorFactory,
			final Analyzer analyzer) {
		return many(listExecutorFactory, analyzer, 0, INFINITE);
	}

	/**
	 * Same as {@link #manyOptional(Factory, Analyzer)} but it does not accept
	 * any factory. Instead it uses a {@link SimpleListExecutorFactory}.
	 */
	public final Analyzer manyOptional(final Analyzer analyzer) {
		return manyOptional(new SimpleListExecutorFactory(), analyzer);
	}

	/**
	 * Same as {@link #manyOptional(Factory, Analyzer)} but the {@code min}
	 * value is one instead of zero. This means that the structure to be
	 * analyzed by the {@code analyzer} has to be present at least once.
	 */
	public final Analyzer many(final Factory<ListExecutor> listExecutorFactory,
			final Analyzer analyzer) {
		return many(listExecutorFactory, analyzer, 1, INFINITE);
	}

	/**
	 * Same as {@link #many(Factory, Analyzer)} but uses the
	 * {@link SimpleListExecutorFactory}.
	 */
	public final Analyzer many(final Analyzer analyzer) {
		return many(SimpleListExecutorFactory.INSTANCE, analyzer);
	}

}
