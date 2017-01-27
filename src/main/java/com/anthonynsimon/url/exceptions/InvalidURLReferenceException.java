package com.anthonynsimon.url.exceptions;

/**
 * InvalidURLReferenceException is thrown when attempting to resolve a relative URL against an
 * absolute URL and something went wrong.
 */
public class InvalidURLReferenceException extends Exception {
    public InvalidURLReferenceException() {
        super();
    }

    public InvalidURLReferenceException(String message) {
        super(message);
    }
}
