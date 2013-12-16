package com.javax0.jdsl;

public class StringSourceCode implements SourceCode {
    private final String source;

    public StringSourceCode(String source) {
        this.source = source;
    }

    @Override
    public char charAt(int i) {
        return source.charAt(i);
    }

    @Override
    public int lenght() {
        return source.length();
    }

    @Override
    public SourceCode rest(int i) {
        if (i < source.length()) {
            return new StringSourceCode(source.substring(i));
        } else {
            return new StringSourceCode("");
        }
    }

}
