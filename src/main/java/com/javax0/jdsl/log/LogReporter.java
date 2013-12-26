package com.javax0.jdsl.log;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javax0.jdsl.analyzers.AlternativesAnalyzer;
import com.javax0.jdsl.analyzers.Analyzer;
import com.javax0.jdsl.analyzers.SourceCode;

/**
 * Some simple logging helper methods that help to generate logs for the
 * debugging of the grammars.
 * <p>
 * When using this library the logs are important to understand what is
 * happening during the syntax analysis process when there is some error in the
 * grammar definition. Therefore the logs generated in the library are not only
 * important to analyze and debug the library itself but rather the program that
 * uses the library. Although this is the case more or less in case of most of
 * the libraries in this case this is even more important. That is because
 * grammars are not simple things.
 * 
 * @author Peter Verhas
 * 
 */
public class LogReporter implements Reporter {

	private static final ThreadLocal<Integer> logTabSize = new ThreadLocal<Integer>() {

		@Override
		protected Integer initialValue() {
			return 0;
		}

	};

	private static int maxDebugChars = 16;

	/**
	 * Set the maximum number of characters of the input displayed when logging
	 * an analyzer start. By default this value is 16.
	 * <p>
	 * Log messages display this many characters of the input, and in case there
	 * are more than {@code maxDebugChars} in the input then elipsis {@code ...}
	 * is appended on the display.
	 */
	public static void setMaxDebugChars(final int maxDebugChars) {
		LogReporter.maxDebugChars = maxDebugChars;
	}

	private Logger getLogger(Class<?> klass){
		String loggerName = String.format("%23s",klass.getSimpleName());
		return LoggerFactory.getLogger(loggerName);
	}
	
	private static String debugStringify(final SourceCode input) {
		final String debug;
		if (input == null) {
			debug = null;
		} else {
			final String source = input.toString();
			if (source.length() > maxDebugChars) {
				debug = source.substring(0, maxDebugChars) + "...";
			} else {
				debug = source;
			}
		}
		return debug;
	}

	private static String dotTabbing() {
		final int tabSize = logTabSize.get();
		if (tabSize > 0) {
			final StringBuilder sb = new StringBuilder(tabSize);
			for (int i = 0; i < tabSize; i++) {
				sb.append(".");
			}
			return sb.toString();
		} else {
			return "";
		}
	}

	private static void modifyTab(final int diff) {
		int tabSize = logTabSize.get() + diff;
		if (tabSize < 0) {
			tabSize = 0;
		}
		logTabSize.set(tabSize);
	}

	private static void incTab() {
		modifyTab(1);
	}

	private static void decTab() {
		modifyTab(-1);
	}

	/**
	 * Write a log message about the starting some analyzer.
	 * 
	 * @param LOG
	 * @param input
	 */
	public void logStart(final Class<? extends Analyzer> klass,
			final SourceCode input) {
		final Logger log = getLogger(klass);
		log.debug(dotTabbing() + "Starting " + debugStringify(input));
		incTab();
	}

	/**
	 * Convert a list of analyzers into string representation proper for
	 * logging/debugging purposes.
	 * 
	 * @param analyzerList
	 *            the list of the analyzers
	 * @param sepChar
	 *            is the separator character, {@code ,} when this is a list and
	 *            {@code |} when the list is used by an alternatives analyzer.
	 */
	public String toString(final List<Analyzer> analyzerList,
			final String sepChar) {
		final StringBuilder sb = new StringBuilder(analyzerList.size() * 12);
		for (final Analyzer analyzer : analyzerList) {
			if (sb.length() > 0) {
				sb.append(sepChar);
			}
			sb.append(analyzer);
		}
		return sb.toString();
	}

	/**
	 * Log that an analyzer has started to analyze the input.
	 * 
	 * @param klass
	 *            the class of the analyzer
	 * @param input
	 *            is the source when the analyzer starts
	 * @param analyzerList
	 *            is the underlying analyzer of a list analyzer or alternatives
	 *            analyzer.
	 */
	public void logStart(final Class<? extends Analyzer> klass,
			final SourceCode input, final List<Analyzer> analyzerList) {
		final Logger log = getLogger(klass);
		final String sep;
		if (klass.isAssignableFrom(AlternativesAnalyzer.class)) {
			sep = "|";
		} else {
			sep = ",";
		}
		log.debug(dotTabbing() + "Starting [" + toString(analyzerList, sep)
				+ "] " + debugStringify(input));
		incTab();
	}

	/**
	 * Log that an analyzer has started to analyze the input.
	 * 
	 * @param klass
	 *            the class of the analyzer
	 * @param input
	 *            is the source when the analyzer starts
	 * @param message
	 *            is optional message presumably containing
	 *            {@code String.format} parameters
	 * @param params
	 *            parameters used to format the message.
	 */
	public void logStart(final Class<? extends Analyzer> klass,
			final SourceCode input, final String message,
			final Object... params) {
		final String formattedMessage = String.format(message, params);
		final Logger log = getLogger(klass);
		log.debug(dotTabbing() + "Starting " + formattedMessage + " "
				+ debugStringify(input));
		incTab();
	}

	/**
	 * Log the success of an analyzer.
	 * 
	 * @param klass
	 *            the class of the analyzer that was successful.
	 */
	public void logSuccess(final Class<? extends Analyzer> klass) {
		final Logger log = getLogger(klass);
		decTab();
		log.debug(dotTabbing() + "success");
	}

	/**
	 * Same as {@link #logFail(Class)} also giving a reason message.
	 */
	public void logFail(final Class<?> klass, final String message) {
		final Logger log = getLogger(klass);
		decTab();
		log.debug(dotTabbing() + "fail " + message);
	}

	/**
	 * Log that an analyzer has failed.
	 * 
	 * @param klass
	 *            is the class of the analyzer that failed.
	 */
	public void logFail(final Class<? extends Analyzer> klass) {
		final Logger log = getLogger(klass);
		decTab();
		log.debug(dotTabbing() + "fail");
	}
}
