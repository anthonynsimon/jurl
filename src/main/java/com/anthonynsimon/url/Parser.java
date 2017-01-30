package com.anthonynsimon.url;

import com.anthonynsimon.url.exceptions.MalformedURLException;

/**
 * Parser handles the parsing of a URL string into a URL object.
 */
interface Parser {
    /**
     * Returns a the URL with the new values after parsing the provided URL string.
     */
    URL parse(String url) throws MalformedURLException;
}
