package com.javax0.jdsl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class KeywordAnalyzer implements Analyzer {

    private Map<String, Integer> map = new HashMap<>();

    public void keyword(String keyword, Integer token) {
        map.put(keyword, token);
    }

    private StringBuilder startOfInput;

    private void fillUpBufferTo(int length, SourceCode input) {
        for (int i = startOfInput.length(); i < length; i++) {
            startOfInput.append(input.charAt(i));
        }
    }

    private TerminalSymbolExecutor<Integer> token(Integer t) {
        return new TerminalSymbolExecutor<Integer>(t);
    }

    @Override
    public AnalysisResult analyze(SourceCode input) {
        startOfInput = new StringBuilder();
        for (Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getKey().length() <= input.lenght()) {
                if (entry.getKey().length() > startOfInput.length()) {
                    fillUpBufferTo(entry.getKey().length(), input);
                }
            }
            if (startOfInput.toString().startsWith(entry.getKey())) {
                return SimpleAnalysisResult.success(input.rest(entry.getKey().length()), token(entry.getValue()));
            }
        }
        return SimpleAnalysisResult.failed();
    }

}
