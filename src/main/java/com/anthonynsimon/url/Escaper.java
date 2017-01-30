package com.anthonynsimon.url;

import com.anthonynsimon.url.exceptions.MalformedURLException;

/**
 * An escaper handles the escaping and unescaping of characters in URLs. It escapes characters
 * based on the zone (part of the URL) that is being dealt with.
 */
interface Escaper {
    /**
     * Returns a percent-escaped string. Each character will be evaluated in case it needs to be escaped
     * based on the provided EncodeZone.
     */
    String escape(String str, URLPart zone);

    /**
     * Returns an unescaped string.
     *
     * @throws MalformedURLException if an invalid escape sequence is found.
     */
    public String unescape(String str) throws MalformedURLException;
}
