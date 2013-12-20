package com.javax0.jdsl.analyzers;

import org.slf4j.Logger;

import com.javax0.jdsl.executors.Executor;
import static com.javax0.jdsl.log.LogHelper.debugStringify;
import static com.javax0.jdsl.log.LogHelper.get;

public class TerminalSymbolAnalyzer implements Analyzer {
	private static Logger LOG = get();
	private final String keyword;

	public TerminalSymbolAnalyzer(final String keyword) {
		this.keyword = keyword;
	}

	@Override
	public AnalysisResult analyze(final SourceCode input) {
		LOG.debug("Starting " + keyword + "? " + debugStringify(input));
		if (keyword.length() > input.length()) {
			LOG.debug("failed, input short");
			return SimpleAnalysisResult.failed();
		}

		for (int i = 0; i < keyword.length(); i++) {
			if (keyword.charAt(i) != input.charAt(i)) {
				LOG.debug("failed, does not match");
				return SimpleAnalysisResult.failed();
			}
		}
		LOG.debug("success");
		return SimpleAnalysisResult.success(input.rest(keyword.length()),
				Executor.NONE);
	}

}
