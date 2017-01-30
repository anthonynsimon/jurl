package com.anthonynsimon.url.examples;

import com.anthonynsimon.url.URL;
import com.anthonynsimon.url.exceptions.MalformedURLException;

import java.net.URI;
import java.net.URISyntaxException;

public class ToJavaClasses {

    public static void main(String[] args) {
        try {

            URL url = URL.parse("http://example.com/dir/#fragment");

            URI javaURI = url.toJavaURI(); // to java.net.URI

            java.net.URL javaURL = url.toJavaURL(); // to java.net.URL

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (java.net.MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
