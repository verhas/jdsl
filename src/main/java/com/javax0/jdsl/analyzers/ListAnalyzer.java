package com.javax0.jdsl.analyzers;

import java.util.LinkedList;
import java.util.List;

import com.javax0.jdsl.executors.Executor;
import com.javax0.jdsl.executors.Factory;
import com.javax0.jdsl.executors.ListExecutor;
import com.javax0.jdsl.log.Reporter;
import com.javax0.jdsl.log.ReporterFactory;

/**
 * Implements an analyzer that accepts a code if the underlying analyzers accept
 * the starts of the input one after the other.
 * <p>
 * If there is any skipping analyzer defined then it is used before the list,
 * between the list elements and after the list.
 * 
 * @author Peter Verhas
 * 
 */
public class ListAnalyzer extends SpaceIgnoringAnalyzer {
	private final Reporter reporter = ReporterFactory.getReporter();
	private final List<Analyzer> analyzerList = new LinkedList<>();

	protected List<Analyzer> getAnalyzerList() {
		return analyzerList;
	}

	public void add(final List<Analyzer> analyzers) {
        analyzerList.addAll(analyzers);
    }

	/**
	 * Add one or more analyzers to the list of the analyzers that are used to
	 * analyze the source.
	 * 
	 * @param analyzers
	 */
	public void add(final Analyzer... analyzers) {
		for (final Analyzer analyzer : analyzers) {
			analyzerList.add(analyzer);
		}
	}

	/**
	 * Set the executor that will be returned by the analysis. During the
	 * analysis this executor will be furnished with the underlying executors
	 * from the result of the analysis performed by the list elements.
	 */
	public ListAnalyzer(final Factory<ListExecutor> listExecutorFactory) {
		super(listExecutorFactory);
	}

	@Override
	public AnalysisResult analyze() {
		reporter.logStart(ListAnalyzer.class, getInput(), analyzerList);
		final List<Executor> executors = new LinkedList<>();
		final List<State> states = new LinkedList<>();

		for (final Analyzer analyzer : analyzerList) {
			final AnalysisResult result = analyzer.analyze(getInput());
			if (!result.wasSuccessful()) {
				return SimpleAnalysisResult.failed(ListAnalyzer.class);
			}
			advanceList(result, executors, states);
		}

		return SimpleAnalysisResult.success(ListAnalyzer.class, getInput(),
				createExecutor(executors), new ListAnalysisState(states));
	}

	@Override
	public String toString() {
		return "[" + reporter.toString(analyzerList, ",") + "]";
	}
}
