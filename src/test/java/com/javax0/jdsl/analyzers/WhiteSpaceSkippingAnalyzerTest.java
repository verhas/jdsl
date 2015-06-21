package com.javax0.jdsl.analyzers;

import static com.javax0.jdsl.analyzers.StringSourceCode.EMPTY_SOURCE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WhiteSpaceSkippingAnalyzerTest {

    @Test
    public void isSuccessfulForEmptySourceCode() {
    	assertTrue(new WhiteSpaceSkippingAnalyzer().analyze(EMPTY_SOURCE).wasSuccessful());
    }
    
    @Test
    public void returnsSameCodeForEmptySourceCode() {
    	assertEquals(EMPTY_SOURCE , new WhiteSpaceSkippingAnalyzer().analyze(EMPTY_SOURCE).remainingSourceCode());
    }
    
    @Test
    public void isSuccessfulTrimmingNonSpaceSourceCode() {
    	final SourceCode sourceCode = new StringSourceCode("Non-space source code.");
    	assertTrue(new WhiteSpaceSkippingAnalyzer().analyze(sourceCode).wasSuccessful());
    }
    
    @Test
    public void returnsSameSourceCodeWhenGivenNonSpaceSourceCode() {
    	final SourceCode sourceCode = new StringSourceCode("Non-space source code.");
    	assertEquals(sourceCode , new WhiteSpaceSkippingAnalyzer().analyze(sourceCode).remainingSourceCode());
    }
    
    @Test
    public void isSuccessfulForWhiteSpaceOnlySourceCode() {
    	final SourceCode sourceCode = new StringSourceCode("     ");
    	assertTrue(new WhiteSpaceSkippingAnalyzer().analyze(sourceCode).wasSuccessful());
    }
    
    @Test
    public void returnsEmptySourceCodeForWhiteSpaceOnlySourceCode() {
    	final SourceCode sourceCode = new StringSourceCode("     ");
    	assertEquals(EMPTY_SOURCE , new WhiteSpaceSkippingAnalyzer().analyze(sourceCode).remainingSourceCode());
    }

    
    @Test
    public void isSuccessfulTrimmingSpaceStartingSourceCode() {
    	final SourceCode sourceCode = new StringSourceCode("     space starting source code.");
    	assertTrue(new WhiteSpaceSkippingAnalyzer().analyze(sourceCode).wasSuccessful());
    }

    @Test
    public void trimsBeginningOfSourceCode() {
    	final String nonSpaceString = "non-space string";
    	final SourceCode sourceCode = new StringSourceCode("     " + nonSpaceString);
    	assertEquals(new StringSourceCode(nonSpaceString) , new WhiteSpaceSkippingAnalyzer().analyze(sourceCode).remainingSourceCode());
    }
}
