package com.javax0.jdsl.analyzers;

public class NumberAnalyzer implements Analyzer {

    private boolean isIndexInRange(int i, SourceCode input) {
        return i < input.length();
    }

    private boolean isDigit(int i, SourceCode input) {
        return isIndexInRange(i, input) && Character.isDigit(input.charAt(i));
    }

    private boolean isSignChar(int i, SourceCode input) {
        return isIndexInRange(i, input) && (input.charAt(i) == '+' || input.charAt(i) == '-');
    }

    private boolean isChar(int i, SourceCode input, char... chars) {
        if (!isIndexInRange(i, input)) {
            return false;
        }
        for (char ch : chars) {
            if (input.charAt(i) == ch) {
                return true;
            }
        }
        return false;
    }

    @Override
    public AnalysisResult analyze(SourceCode input) {
        int i = 0;
        long sig = 1;
        if (isSignChar(i, input)) {
            if (input.charAt(0) == '-') {
                sig = -1;
            }
            i++;
        }
        if (Character.isDigit(input.charAt(i))) {
            long result = 0;
            while (isDigit(i, input)) {
                result = 10 * result + input.charAt(i) - '0';
                i++;
            }
            if (!isChar(i, input, '.', 'e', 'E')) {
                return SimpleAnalysisResult.success(input.rest(i), new TerminalSymbolExecutor<Long>(result * sig));
            }
            double mantissa = (double) result;
            if (input.charAt(i) == '.') {
                i++;
            }
            double fractionMultiplier = 0.1;
            while (isDigit(i, input)) {
                mantissa += (input.charAt(i) - '0') * fractionMultiplier;
                fractionMultiplier *= 0.1;
                i++;
            }
            if (isChar(i, input, 'e', 'E')) {
                i++;
                double esig = 1.0;
                if (isSignChar(i, input)) {
                    if (input.charAt(i) == '-') {
                        esig = -1.0;
                    }
                    i++;
                }
                double exponent = 0.0;
                while (isDigit(i, input)) {
                    exponent = exponent * 10 + (double) (input.charAt(i) - '0');
                    i++;
                }
                exponent *= esig;
                return SimpleAnalysisResult.success(input.rest(i), new TerminalSymbolExecutor<Double>(mantissa * sig * Math.pow(10.0, exponent)));
            } else {
                return SimpleAnalysisResult.success(input.rest(i), new TerminalSymbolExecutor<Double>(mantissa * sig));
            }
        } else {
            return SimpleAnalysisResult.failed();
        }
    }
}
