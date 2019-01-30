package com.javax0.jdsl.analyzers.terminals;

import com.javax0.jdsl.analyzers.AnalysisResult;
import com.javax0.jdsl.analyzers.Rule;
import com.javax0.jdsl.analyzers.SimpleAnalysisResult;
import com.javax0.jdsl.analyzers.SourceCode;
import com.javax0.jdsl.executors.TerminalSymbolExecutor;
import com.javax0.jdsl.log.Reporter;
import com.javax0.jdsl.log.ReporterFactory;

/**
 * Analyzes a string.
 * 
 *
 */
public class StringAnalyzer implements Rule {
	private final Reporter reporter = ReporterFactory.getReporter();
	private static final char DQ = '"';
	private static final char PQ = '\'';
	private static final char BS = '\\';

	private static boolean isDq(SourceCode input, int i) {
		return input.length() > i && input.charAt(i) == DQ;
	}

	private static boolean is3Dq(SourceCode input, int i) {
		return input.length() > i + 2 && input.charAt(i) == DQ
				&& input.charAt(i + 1) == DQ && input.charAt(i + 2) == DQ;
	}

	private static boolean isPq(SourceCode input, int i) {
		return input.length() > i && input.charAt(i) == PQ;
	}

	private static boolean is3Pq(SourceCode input, int i) {
		return input.length() > i + 2 && input.charAt(i) == PQ
				&& input.charAt(i + 1) == PQ && input.charAt(i + 2) == PQ;
	}

	private enum Terminator {
		DQ, PQ, DQ3, PQ3, NONE
	}

	private static Terminator getTerminator(SourceCode input) {
		final Terminator terminator;
		if (is3Dq(input, 0)) {
			terminator = Terminator.DQ3;
		} else if (isDq(input, 0)) {
			terminator = Terminator.DQ;
		} else if (is3Pq(input, 0)) {
			terminator = Terminator.PQ3;
		} else if (isPq(input, 0)) {
			terminator = Terminator.PQ;
		} else {
			terminator = Terminator.NONE;
		}
		return terminator;
	}

	private static boolean isTerminated(SourceCode input, int i,
			Terminator terminator) {
		final boolean result;
		switch (terminator) {
		case DQ:
			result = isDq(input, i);
			break;
		case PQ:
			result = isPq(input, i);
			break;
		case DQ3:
			result = is3Dq(input, i);
			break;
		case PQ3:
			result = is3Pq(input, i);
			break;
		default:
			result = false;
		}
		return result;
	}

	private static int length(Terminator terminator) {
		final int len;
		if (terminator == Terminator.PQ3 || terminator == Terminator.DQ3) {
			len = 3;
		} else {
			len = 1;
		}
		return len;
	}

	private static boolean singleLine(Terminator terminator) {
		return terminator == Terminator.PQ || terminator == Terminator.DQ;
	}

	/**
	 * Convert the character stored in {@code ch} to the character that this
	 * character means in a string when there is a \ before it.
	 * <p>
	 * Very simple n to \n, t to \t and r to \r. Anything else remains itself.
	 * Also " and ' and \.
	 */
	private static char convertEscapedChar(final char ch) {
		final char outputCh;
		switch (ch) {
		case (int) 'n':
			outputCh = '\n';
			break;
		case (int) 't':
			outputCh = '\t';
			break;
		case (int) 'r':
			outputCh = '\r';
			break;
		default:
			outputCh = ch;
			break;
		}
		return outputCh;
	}

	private static boolean isNL(char ch) {
		return ch == '\n' || ch == '\r';
	}

	@Override
	public AnalysisResult analyze(final SourceCode input) {
		reporter.logStart(StringAnalyzer.class, input);
		StringBuilder sb = new StringBuilder();
		final Terminator terminator = getTerminator(input);
		if (terminator == Terminator.NONE) {
			return SimpleAnalysisResult.failed(StringAnalyzer.class,
					"does not start as a string");
		}
		final boolean singleLine = singleLine(terminator);
		boolean escaped = false;
		for (int i = length(terminator); i < input.length(); i++) {
			if (escaped) {
				sb.append(convertEscapedChar(input.charAt(i)));
				escaped = false;
			} else {
				if (isTerminated(input, i, terminator)) {
					return SimpleAnalysisResult.success(StringAnalyzer.class,
							input.rest(i + length(terminator)),
                            new TerminalSymbolExecutor<>(sb.toString()));
				}
				if (singleLine && isNL(input.charAt(i))) {
					return SimpleAnalysisResult.failed(StringAnalyzer.class,
							"single line string not terminated before eol");
				}
				if (input.charAt(i) == BS) {
					escaped = true;
				} else {
					sb.append(input.charAt(i));
				}
			}
		}

		return SimpleAnalysisResult.failed(StringAnalyzer.class,
				"string not terminated");
	}

	public static final Rule INSTANCE = new StringAnalyzer();

	public static Rule string() {
		return INSTANCE;
	}

	@Override
	public String toString() {
		return "number";
	}
}
