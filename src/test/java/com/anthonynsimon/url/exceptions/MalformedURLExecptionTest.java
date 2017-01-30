package com.anthonynsimon.url.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MalformedURLExecptionTest {

    @Test(expected = MalformedURLException.class)
    public void testNoMessage() throws Exception {
        throw new MalformedURLException();
    }


    @Test(expected = MalformedURLException.class)
    public void testWithMessage() throws Exception {
        try {
            throw new MalformedURLException("with message");
        } catch (MalformedURLException e) {
            assertEquals("with message", e.getMessage());
        }
        throw new MalformedURLException("with message");
    }
}
