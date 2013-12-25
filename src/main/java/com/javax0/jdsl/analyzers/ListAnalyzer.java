package com.javax0.jdsl.analyzers;

import java.util.LinkedList;
import java.util.List;

import com.javax0.jdsl.executors.Executor;
import com.javax0.jdsl.executors.Factory;
import com.javax0.jdsl.executors.ListExecutor;
import com.javax0.jdsl.log.LogHelper;

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

	private final List<Analyzer> analyzerList = new LinkedList<>();

	protected List<Analyzer> getAnalyzerList() {
		return analyzerList;
	}

	/**
	 * Add one or more analyzers to the list of the analyzers that are used to
	 * analyze the source.
	 * 
	 * @param analyzers
	 */
	public void add(Analyzer... analyzers) {
		for (Analyzer analyzer : analyzers) {
			analyzerList.add(analyzer);
		}
	}

	private final Factory<ListExecutor> listExecutorFactory;

	/**
	 * Set the executor that will be returned by the analysis. During the
	 * analysis this executor will be furnished with the underlying executors
	 * from the result of the analysis performed by the list elements.
	 * 
	 * @param listExecutor
	 */
	public ListAnalyzer(Factory<ListExecutor> listExecutorFactory) {
		this.listExecutorFactory = listExecutorFactory;
	}

	private ListExecutor createExecutor(List<Executor> executors) {
		final ListExecutor listExecutor;
		if (listExecutorFactory != null) {
			listExecutor = listExecutorFactory.get();
			listExecutor.setList(executors);
		} else {
			listExecutor = null;
		}
		return listExecutor;
	}

	@Override
	public AnalysisResult analyze() {
		LogHelper.logStart(ListAnalyzer.class, getInput(), analyzerList);
		final List<Executor> executors = new LinkedList<>();

		for (Analyzer analyzer : analyzerList) {
			AnalysisResult result = analyzer.analyze(getInput());
			if (!result.wasSuccessful()) {
				return SimpleAnalysisResult.failed(ListAnalyzer.class);
			}
			if (result.getExecutor() != null) {
				executors.add(result.getExecutor());
			}
			setInput(result.remainingSourceCode());
		}

		return SimpleAnalysisResult.success(ListAnalyzer.class, getInput(),
				createExecutor(executors));
	}

	@Override
	public String toString() {
		return "[" + LogHelper.toString(analyzerList, ",") + "]";
	}
}
