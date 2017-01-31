package com.anthonynsimon.url.examples;

import com.anthonynsimon.url.URL;
import com.anthonynsimon.url.exceptions.MalformedURLException;

import java.text.DecimalFormat;

public class ParsesPerSecond {

    public static void main(String[] args) {
        try {
            double average = 0;
            int n = 20000000;

            for (int i = 0; i < n; i++) {
                long start = System.currentTimeMillis();
                URL url = URL.parse("https://example.com/path/to/file.html/?q=my+values#fragment");
                average += System.currentTimeMillis() - start;
            }

            average /= n;
            double perSecond = 1000.0 / (double) average;
            DecimalFormat formatter = new DecimalFormat();
            System.out.println(formatter.format(perSecond) + " parses per second");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
