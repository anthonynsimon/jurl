package com.anthonynsimon.url;

import com.anthonynsimon.url.exceptions.MalformedURLException;

/**
 * URLParser handles the parsing of a URL string into a URL object.
 */
interface URLParser {
    /**
     * Returns a the URL with the new values after parsing the provided URL string.
     */
    URL parse(String url) throws MalformedURLException;
}
