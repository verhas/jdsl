package com.javax0.jdsl.log;

import java.util.List;

import com.javax0.jdsl.analyzers.Analyzer;
import com.javax0.jdsl.analyzers.SourceCode;

public interface Reporter {
	/**
	 * Write a log message about the starting some analyzer.
	 */
	void logStart(final Class<? extends Analyzer> klass, final SourceCode input);

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
	String toString(final List<Analyzer> analyzerList, final String sepChar);

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
	void logStart(final Class<? extends Analyzer> klass,
			final SourceCode input, final List<Analyzer> analyzerList);

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
	void logStart(final Class<? extends Analyzer> klass,
			final SourceCode input, final String message,
			final Object... params);

	/**
	 * Log the success of an analyzer.
	 * 
	 * @param klass
	 *            the class of the analyzer that was successful.
	 */
	void logSuccess(final Class<? extends Analyzer> klass);

	/**
	 * Same as {@link #logFail(Class)} also giving a reason message.
	 */
	void logFail(final Class<?> klass, final String message);

	/**
	 * Log that an analyzer has failed.
	 * 
	 * @param klass
	 *            is the class of the analyzer that failed.
	 */
	void logFail(final Class<? extends Analyzer> klass);
}
