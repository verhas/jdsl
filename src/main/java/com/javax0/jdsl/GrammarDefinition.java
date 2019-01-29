package com.javax0.jdsl;

import com.javax0.jdsl.analyzers.*;
import com.javax0.jdsl.analyzers.terminals.TerminalSymbolAnalyzer;
import com.javax0.jdsl.executors.*;

import java.util.LinkedList;
import java.util.List;

import static com.javax0.jdsl.analyzers.SequenceAnalyzer.INFINITE;

/**
 * This class is an abstract class to ease the build of grammar. To use it you can
 * extend the class and define the method {@code define()}. Inside the method
 * you can use the methods of the class to build up a grammar in easy and
 * readable way.
 *
 * <pre>
 * final Analyzer myGrammar = new GrammarDefinition() {
 * 	&#064;Override
 * 	protected final Analyzer define() {
 * 		ReporterFactory.setReporter(new NullReporter());
 * 		skipSpaces();
 * 		final Define expression = later();
 * 		final Analyzer ifStatement = list(new IfExecutorFactory(),
 * 				kw(&quot;if&quot;, &quot;(&quot;), expression, kw(&quot;)&quot;, &quot;{&quot;), expression, kw(&quot;}&quot;),
 * 				optional(kw(&quot;else&quot;, &quot;{&quot;), expression, kw(&quot;}&quot;)));
 * 		expression.define(or(ifStatement, number(),
 * 				list(kw(&quot;{&quot;), many(expression), kw(&quot;}&quot;))));
 * 		return many(expression);
 *    }
 * };
 * </pre>
 * <p>
 * Some of the methods in this class have three different forms:
 *
 * <ol>
 * <li>method(analyzer/s)</li>
 * <li>method(factory,analyzer/s)</li>
 * <li>method(class,analyzer/s)</li>
 * </ol>
 * <p>
 * The first version analyzers the source and creates an executor that is
 * created using the {@code SimpleListExecutorFactory}. This will just execute
 * the underlying executors of the list that is resulted by the analysis.
 * <p>
 * The second version uses the executor created by the passed factory.
 * <p>
 * The third version just creates a new instance of the executor calling the
 * method {@code newInstance()} for the passed class.
 */
public abstract class GrammarDefinition implements Analyzer {

    private Analyzer grammar = null;
    private final List<PassThroughAnalyzer> delayedDefinitionAnalyzers = new LinkedList<>();
    private SkippingAnalyzer skippingAnalyzer;
    private TerminalSymbolAnalyzer.CharCompare charCompare = TerminalSymbolAnalyzer.CharCompare.caseSensitive;

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

    protected abstract Analyzer define();

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
        if (result.wasSuccessful() && result.remainingSourceCode().length() > 0) {
            result = SimpleAnalysisResult.failed(this.getClass(),
                    "there are trailing characters");
        }
        return result;
    }

    public final void setSkippingAnalyzer(
            final SkippingAnalyzer skippingAnalyzer) {
        this.skippingAnalyzer = skippingAnalyzer;
    }

    public final void skipSpaces() {
        setSkippingAnalyzer(new WhiteSpaceSkippingAnalyzer());
    }

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
     * <p>
     * is equivalent to
     *
     * <pre>
     * list(kw(&quot;A&quot;, &quot;B&quot;))
     * </pre>
     * <p>
     * when calling {@link #list(Analyzer...)} or
     * {@link #list(Class, Analyzer...)} or {@link #list(Factory, Analyzer...)}
     * because the {@link NoExecutorListAnalyzer}s get flattened into the list.
     * <p>
     * Note that you can not use {@code or(kw("A","B"))} instead of
     * {@code or(kw("A"),kw("B"))}. Alternative analyzer does NOT flatten
     * keyword lists.
     * <p>
     * Note that this version of them ethod creates
     * {@link TerminalSymbolAnalyzer} that compares the keywords case sensitive.
     * If you want case insensitive keywords, then use
     */
    public final Rule kw(final String... keywords) {
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
     * Returns a new special analyzer that implements the interface Define. This
     * way a grammar element can be defined later than it is used. This is
     * needed when there is some recursive (circular) definition in the grammar.
     * (And usually there is.)
     * <p>
     * The parameter {@code name} is only used in the debug logs when the
     * grammar is debugged.
     */
    public final Define later(final String name) {
        final PassThroughAnalyzer delayedDefinitionAnalyzer = new PassThroughAnalyzer(
                name);
        delayedDefinitionAnalyzers.add(delayedDefinitionAnalyzer);
        return delayedDefinitionAnalyzer;
    }

    public final Define later() {
        return later("noname");
    }

    /**
     * Create a {@link ListAnalyzer} that uses an executor created using the
     * {@code listExecutorFactory} and contains the {@code analyzers} in a list.
     * <p>
     * If the {@code analyzers} contain {@link NoExecutorListAnalyzer} instances
     * then these analyzers are replaced by the elements of their lists. The
     * flattening is done recursively so long as long there are
     * {@link NoExecutorListAnalyzer}s in any of the lists. This means that the
     * method {@link #kw(String...)} can be used with many arguments and when
     * used in the argument list of a {@link #list(Analyzer...)} or {@link #list(Class, Analyzer...)} or {@link #list(Factory, Analyzer...)}
     * then they will have the same effect as if the strings were used
     * individually to define terminal symbols.
     */
    public final Rule list(final Factory<ListExecutor> listExecutorFactory,
                           final Analyzer... analyzers) {
        final ListAnalyzer listAnalyzer = new ListAnalyzer(listExecutorFactory);
        listAnalyzer.setSkipAnalyzer(skippingAnalyzer);
        for (final Analyzer analyzer : analyzers) {
            addAnalyzerFlattened(listAnalyzer, analyzer);
        }
        return listAnalyzer;
    }

    /**
     * Same as {@link #list(Factory, Analyzer...)} except the first argument is
     * not a factory, but the class of the executor. For more information see
     */
    public final Rule list(
            final Class<ListExecutor> listExecutorClass,
            final Analyzer... analyzers) {
        Factory<ListExecutor> listExecutorFactory = SingletonFactory.get(listExecutorClass);
        return list(listExecutorFactory, analyzers);
    }

    /**
     * Same as {@link #list(Factory, Analyzer...)} except it does not use
     * an external executor, but rather creates a new {@link SimpleListExecutor}
     * . This is quite handy in many cases when the underlying elements in the
     * list should simply be executed one after the other, or when there is
     * nothing to execute in a list.
     */
    public final Rule list(final Analyzer... analyzers) {
        return list(SimpleListExecutorFactory.INSTANCE, analyzers);
    }

    /**
     * Creates an {@link AlternativesAnalyzer} with the arguments as
     * alternatives.
     */
    public final Rule or(final Analyzer... analyzers) {
        final AlternativesAnalyzer alternativesAnalyzer = new AlternativesAnalyzer();
        alternativesAnalyzer.add(analyzers);
        return alternativesAnalyzer;
    }

    /**
     * Creates an {@link AlternativesAnalyzer} that matches one of the
     * characters of the argument string. In other words if the argument is
     * {@code "abc"} then {@code or("abc")} is equivalent to
     * {@code or(kw("a"),kw("b"),kw("c"))} using {@link #or(Analyzer...)}.
     */
    public final Rule or(final String terminals) {
        final AlternativesAnalyzer alternativesAnalyzer = new AlternativesAnalyzer();
        for (int i = 0; i < terminals.length(); i++) {
            alternativesAnalyzer.add(kw(terminals.substring(i, i + 1)));
        }
        return alternativesAnalyzer;
    }

    /**
     * Same as {@link #or(Analyzer...)}.
     */
    public final Rule oneOf(final Analyzer... analyzers) {
        return or(analyzers);
    }

    /**
     * Same as {@link #or(Analyzer...)}.
     */
    public final Rule oneOf(final String terminals) {
        return or(terminals);
    }

    /**
     * Creates a {@link SequenceAnalyzer} with zero min value and one max value.
     * This means that the underlying analyzer need not be matched, or may be
     * matched once.
     */
    public final Rule optional(final Factory<ListExecutor> listExecutorFactory,
                               final Analyzer analyzer) {
        return many(listExecutorFactory, analyzer, 0, 1);
    }

    /**
     * Creates a {@link SequenceAnalyzer} with zero min value and one max value.
     * This means that the underlying analyzer need not be matched, or may be
     * matched once.
     */
    public final Rule optional(
            final Class<? extends ListExecutor> listExecutorClass,
            final Analyzer analyzer) {
        return many(listExecutorClass, analyzer, 0, 1);
    }

    /**
     * This method is a convenience method to call
     * {@code optional(list(factory, analyzers))}
     */
    public final Rule optional(final Factory<ListExecutor> listExecutorFactory,
                               final Analyzer... analyzers) {
        return optional(listExecutorFactory, list(analyzers));
    }

    /**
     * This method is a convenience method to call
     * {@code optional(list(class, analyzers))}
     */
    public final Rule optional(
            final Class<? extends ListExecutor> listExecutorClass,
            final Analyzer... analyzers) {
        return optional(listExecutorClass, list(analyzers));
    }

    /**
     * Same as {@link #optional(Factory, Analyzer)} except that it does not
     * use an external executor, but rather creates a new
     * {@link SimpleListExecutor}.
     */
    public final Rule optional(final Analyzer analyzer) {
        return optional(SimpleListExecutorFactory.INSTANCE, analyzer);
    }

    /**
     * This method is a convenience method to call
     * {@code optional(list(analyzers))}
     */
    public final Rule optional(final Analyzer... analyzers) {
        return optional(SimpleListExecutorFactory.INSTANCE, analyzers);
    }

    /**
     * Creates a sequence analyzer that uses the {@code analyzer} to at least
     * {@code min} and at most {@code max} times. Each time the analyzer is
     * successful it creates a new {@link ListExecutor} using the
     * {@code listExecutorFactory}.
     */
    public final Rule many(final Factory<ListExecutor> listExecutorFactory,
                           final Analyzer analyzer, final int min, final int max) {
        final SequenceAnalyzer sequenceAnalyzer = new SequenceAnalyzer(
                listExecutorFactory, analyzer, min, max);
        sequenceAnalyzer.setSkipAnalyzer(skippingAnalyzer);
        return sequenceAnalyzer;
    }

    /**
     * Same as {@link #many(Factory, Analyzer, int, int)} except the first
     * argument is not a factory for a list analyzer but rather the class. The
     * executor creation details are described in the class documentation
     * {@link GrammarDefinition}.
     */
    public final Rule many(
            final Class<? extends ListExecutor> listExecutorClass,
            final Analyzer analyzer, final int min, final int max) {
        Factory<ListExecutor> listExecutorFactory = (Factory<ListExecutor>) SingletonFactory
                .get(listExecutorClass);
        return many(listExecutorFactory, analyzer, min, max);
    }

    /**
     * Same as {@link #many(Factory, Analyzer, int, int)} but it does not accept
     * any factory. Instead it uses a {@link SimpleListExecutorFactory}.
     */
    public final Rule many(final Analyzer analyzer, final int min, final int max) {
        return many(SimpleListExecutorFactory.INSTANCE, analyzer, min, max);
    }

    /**
     * Creates a sequence analyzer that analyzes zero or more times. On the
     * argument see {@link #many(Factory, Analyzer, int, int)} with {@code min}
     * and {@code max} set to zero, and infinite.
     */
    public final Rule manyOptional(
            final Factory<ListExecutor> listExecutorFactory,
            final Analyzer analyzer) {
        return many(listExecutorFactory, analyzer, 0, INFINITE);
    }

    /**
     * Same as {@link #manyOptional(Factory, Analyzer)} except the first
     * argument is not a factory for a list analyzer but rather the class. The
     * executor creation details are described in the class documentation
     * {@link GrammarDefinition}.
     */
    public final Rule manyOptional(
            final Class<? extends ListExecutor> listExecutorClass,
            final Analyzer analyzer) {
        Factory<ListExecutor> listExecutorFactory = (Factory<ListExecutor>) SingletonFactory
                .get(listExecutorClass);
        return manyOptional(listExecutorFactory, analyzer);
    }

    /**
     * Same as {@link #manyOptional(Factory, Analyzer)} but it does not accept
     * any factory. Instead it uses a {@link SimpleListExecutorFactory}.
     */
    public final Rule manyOptional(final Analyzer analyzer) {
        return manyOptional(new SimpleListExecutorFactory(), analyzer);
    }

    /**
     * Same as {@link #manyOptional(Factory, Analyzer)} but the {@code min}
     * value is one instead of zero. This means that the structure to be
     * analyzed by the {@code analyzer} has to be present at least once.
     */
    public final Rule many(final Factory<ListExecutor> listExecutorFactory,
                           final Analyzer analyzer) {
        return many(listExecutorFactory, analyzer, 1, INFINITE);
    }

    /**
     * Same as {@link #many(Factory, Analyzer)} except the first argument is not
     * a factory for a list analyzer but rather the class. The executor creation
     * details are described in the class documentation
     * {@link GrammarDefinition}.
     */
    public final Rule many(
            final Class<? extends ListExecutor> listExecutorClass,
            final Analyzer analyzer) {
        Factory<ListExecutor> listExecutorFactory = (Factory<ListExecutor>) SingletonFactory
                .get(listExecutorClass);
        return many(listExecutorFactory, analyzer);
    }

    /**
     * Same as {@link #many(Factory, Analyzer)} but uses the
     * {@link SimpleListExecutorFactory}.
     */
    public final Rule many(final Analyzer analyzer) {
        return many(SimpleListExecutorFactory.INSTANCE, analyzer);
    }

}
