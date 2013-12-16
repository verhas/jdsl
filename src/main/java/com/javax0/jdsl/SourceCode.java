package com.javax0.jdsl;

/**
 * SourceCode represents the character stream of the program source code.
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
	int lenght();

	/**
	 * Get source code that represent the characters that follow the first
	 * {@code i} characters of the original source code.
	 */
	SourceCode rest(int i);
}
