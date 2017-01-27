package com.anthonynsimon.url.examples;

import com.anthonynsimon.url.URL;

public class Main {

    public static void main(String[] args) {
        try {
            URL base = URL.parse("http://example.com/dir/#fragment");
            URL ref = URL.parse("./../../file.html?key=value");

            base.scheme(); // 'http'
            base.host(); // 'example.com'
            base.path(); // '/dir/'
            base.fragment(); // 'fragment'

            ref.path(); // './../../file.html'
            ref.query(); // 'key=value'

            URL resolved = base.resolveReference(ref); // 'http://example.com/file.html?key=value'

            resolved.path(); // '/file.html'
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
