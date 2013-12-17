package com.javax0.jdsl.analyzers;

public class WhiteSpaceSkippingAnalyzer extends SkippingAnalyzer {

    @Override
    protected int countCharacters(SourceCode input) {
        int i = 0;
        while (i < input.length() && Character.isWhitespace(input.charAt(i))) {
            i++;
        }
        return i;
    }
}
