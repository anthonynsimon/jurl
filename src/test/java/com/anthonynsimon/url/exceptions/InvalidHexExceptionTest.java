package com.anthonynsimon.url.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InvalidHexExceptionTest {

    @Test(expected = InvalidHexException.class)
    public void testNoMessage() throws Exception {
        throw new InvalidHexException();
    }


    @Test(expected = InvalidHexException.class)
    public void testWithMessage() throws Exception {
        try {
            throw new InvalidHexException("with message");
        } catch (InvalidHexException e) {
            assertEquals("with message", e.getMessage());
        }
        throw new InvalidHexException("with message");
    }
}
