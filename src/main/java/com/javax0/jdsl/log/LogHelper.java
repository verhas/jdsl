package com.javax0.jdsl.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.javax0.jdsl.analyzers.SourceCode;

public class LogHelper {
	public static Logger get() {
		Exception e = new Exception();
		StackTraceElement[] ste = e.getStackTrace();
		return LoggerFactory.getLogger(ste[1].getClassName());
	}

	private static final int MAX_DEBUG_CHARS = 16;

	public static String debugStringify(SourceCode input) {
		final String source = input.toString();
		final String debug;
		if (source.length() > MAX_DEBUG_CHARS) {
			debug = source.substring(0, MAX_DEBUG_CHARS) + "...";
		} else {
			debug = source;
		}
		return debug;
	}

	public static void logStart(Logger LOG, SourceCode input) {
		LOG.debug("Starting " + debugStringify(input));
	}
}
