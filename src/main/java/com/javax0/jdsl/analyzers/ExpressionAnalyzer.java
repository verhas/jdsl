package com.javax0.jdsl.analyzers;

import com.javax0.jdsl.analyzers.terminals.TerminalSymbolAnalyzer;
import com.javax0.jdsl.executors.Executor;
import com.javax0.jdsl.executors.Factory;
import com.javax0.jdsl.executors.ListExecutor;
import com.javax0.jdsl.executors.SimpleListExecutorFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An expression is a conventional expression, using binary operators, unary prefix operators, unary postfix operators,
 * terminals, parentheses pairs, and postfix list parameterized operators (function call, array access).
 * <p>
 * Assume that the priorities of the operators are from 1 to N. In that case an expression is:
 * <p>
 * <pre>
 * {@code
 * tag ::= openingParenthesis expression closingParenthesis | terminalSymbol
 * expressionList ::= expression (listSeparator expression)*
 *
 * EXP(X) =  unary* X (binaryOp_N X)* unary_N* |
 *             X (postfix_parameterized_operator_N_opening expressionList postfix_parameterized_operator_N_closing)+
 *
 * expression_N :== EXP(tag)
 * expression_i : i < N) :== EXP( expression_(i+1) )
 * expression :== expression_1
 * }
 * </pre>
 */
public final class ExpressionAnalyzer implements Rule {

    private final ExpressionListAnalyser listAnalyser = new ExpressionListAnalyser();

    @Override
    public AnalysisResult analyze(final SourceCode input) {
        return null;
    }

    private static class Tuple<PostFixCall, AnalysisResult> {
        private final PostFixCall a;
        private final AnalysisResult result;

        private Tuple(final PostFixCall a, final AnalysisResult result) {
            this.a = a;
            this.result = result;
        }
    }

    /**
     * A mutating class supporting the analysis process. It maintains the state of the input source code, the list of
     * collected executors, the result of the last executed analysis and the analyser used during the last analysis
     */
    private static class AnalysisContext {
        AnalysisContext(final SourceCode input) {
            this(input, new ArrayList<>());
        }

        AnalysisContext(final SourceCode input, List<Executor> executors) {
            this.input = input;
            this.executors = executors;
        }

        SourceCode input;
        List<Executor> executors;
        AnalysisResult result;
        Analyzer analyzer;

        /**
         * Analyse using the analyser and update the context.
         * <p>
         * If the analysis is successful, then the input is set after the analysed characters, and the result is stored
         * in the field 'result'. Also, the executor from the result is added to the list of the executors if it is not
         * null.
         * <p>
         * Independent of the success or not, the last analyser object is also stored.
         *
         * @param analyzer the analyser to use
         */
        void analyzeWith(Analyzer analyzer) {
            this.analyzer = analyzer;
            result = analyzer.analyze(input);
            if (result.wasSuccessful()) {
                if (result.getExecutor() != null) {
                    executors.add(result.getExecutor());
                }
                input = result.remainingSourceCode();
            }
        }

        /**
         * Get the last analyser, which was used. This is used for the list separator. In case there are multiple list
         * separators possible in the language, when an expression list first separator is matched by an analyser the
         * rest separators should also match the one. For example, some language could allow ',' as well as ';' as
         * expression list separator. In that case if a list uses ',' it cannot use ';' and the other way around.
         * If a grammar wants intermixed expression list separators, then it should define an analyser that accepts
         * bot ',' and ';' instead of having two terminal symbol analysers.
         *
         * @return the last analyser used, failed or not
         */
        Analyzer lastAnalyzer() {
            return analyzer;
        }

        /**
         * @return true if the last analysis failed
         */
        boolean failed() {
            return !result.wasSuccessful();
        }

        /**
         * @return true if the last analysis was successful
         */
        boolean success() {
            return result.wasSuccessful();
        }

    }

    /**
     * A simple generic fail result.
     */
    final static AnalysisResult FAIL = SimpleAnalysisResult.failed(ExpressionAnalyzer.class);

    /**
     * A pair of analysers. Usually an opening and a closing parentheses analyser.
     */
    private static class Pair {
        final Analyzer open;
        final Analyzer close;

        private Pair(final String open, final String close) {
            this(new TerminalSymbolAnalyzer(open), new TerminalSymbolAnalyzer(close));
        }

        private Pair(final Analyzer open, final Analyzer close) {
            this.open = open;
            this.close = close;
        }
    }

    /**
     * An unary or binary operator.
     */
    private static class Operator {
        final Analyzer operatorAnalyzer;
        final int priority;

        private Operator(final String operator, final int priority, final Executor executor) {
            this(TerminalSymbolAnalyzer.analyzer(operator, executor), priority);
        }

        private Operator(final Analyzer operatorAnalyzer, final int priority) {
            this.operatorAnalyzer = operatorAnalyzer;
            this.priority = priority;
        }
    }

    int maxPriority = 1;

    final List<Analyzer> terminals = new ArrayList<>(1);

    Factory<ListExecutor> factory;

    public void setFactory(Factory<ListExecutor> factory) {
        this.factory = factory;
    }

    public void addTerminals(Analyzer... analyzer) {
        terminals.addAll(List.of(analyzer));
    }

    final List<Analyzer> listSeparators = new ArrayList<>(1);

    public void addListSeparators(Analyzer... analyzer) {
        listSeparators.addAll(List.of(analyzer));
    }

    public void addListSeparator(final String lexeme, final Executor executor) {
        listSeparators.add(TerminalSymbolAnalyzer.analyzer(lexeme, executor));
    }

    public void addListSeparator(String... separators) {
        addListSeparators(Arrays.stream(separators).map(TerminalSymbolAnalyzer::new).toArray(TerminalSymbolAnalyzer[]::new));
    }

    final List<Pair> parentheses = new ArrayList<>(1);

    public void addParentheses(final String open, final String close) {
        parentheses.add(new Pair(open, close));
    }

    public void addParentheses(final Analyzer open, final Analyzer close) {
        parentheses.add(new Pair(open, close));
    }

    private static class PostfixCall {
        final Pair parentheses;
        final Executor executor;
        final int priority;

        private PostfixCall(final Analyzer open, final Analyzer close, final Executor executor, final int priority) {
            this.parentheses = new Pair(open, close);
            this.executor = executor;
            this.priority = priority;
        }

    }

    final List<PostfixCall> postfixCalls = new ArrayList<>(2);

    /**
     * Define a postfix parameterized call, like array access.
     *
     * @param open     the opening parentheses, like "[" for array access or "(" for function call, "{" for associative array access or closure call or something similar
     * @param close    the closing parentheses
     * @param executor the executor that will execute the access
     * @param priority the priority of the postfix operator
     */
    public void addPostfixCall(final String open, final String close, final Executor executor, final int priority) {
        addPostfixCall(new TerminalSymbolAnalyzer(open), new TerminalSymbolAnalyzer(close), executor, priority);
    }

    /**
     * Same as {@link #addPostfixCall(String, String, Executor, int) addPostixCall()} with string arguments, but this time the opening and closing parentheses are defined using analysers.
     *
     * @param open     the opening parentheses analyser
     * @param close    the closing parenthesis analyser
     * @param executor the executor that will execute the operator
     * @param priority the priority of the postfix operator
     */
    public void addPostfixCall(final Analyzer open, final Analyzer close, final Executor executor, final int priority) {
        postfixCalls.add(new PostfixCall(open, close, executor, priority));
        maxPriority = Math.max(maxPriority, priority);
    }

    final List<Operator> unaryPrefixes = new ArrayList<>();

    public void addUnaryPrefix(final String operator, final int priority, final Executor executor) {
        addUnaryPrefix(TerminalSymbolAnalyzer.analyzer(operator, executor), priority);
    }

    public void addUnaryPrefix(final Analyzer operator, final int priority) {
        unaryPrefixes.add(new Operator(operator, priority));
        maxPriority = Math.max(maxPriority, priority);
    }

    final List<Operator> unaryPostfixes = new ArrayList<>();

    public void addUnaryPostfix(final String operator, final int priority, final Executor executor) {
        addUnaryPostfix(TerminalSymbolAnalyzer.analyzer(operator, executor), priority);
    }

    public void addUnaryPostfix(final Analyzer operator, final int priority) {
        unaryPostfixes.add(new Operator(operator, priority));
        maxPriority = Math.max(maxPriority, priority);
    }

    final List<Operator> binaries = new ArrayList<>();

    public void addBinary(final String operator, final int priority, final Executor executor) {
        addBinary(TerminalSymbolAnalyzer.analyzer(operator, executor), priority);
    }

    public void addBinary(final Analyzer operator, final int priority) {
        binaries.add(new Operator(operator, priority));
        maxPriority = Math.max(maxPriority, priority);
    }

    private class TagAnalyzer implements Analyzer {

        @Override
        public AnalysisResult analyze(final SourceCode input) {
            for (final var p : parentheses) {
                final var openRes = p.open.analyze(input);
                if (openRes.wasSuccessful()) {
                    return getComplexTagAnalysisResult(p, openRes.remainingSourceCode());
                }
            }
            for (final var t : terminals) {
                final var terminalResult = t.analyze(input);
                if (terminalResult.wasSuccessful()) {
                    return terminalResult;
                }
            }
            return SimpleAnalysisResult.failed(TagAnalyzer.class, "Tag could not be analyzed");
        }

        /**
         * Analyze a tag that is '( expression )'. The input already points after the opening parentheses.
         *
         * @param p     the pair of parentheses
         * @param input after the opening parentheses
         * @return the analysis result
         */
        private AnalysisResult getComplexTagAnalysisResult(final Pair p, final SourceCode input) {
            final var expressionAnalysisRes = ExpressionAnalyzer.this.analyze(input);
            if (!expressionAnalysisRes.wasSuccessful()) {
                return SimpleAnalysisResult.failed(TagAnalyzer.class, "expression syntax error between parentheses");
            }
            final var analysisResult = p.close.analyze(expressionAnalysisRes.remainingSourceCode());
            if (analysisResult.wasSuccessful()) {
                return SimpleAnalysisResult.success(TagAnalyzer.class, analysisResult.remainingSourceCode(), expressionAnalysisRes.getExecutor());
            }
            return SimpleAnalysisResult.failed(TagAnalyzer.class, "no closing parenthesis");
        }
    }

    private class ExpressionListAnalyser implements Analyzer {

        Factory<ListExecutor> factory = new SimpleListExecutorFactory();

        @Override
        public AnalysisResult analyze(final SourceCode input) {
            final var context = new AnalysisContext(input);
            context.analyzeWith(ExpressionAnalyzer.this);
            if (context.success()) {
                AnalysisResult separatorRes;
                analyzeFirstListSeparator(context);
                Analyzer listSeparatorAnalyser = context.lastAnalyzer();
                while (context.success()) {
                    context.analyzeWith(ExpressionAnalyzer.this);
                    if (context.failed()) {
                        return FAIL;
                    }
                    context.analyzeWith(listSeparatorAnalyser);
                }
                final var executor = factory.get();
                executor.setList(context.executors);
                return SimpleAnalysisResult.success(ExpressionListAnalyser.class, context.input, executor);
            } else {
                // empty expression list
                return SimpleAnalysisResult.success(ExpressionListAnalyser.class, input, factory.get());
            }
        }

        private void analyzeFirstListSeparator(final AnalysisContext contex) {
        }
    }

    private class ExpressionAnalyzerPriorityN implements Analyzer {
        Factory<ListExecutor> factory = new SimpleListExecutorFactory();

        private final Analyzer underlying;
        private final Operator[] unaryPrefixes;
        private final Operator[] unaryPostfixes;
        private final Operator[] binaries;

        private final PostfixCall[] postfixCalls;

        private ExpressionAnalyzerPriorityN(final int priority) {
            if (priority == maxPriority) {
                underlying = new TagAnalyzer();
            } else {
                underlying = new ExpressionAnalyzerPriorityN(priority + 1);
            }
            unaryPrefixes = ExpressionAnalyzer.this.unaryPrefixes.stream().filter(op -> op.priority == priority).toArray(Operator[]::new);
            unaryPostfixes = ExpressionAnalyzer.this.unaryPostfixes.stream().filter(op -> op.priority == priority).toArray(Operator[]::new);
            binaries = ExpressionAnalyzer.this.binaries.stream().filter(op -> op.priority == priority).toArray(Operator[]::new);
            postfixCalls = ExpressionAnalyzer.this.postfixCalls.stream().filter(pfc -> pfc.priority == priority).toArray(PostfixCall[]::new);
        }

        /**
         * <pre>
         * {@code
         * EXP(X) =  unary* X (binaryOp_N X)* unary_N* |
         *          X (postfix_parameterized_operator_N_opening expressionList postfix_parameterized_operator_N_closing)+
         * }
         * </pre>
         *
         * @param input
         * @return the result of the analysis
         */
        @Override
        public AnalysisResult analyze(final SourceCode input) {
            final var context = new AnalysisContext(input);
            analyzeUnaryList(context, unaryPrefixes);
            context.analyzeWith(in -> analyseExpressionWithPostfix(context));
            if (context.failed()) {
                return FAIL;
            }
            context.analyzeWith(in -> operatorAnalyze(in, binaries));
            while (context.success()) {
                context.analyzeWith(in -> analyseExpressionWithPostfix(context));
                if (context.failed()) {
                    return FAIL;
                }
            }
            analyzeUnaryList(context, unaryPostfixes);
            return createResultWithExecutor(context);
        }

        private AnalysisResult analyseExpressionWithPostfix(AnalysisContext context) {
            context.analyzeWith(underlying);
            if (context.failed()) {
                return FAIL;
            }
            var postFixCall = findPostFixCall(context);
            while (context.success() && postFixCall != null) {
                context.analyzeWith(ExpressionAnalyzer.this.listAnalyser);
                if (context.failed()) {
                    return FAIL;
                }
                context.analyzeWith(postFixCall.parentheses.close);
                if (context.failed()) {
                    return FAIL;
                }
                postFixCall = findPostFixCall(context);
            }
            //TODO
            return null;
        }


        /**
         * Try all the postfix call openings. If one accepts the start of the input then step the context and return the
         * structure to the postfix call so  that the caller later can analyze the matching closing. For example, if
         * the opening is '(' then the closing will be ')', if the opening is '[' the colsing has to be ']'.
         * <p>
         * If there is no matching opening postfix call then return null and the context will be in a failed state.
         * Even though it is guaranteed that the returned value is not null when the context is success, the caller
         * is encouraged to check the nullity of the return value.
         *
         * @param context the analysis context before the
         * @return teh structure of the found post fix call
         */
        private PostfixCall findPostFixCall(final AnalysisContext context) {
            for (final var call : postfixCalls) {
                context.analyzeWith(call.parentheses.open);
                if (context.success()) {
                    return call;
                }
            }
            return null;
        }


        /**
         * Analyzes a list of unary operators and add the executors returned by the individual analysis result to the
         * executors in the context.
         * The analysis is always successful, because it is an {@code (unary)*} analysis, and should be treated by the
         * caller as such, even if the context state is failed.
         *
         * @param context   containing the analysis context
         * @param operators the array of operators that may be used on this level
         */
        private void analyzeUnaryList(final AnalysisContext context, final ExpressionAnalyzer.Operator[] operators) {
            context.analyzeWith(in -> operatorAnalyze(in, operators));
            while (context.success()) {
                context.analyzeWith(in -> operatorAnalyze(in, operators));
            }
        }

        /**
         * Analyze the input and return a successful result if the input starts with an operator that is on the current priority level.
         *
         * @param input     the input that may start with an operator
         * @param operators the array of the operators to check. These can be unary prefix, postfix or binary operators.
         * @return the analysis result
         */
        private AnalysisResult operatorAnalyze(final SourceCode input, final ExpressionAnalyzer.Operator[] operators) {
            for (final var operator : operators) {
                final var result = operator.operatorAnalyzer.analyze(input);
                if (result.wasSuccessful()) {
                    return result;
                }
            }
            return SimpleAnalysisResult.failed(ExpressionAnalyzerPriorityN.class);
        }
    }

    private SimpleAnalysisResult createResultWithExecutor(final AnalysisContext context) {
        final var executor = factory.get();
        executor.setList(context.executors);
        return SimpleAnalysisResult.success(ExpressionAnalyzerPriorityN.class, context.input, executor);
    }
}