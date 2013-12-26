package com.javax0.jdsl.analyzers;

import org.junit.Assert;
import org.junit.Test;

import com.javax0.jdsl.analyzers.SourceCode;
import com.javax0.jdsl.analyzers.StringSourceCode;

public class StringSourceCodeTest {

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void given_NullObjectString_when_CreatingStringSourceCodeObject_then_ThrowsException() {
		final String source;
		GIVEN: {
			source = null;
		}
		WHEN: {
			final SourceCode sc = new StringSourceCode(source);
		}
		THEN: {
			Assert.fail();
		}
	}

	private static final String SOME_STRING = "some string";

	@SuppressWarnings("unused")
	@Test
	public void given_SomeString_when_CreatingStringSourceCodeObject_then_ItIsCreated() {
		final String source;
		GIVEN: {
			source = SOME_STRING;
		}
		final SourceCode sc;
		WHEN: {
			sc = new StringSourceCode(source);
		}
		THEN: {
			Assert.assertNotNull(sc);
		}
	}

	@SuppressWarnings("unused")
	@Test
	public void given_SomeString_when_CreatingStringSourceCodeObject_then_ItHasTheLengthOfTheString() {
		final String source;
		GIVEN: {
			source = SOME_STRING;
		}
		final SourceCode sc;
		WHEN: {
			sc = new StringSourceCode(source);
		}
		THEN: {
			Assert.assertEquals(source.length(), sc.length());
		}
	}

	@SuppressWarnings("unused")
	@Test
	public void given_SomeString_when_CreatingStringSourceCodeObject_then_ItReturnsTheCharactersOfTheString() {
		final String source;
		GIVEN: {
			source = SOME_STRING;
		}
		final SourceCode sc;
		WHEN: {
			sc = new StringSourceCode(source);
		}
		THEN: {
			for (int i = 0; i < sc.length(); i++) {
				Assert.assertEquals(source.charAt(i), sc.charAt(i));
			}
		}
	}

	@SuppressWarnings("unused")
	@Test
	public void given_SomeString_when_CreatingStringSourceCodeObject_then_GettingTheRestGivesProperSource() {
		final String source;
		GIVEN: {
			source = SOME_STRING;
		}
		final SourceCode sc;
		WHEN: {
			sc = new StringSourceCode(source);
		}
		THEN: {
			for (int i = 0; i < sc.length(); i++) {
				Assert.assertEquals(source.charAt(i), sc.rest(i).charAt(0));
			}
		}
	}

	@SuppressWarnings("unused")
	@Test
	public void given_SomeString_when_CreatingStringSourceCodeObject_then_RestZeroReturnsTheSameObject() {
		final String source;
		GIVEN: {
			source = SOME_STRING;
		}
		final SourceCode sc;
		WHEN: {
			sc = new StringSourceCode(source);
		}
		THEN: {
			Assert.assertTrue(sc == sc.rest(0));
		}
	}

	@SuppressWarnings("unused")
	@Test
	public void given_SomeString_when_CreatingStringSourceCodeObject_then_RestPastEntReturnsTheEmptySourceCodeObject() {
		final String source;
		GIVEN: {
			source = SOME_STRING;
		}
		final SourceCode sc;
		WHEN: {
			sc = new StringSourceCode(source);
		}
		THEN: {
			Assert.assertTrue(StringSourceCode.EMPTY_SOURCE == sc.rest(source
					.length()));
		}
	}
}
