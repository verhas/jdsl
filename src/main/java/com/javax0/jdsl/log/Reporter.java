package com.javax0.jdsl.log;

import java.util.List;

import com.javax0.jdsl.analyzers.Analyzer;
import com.javax0.jdsl.analyzers.SourceCode;

public interface Reporter {

	void logStart(final Class<? extends Analyzer> klass, final SourceCode input);

	String toString(final List<Analyzer> analyzerList, final String sepChar);

	void logStart(final Class<? extends Analyzer> klass, final SourceCode input,
			final List<Analyzer> analyzerList);

	void logStart(final Class<? extends Analyzer> klass, final SourceCode input,
			final String message, final Object... params);

	void logSuccess(final Class<? extends Analyzer> klass);

	void logFail(final Class<?> klass, final String message);

	void logFail(final Class<? extends Analyzer> klass);
}
