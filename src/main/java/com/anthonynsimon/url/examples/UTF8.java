package com.anthonynsimon.url.examples;

import com.anthonynsimon.url.URL;
import com.anthonynsimon.url.exceptions.InvalidURLReferenceException;
import com.anthonynsimon.url.exceptions.MalformedURLException;

public class UTF8 {

    public static void main(String[] args) {
        try {
            // Parse URLs
            URL base = URL.parse("https://user:secret@example♬.com/path/to/my/dir#about");
            URL ref = URL.parse("./../file.html?search=germany&language=de_DE");

            // Parsed base
            base.getScheme(); // https
            base.getUsername(); // user
            base.getPassword(); // secret
            base.getHost(); // example♬.com
            base.getPath(); // /path/to/my/dir
            base.getFragment(); // about

            // Parsed reference
            ref.getPath(); // ./../file.html
            ref.getQueryPairs(); // Map<String, String> = {search=germany, language=de_DE}

            // Resolve them!
            URL resolved = base.resolveReference(ref); // https://user:secret@example♬.com/path/to/file.html?key=value
            resolved.getPath(); // /file.html

            // Escape UTF-8 result
            resolved.toString(); // https://user:secret@example%E2%99%AC.com/path/to/file.html?search=germany&language=de_DE

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InvalidURLReferenceException e) {
            e.printStackTrace();
        }
    }
}
