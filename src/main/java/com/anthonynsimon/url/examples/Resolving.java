package com.anthonynsimon.url.examples;

import com.anthonynsimon.url.URL;
import com.anthonynsimon.url.exceptions.InvalidURLReferenceException;
import com.anthonynsimon.url.exceptions.MalformedURLException;

public class Resolving {

    public static void main(String[] args) {
        try {

            URL base = URL.parse("http://example.com/dir/#fragment");
            URL ref = URL.parse("./../../file.html?key=value");

            base.getScheme(); // 'http'
            base.getHost(); // 'example.com'
            base.getPath(); // '/dir/'
            base.getFragment(); // 'fragment'

            ref.getPath(); // './../../file.html'
            ref.getQuery(); // 'key=value'

            URL resolved = base.resolveReference(ref); // 'http://example.com/file.html?key=value'

            resolved.getPath(); // '/file.html'

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InvalidURLReferenceException e) {
            e.printStackTrace();
        }
    }
}
