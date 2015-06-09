package com.javax0.jdsl.analyzers;

import static com.javax0.jdsl.analyzers.StringSourceCode.sourceCode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

public class StringSourceCodeTest {

	@Test(expected = IllegalArgumentException.class)
	public void willNotAcceptNull() {
		sourceCode(null);
		Assert.fail();
	}

	private static final String SOME_STRING = "some string";

	@Test
	public void createsSourceCodeFromSomeString() {
		assertNotNull(sourceCode(SOME_STRING));
	}

	@Test
	public void sourceCodeLengthIsSameAsStringLength() {
		assertEquals(SOME_STRING.length(), sourceCode(SOME_STRING).length());
	}

	@Test
	public void sourceCodeGivesBackCharactersOfTheString() {
		final SourceCode sc = sourceCode(SOME_STRING);
		for (int i = 0; i < sc.length(); i++) {
			assertEquals(SOME_STRING.charAt(i), sc.charAt(i));
		}
	}

	@Test
	public void sourceCodeGivesBack_Ith_CharacterOfStringAsRest_I() {
		final SourceCode sc = sourceCode(SOME_STRING);
		for (int i = 0; i < sc.length(); i++) {
			assertEquals(SOME_STRING.charAt(i), sc.rest(i).charAt(0));
		}
	}

	@Test
	public void restZeroGivesBackTheSameSourceCodeObject() {
		final SourceCode sc = sourceCode(SOME_STRING);
		assertTrue(sc == sc.rest(0));
	}

	@Test
	public void restTotalLengthGivesBack_The_EmptySourceObject() {
		assertTrue(StringSourceCode.EMPTY_SOURCE == sourceCode(SOME_STRING)
				.rest(SOME_STRING.length()));
	}
}
