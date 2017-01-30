# jurl
[![Build Status](https://travis-ci.org/anthonynsimon/jurl.svg?branch=master)](https://travis-ci.org/anthonynsimon/jurl/builds) 
[![Test Coverage](https://codecov.io/gh/anthonynsimon/jurl/branch/master/graph/badge.svg)](https://codecov.io/gh/anthonynsimon/jurl)

Fast and simple URL parsing for Java with UTF-8 support.

## Why
- Easy to use API - you just want to parse a URL after all.
- Fast - 5+ million URLs per second on commodity hardware.
- UTF-8 encoding and decoding.
- Supports path resolving between URLs (absolute and relative).
- Good test coverage (94%) with plenty of edge cases.
- Supports IPv4 and IPv6.
- No dependencies.

## Getting Started

Example:
```java
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
