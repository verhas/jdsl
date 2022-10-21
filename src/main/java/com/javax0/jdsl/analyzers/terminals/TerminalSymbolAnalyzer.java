package com.javax0.jdsl.analyzers.terminals;

import com.javax0.jdsl.analyzers.AnalysisResult;
import com.javax0.jdsl.analyzers.Rule;
import com.javax0.jdsl.analyzers.SimpleAnalysisResult;
import com.javax0.jdsl.analyzers.SourceCode;
import com.javax0.jdsl.executors.Executor;
import com.javax0.jdsl.log.Reporter;
import com.javax0.jdsl.log.ReporterFactory;

public class TerminalSymbolAnalyzer implements Rule {
    private final Reporter reporter = ReporterFactory.getReporter();

    public interface CharCompare {
        boolean isEqual(char a, char b);

        CharCompare caseSensitive = (a, b) -> a == b;
        CharCompare caseInsensitive = (a, b) -> Character.toLowerCase(a) == Character.toLowerCase(b);
    }

    private final CharCompare charCompare;

    private final String lexeme;

    private final Executor executor;

    /**
     * Create a terminal symbol analyser that has no executor and case sensitive.
     *
     * @param lexeme the lexeme of the terminal symbol
     * @return the new analyser
     */
    public static Rule analyzer(final String lexeme) {
        return analyzer(lexeme, Executor.NONE);
    }

    /**
     * Create a case-sensitive terminal symbol analyzer with an executor.
     *
     * @param lexeme   the lexeme of the terminal symbol
     * @param executor the executor of the terminal symbol. It may be needed in rare cases. Terminal symbols usually
     *                 only structure languages and do not come with an executor. In some cases they may  do.
     * @return the new analyser
     */
    public static Rule analyzer(final String lexeme, final Executor executor) {
        return new TerminalSymbolAnalyzer(lexeme, executor);
    }

    /**
     * Create a terminal symbol analyser with no executor and with the given character case sensitivity.
     *
     * @param lexeme      the lexeme of the terminal symbol
     * @param charCompare the character case sensitivity
     * @return the new analyser
     */
    public static Rule analyzer(final String lexeme, final CharCompare charCompare) {
        return new TerminalSymbolAnalyzer(lexeme, charCompare);
    }

    /**
     * Create a terminal symbol analyser with executor and with the given character case sensitivity.
     *
     * @param lexeme      the lexeme of the terminal symbol
     * @param charCompare the character case sensitivity
     * @param executor    the executor
     * @return the new analyser
     */
    public static Rule analyzer(final String lexeme, final CharCompare charCompare, final Executor executor) {
        return new TerminalSymbolAnalyzer(lexeme, charCompare, executor);
    }

    public TerminalSymbolAnalyzer(final String lexeme) {
        this(lexeme, Executor.NONE);
    }

    public TerminalSymbolAnalyzer(final String lexeme, final Executor executor) {
        this(lexeme, CharCompare.caseSensitive, null);
    }

    public TerminalSymbolAnalyzer(final String lexeme, CharCompare charCompare) {
        this(lexeme, charCompare, null);
    }

    public TerminalSymbolAnalyzer(final String lexeme, CharCompare charCompare, final Executor executor) {
        this.lexeme = lexeme;
        this.charCompare = charCompare;
        this.executor = executor;
    }

    @Override
    public AnalysisResult analyze(final SourceCode input) {
        reporter.logStart(TerminalSymbolAnalyzer.class, input, "%s?", lexeme);
        if (lexeme.length() > input.length()) {
            return SimpleAnalysisResult.failed(TerminalSymbolAnalyzer.class,
                    "input short");
        }

        for (int i = 0; i < lexeme.length(); i++) {
            if (!charCompare.isEqual(lexeme.charAt(i), input.charAt(i))) {
                return SimpleAnalysisResult
                        .failed(TerminalSymbolAnalyzer.class);
            }
        }
        return SimpleAnalysisResult.success(TerminalSymbolAnalyzer.class,
                input.rest(lexeme.length()), executor);
    }

    @Override
    public String toString() {
        return (lexeme);
    }
}
