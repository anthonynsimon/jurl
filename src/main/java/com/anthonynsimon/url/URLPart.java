package com.anthonynsimon.url;

/**
 * URLPart is used to distinguish between the parts of the url when encoding/decoding.
 */
enum URLPart {
    CREDENTIALS,
    HOST,
    PATH,
    QUERY,
    FRAGMENT,
    ENCODE_ZONE,
}
