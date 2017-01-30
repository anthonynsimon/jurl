package com.anthonynsimon.url.examples;

import com.anthonynsimon.url.URL;
import com.anthonynsimon.url.exceptions.MalformedURLException;

public class QueryString {

    public static void main(String[] args) {
        try {

            URL url = URL.parse("http://example.com?one=uno&two=dos&three=tres");

            url.getQuery(); // 'one=uno&two=dos&three=tres'
            url.getQueryPairs(); // Map<String, String> = '{one=uno, two=dos, three=tres}'

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
