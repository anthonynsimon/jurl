package com.anthonynsimon.url.exceptions;

/**
 * InvalidHexException is thrown when parsing a Hexadecimal was not
 * possible due to bad input.
 */
public class InvalidHexException extends Exception {
    public InvalidHexException() {
        super();
    }

    public InvalidHexException(String message) {
        super(message);
    }
}
