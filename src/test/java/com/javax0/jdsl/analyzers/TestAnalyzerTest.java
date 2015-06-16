package com.javax0.jdsl.analyzers;


import static com.javax0.jdsl.analyzers.MockAnalyzerGeneratorUtil.newFailingAnalyzer;
import static com.javax0.jdsl.analyzers.MockAnalyzerGeneratorUtil.newSuccessfulAnalyzer;
import static com.javax0.jdsl.analyzers.TestAnalyzer.not;
import static com.javax0.jdsl.analyzers.TestAnalyzer.test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestAnalyzerTest {

	@Test
	public void failsWhenUnderlyingAnalyzerFails() {
		assertFalse(test(newFailingAnalyzer()).analyze(null).wasSuccessful());
	}
	@Test
	public void failsWhenNegatedUnderlyingAnalyzerIsSuccessful() {
		assertFalse(not(newSuccessfulAnalyzer()).analyze(null).wasSuccessful());
	}
	
	@Test
	public void isSuccessfulWhenUnderlyingAnalyzerIsSuccessful() {		
		assertTrue(test(newSuccessfulAnalyzer()).analyze(null).wasSuccessful());
	}
	
	@Test
	public void hasNullStateWhenUnderlyingAnalyzerIsSuccessful() {		
		assertNull(test(newSuccessfulAnalyzer()).analyze(null).getState());
	}
	
	@Test
	public void hasNullExecutorWhenUnderlyingAnalyzerIsSuccessful() {				
		assertNull(test(newSuccessfulAnalyzer()).analyze(null).getExecutor());
	}
	
	@Test
	public void isSuccessfulWhenNegatedUnderlyingAnalyzerFails() {		
		assertTrue(not(newFailingAnalyzer()).analyze(null).wasSuccessful());
	}
	
	@Test
	public void hasNullStateWhenNegatedUnderlyingAnalyzerFails() {		
		assertNull(not(newFailingAnalyzer()).analyze(null).getState());
	}
	
	@Test
	public void hasNullExecutorWhenNegatedUnderlyingAnalyzerFails() {				
		assertNull(not(newFailingAnalyzer()).analyze(null).getExecutor());
	}
}
