package com.anthonynsimon.url.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InvalidURLReferenceExceptionTest {

    @Test(expected = InvalidURLReferenceException.class)
    public void testNoMessage() throws Exception {
        throw new InvalidURLReferenceException();
    }


    @Test(expected = InvalidURLReferenceException.class)
    public void testWithMessage() throws Exception {
        try {
            throw new InvalidURLReferenceException("with message");
        } catch (InvalidURLReferenceException e) {
            assertEquals("with message", e.getMessage());
        }
        throw new InvalidURLReferenceException("with message");
    }
}
