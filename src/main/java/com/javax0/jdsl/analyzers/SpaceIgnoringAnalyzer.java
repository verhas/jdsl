package com.javax0.jdsl.analyzers;

/**
 * SpaceIgnoringAnalyzer(s) are analyzers that ignore parts of the input,
 * typically white space. The parts to ignore is defined providing a
 * {@link SkippingAnalyzer}, typically an instance of the
 * {@link WhiteSpaceSkippingAnalyzer}.
 * 
 * @author Peter Verhas
 * 
 */
public abstract class SpaceIgnoringAnalyzer implements Analyzer {
    private SkippingAnalyzer skippingAnalyzer = null;

    public void setSkipAnalyzer(SkippingAnalyzer skipAnalyzer) {
        this.skippingAnalyzer = skipAnalyzer;
    }

    private SourceCode skip(SourceCode sc) {
        final SourceCode skippedSourceCode;
        if (skippingAnalyzer == null) {
            skippedSourceCode = sc;
        } else {
            AnalysisResult result = skippingAnalyzer.analyze(sc);
            skippedSourceCode = result.remainingSourceCode();
        }
        return skippedSourceCode;
    }

    abstract protected AnalysisResult analyze();

    @Override
    public final AnalysisResult analyze(final SourceCode input) {
        setInput(input);
        return analyze();
    }

    private SourceCode input;

    protected void setInput(SourceCode input) {
        this.input = skip(input);
    }

    protected SourceCode getInput() {
        return input;
    }
}
