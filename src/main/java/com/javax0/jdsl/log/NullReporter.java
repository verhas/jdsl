package com.javax0.jdsl.log;

import java.util.List;

import com.javax0.jdsl.analyzers.Analyzer;
import com.javax0.jdsl.analyzers.SourceCode;

public class NullReporter implements Reporter {

	public void logStart(final Class<? extends Analyzer> klass,
			final SourceCode input) {
	}

	public String toString(final List<Analyzer> analyzerList,
			final String sepChar) {
		return "";
	}

	public void logStart(final Class<? extends Analyzer> klass,
			final SourceCode input, final List<Analyzer> analyzerList) {
	}

	public void logStart(final Class<? extends Analyzer> klass,
			final SourceCode input, final String message,
			final Object... params) {
	}

	public void logSuccess(final Class<? extends Analyzer> klass) {
	}

	public void logFail(final Class<?> klass, final String message) {
	}

	public void logFail(final Class<? extends Analyzer> klass) {
	}
}
