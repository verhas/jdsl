package com.javax0.jdsl.analyzers;

/**
 * SourceCode represents the character stream of the program source code.
 * Implementation of SourceCode should be immutable objects. As the input source
 * is consumed new SourceCode objects are returned by the analyzers. Even though
 * the underlying character stream may be the same in a very similar way as
 * String is implemented in the Java language.
 * <p>
 * For a sample implementation have a look at {@link StringSourceCode}.
 * 
 * @author Peter Verhas
 * 
 */
public interface SourceCode {
    /**
     * Get the character at the position {@code i}. Indexing starts with zero.
     */
    char charAt(int i);

    /**
     * Get the number of characters that are available in the source code.
     */
    int length();

    /**
     * Get source code that represent the characters that follow the first
     * {@code i} characters of the original ({@code this}) source code.
     */
    SourceCode rest(int i);
}
