# jurl
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/e8a7715455e44c73be897eaa131a8054)](https://app.codacy.com/app/anthonynajjars/jurl?utm_source=github.com&utm_medium=referral&utm_content=anthonynsimon/jurl&utm_campaign=badger)
[![Build Status](https://travis-ci.org/anthonynsimon/jurl.svg?branch=master)](https://travis-ci.org/anthonynsimon/jurl/builds) 
[![Test Coverage](https://codecov.io/gh/anthonynsimon/jurl/branch/master/graph/badge.svg)](https://codecov.io/gh/anthonynsimon/jurl) 
[![MIT License](https://img.shields.io/github/license/mashape/apistatus.svg?maxAge=2592000)](https://github.com/anthonynsimon/jurl/blob/master/LICENSE)
[![](https://jitpack.io/v/anthonynsimon/jurl.svg)](https://jitpack.io/#anthonynsimon/jurl)  

Fast and simple URL parsing for Java, with UTF-8 and path resolving support. Based on Go's excellent `net/url` lib.

## Why
- Easy to use API - you just want to parse a URL after all.
- Fast, 4+ million URLs per second on commodity hardware.
- UTF-8 encoding and decoding.
- Supports path resolving between URLs (absolute and relative).
- Good test coverage with plenty of edge cases.
- Supports IPv4 and IPv6.
- No external dependencies.

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
URL resolved = base.resolveReference(ref); // https://user:secret@example♬.com/path/to/file.html?search=germany&language=de_DE
resolved.getPath(); // /path/to/file.html

// Escaped UTF-8 result
resolved.toString(); // https://user:secret@example%E2%99%AC.com/path/to/file.html?search=germany&language=de_DE

```

## Setup

### Add the JitPack repository to your build file.

For gradle:
```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
For maven:
```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

### Add the dependency:

For gradle:
```
dependencies {
    compile 'com.github.anthonynsimon:jurl:v0.4.2'
}
```

For maven:
```
<dependencies>
    <dependency>
        <groupId>com.github.anthonynsimon</groupId>
        <artifactId>jurl</artifactId>
        <version>v0.4.2</version>
    </dependency>
</dependencies>
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
