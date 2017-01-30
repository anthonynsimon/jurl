# jurl
[![Build Status](https://travis-ci.org/anthonynsimon/jurl.svg?branch=master)](https://travis-ci.org/anthonynsimon/jurl/builds)  
Fast and simple URL parsing for Java with IPv6 support.

## Why
- Easy to use API - you just want to parse a URL after all.
- Fast - 5+ million URLs per second on commodity hardware.
- Supports path resolving between URLs (absolute and relative).
- Good test coverage with plenty of edge cases.
- Supports IPv4 and IPv6.
- No dependencies.

## Getting Started

Simple example:
```java
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
            ref.getQueryPairs(); // '{key=value}'

            URL resolved = base.resolveReference(ref); // 'http://example.com/file.html?key=value'

            resolved.getPath(); // '/file.html'

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (InvalidURLReferenceException e) {
            e.printStackTrace();
        }
    }
}
```

## Issues

The recommended medium to report and track issues is by opening one on Github.

## Contributing

Want to hack on the project? Any kind of contribution is welcome! Simply follow the next steps:

- Fork the project.
- Create a new branch.
- Make your changes and write tests when practical.
- Commit your changes to the new branch.
- Send a pull request, it will be reviewed shortly.

In case you want to add a feature, please create a new issue and briefly explain what the feature would consist of. For bugs or requests, before creating an issue please check if one has already been created for it.

## License

This project is licensed under the MIT license.
