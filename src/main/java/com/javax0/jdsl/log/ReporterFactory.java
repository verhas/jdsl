package com.javax0.jdsl.log;

public class ReporterFactory {
	private static Reporter reporter;

	public static void setReporter(final Reporter reporter) {
		ReporterFactory.reporter = reporter;
	}

	public static Reporter getReporter() {
		if (reporter == null) {
			reporter = new LogReporter();
		}
		return reporter;
	}

}
