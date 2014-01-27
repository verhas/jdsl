package com.javax0.jdsl.analyzers.terminals;

import com.javax0.jdsl.analyzers.AnalysisResult;
import com.javax0.jdsl.analyzers.Rule;
import com.javax0.jdsl.analyzers.SimpleAnalysisResult;
import com.javax0.jdsl.analyzers.SourceCode;
import com.javax0.jdsl.executors.TerminalSymbolExecutor;
import com.javax0.jdsl.log.Reporter;
import com.javax0.jdsl.log.ReporterFactory;

/**
 * Analyzes an integer or floating point decimal number.
 * 
 * A number can have an optional '+' or '-' sign, then at least one digit,
 * optional fractional part starting with a '.' and optional exponential part.
 * The exponential part starts with 'e' or 'E', has optional '+' or '-' and
 * digits. This is just the usual and normal number representation. The result
 * of the analysis will contain an executor that returns the analyzed value of
 * the number, either as a {@code Long} or {@code Double}.
 * 
 * @author Peter Verhas
 * 
 */
public class NumberAnalyzer implements Rule {
	private final Reporter reporter = ReporterFactory.getReporter();

	private boolean isIndexInRange(final int i, final SourceCode input) {
		return i < input.length();
	}

	private boolean isDigit(final int i, final SourceCode input) {
		return isIndexInRange(i, input) && Character.isDigit(input.charAt(i));
	}

	private boolean isSignChar(final int i, final SourceCode input) {
		return isIndexInRange(i, input)
				&& (input.charAt(i) == '+' || input.charAt(i) == '-');
	}

	private boolean isChar(final int i, final SourceCode input,
			final char... chars) {
		if (!isIndexInRange(i, input)) {
			return false;
		}
		for (final char ch : chars) {
			if (input.charAt(i) == ch) {
				return true;
			}
		}
		return false;
	}

	@Override
	public AnalysisResult analyze(final SourceCode input) {
		reporter.logStart(NumberAnalyzer.class, input);
		int i = 0;
		long sig = 1;
		if (isSignChar(i, input)) {
			if (input.charAt(0) == '-') {
				sig = -1;
			}
			i++;
		}
		if (isIndexInRange(i, input) && Character.isDigit(input.charAt(i))) {
			long result = 0;
			while (isDigit(i, input)) {
				result = 10 * result + input.charAt(i) - '0';
				i++;
			}
			if (!isChar(i, input, '.', 'e', 'E')) {
				return SimpleAnalysisResult.success(NumberAnalyzer.class,
						input.rest(i), new TerminalSymbolExecutor<Long>(result
								* sig));
			}
			double mantissa = (double) result;
			if (input.charAt(i) == '.') {
				i++;
			}
			double fractionMultiplier = 0.1;
			while (isDigit(i, input)) {
				mantissa += (input.charAt(i) - '0') * fractionMultiplier;
				fractionMultiplier *= 0.1;
				i++;
			}
			if (isChar(i, input, 'e', 'E')) {
				i++;
				double esig = 1.0;
				if (isSignChar(i, input)) {
					if (input.charAt(i) == '-') {
						esig = -1.0;
					}
					i++;
				}
				double exponent = 0.0;
				while (isDigit(i, input)) {
					exponent = exponent * 10 + (double) (input.charAt(i) - '0');
					i++;
				}
				exponent *= esig;
				return SimpleAnalysisResult.success(NumberAnalyzer.class, input
						.rest(i), new TerminalSymbolExecutor<Double>(mantissa
						* sig * Math.pow(10.0, exponent)));
			} else {
				return SimpleAnalysisResult.success(NumberAnalyzer.class, input
						.rest(i), new TerminalSymbolExecutor<Double>(mantissa
						* sig));
			}
		} else {
			return SimpleAnalysisResult.failed(NumberAnalyzer.class);
		}
	}

	private static final Rule INSTANCE = new NumberAnalyzer();
	
	public static final Rule number() {
		return INSTANCE;
	}

	@Override
	public String toString() {
		return "number";
	}
}
