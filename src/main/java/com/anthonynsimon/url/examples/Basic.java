package com.anthonynsimon.url.examples;

import com.anthonynsimon.url.URL;

public class Basic {

    public static void main(String[] args) {
        try {
            URL url = URL.parse("https://example.com/path/to/file.html/?q=my+values#fragment");

            url.scheme(); // 'https'
            url.host(); // 'example.com'
            url.path(); // '/path/to/file.html'
            url.query(); // 'q=my+values'
            url.fragment(); // 'fragment'

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
