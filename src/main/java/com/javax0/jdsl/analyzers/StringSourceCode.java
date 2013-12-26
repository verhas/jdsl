package com.javax0.jdsl.analyzers;

/**
 * SourceCode implementation that has an underlying String.
 * 
 * @author Peter Verhas
 * 
 */
public class StringSourceCode implements SourceCode {
	public static final SourceCode EMPTY_SOURCE = new StringSourceCode("");

	private final String source;

	public StringSourceCode(final String source) {
		if (source == null) {
			throw new IllegalArgumentException(
					"source can not be noll when constructing StringSourceCode object");
		}
		this.source = source;
	}

	@Override
	public char charAt(final int i) {
		return source.charAt(i);
	}

	@Override
	public int length() {
		return source.length();
	}

	@Override
	public SourceCode rest(final int i) {
		final SourceCode result;
		if (i < source.length()) {
			if (i == 0) {
				result = this;
			} else {
				result = new StringSourceCode(source.substring(i));
			}
		} else {
			result = EMPTY_SOURCE;
		}
		return result;
	}

	@Override
	public String toString() {
		return source;
	}

	@Override
	public int hashCode() {
		return source.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return source.equals(((StringSourceCode) obj).source);
	}

}
