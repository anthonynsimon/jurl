# jurl
[![Build Status](https://travis-ci.org/anthonynsimon/jurl.svg?branch=master)](https://travis-ci.org/anthonynsimon/jurl/builds)  
Fast and simple URL parsing for Java with IPv6 support.

## Why
- Parse URLs and get the result you would expect.
- Easy to use API - you just want to parse a URL.
- Fast - 2+ million URLs per second on a regular laptop.
- Good test coverage with plenty of edge cases.
- Supports IPv4 and IPv6 with zone identifiers.
- No dependencies.

## Getting Started

Simple example:
```java
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
