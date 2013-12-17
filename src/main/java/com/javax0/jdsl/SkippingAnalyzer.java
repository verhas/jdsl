package com.javax0.jdsl;

/**
 * A skipping analyzer is an analyzer that eats up character from the start of
 * the input but does not produce any executor. The best example is the
 * {@link WhiteSpaceSkippingAnalyzer} that eats up whitespaces.
 * <p>
 * SkippingAnalyzers can be used in {@link SpaceIgnoringAnalyzer}s.
 * <p>
 * Skipping analyzers are always successful. If there is no character to ignore
 * then the skipping analyzers return in their result the original input.
 * 
 * @author verhasp
 * 
 */
public abstract class SkippingAnalyzer implements Analyzer {
    private final static Executor NO_EXECUTOR = null;

    protected abstract int countCharacters(SourceCode input);

    @Override
    public final AnalysisResult analyze(SourceCode input) {
        int numberOfSkippedCharactets = countCharacters(input);
        final AnalysisResult result;
        if (numberOfSkippedCharactets < input.length()) {
            if (numberOfSkippedCharactets == 0) {
                result = SimpleAnalysisResult.success(input, NO_EXECUTOR);
            } else {
                result = SimpleAnalysisResult.success(input.rest(numberOfSkippedCharactets), NO_EXECUTOR);
            }
        } else {
            result = SimpleAnalysisResult.success(StringSourceCode.EMPTY_SOURCE, NO_EXECUTOR);
        }
        return result;
    }
}
