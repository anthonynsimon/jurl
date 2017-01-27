package com.anthonynsimon.url.exceptions;

/**
 * MalformedURLException is thrown when parsing a URL or part of it and it was not
 * possible to complete the operation due to bad input.
 */
public class MalformedURLException extends Exception {
    public MalformedURLException() {
        super();
    }

    public MalformedURLException(String message) {
        super(message);
    }
}
