package com.anthonynsimon.url.examples;

import com.anthonynsimon.url.URL;
import com.anthonynsimon.url.exceptions.MalformedURLException;

public class Basic {

    public static void main(String[] args) {
        try {

            URL url = URL.parse("https://example.com/path/to/file.html/?q=my+values#fragment");

            url.getScheme(); // 'https'
            url.getHost(); // 'example.com'
            url.getPath(); // '/path/to/file.html'
            url.getQuery(); // 'q=my+values'
            url.getFragment(); // 'fragment'

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
