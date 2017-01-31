package com.anthonynsimon.url;

import org.junit.Assert;
import org.junit.Test;

public class PathResolverTest {


    private PathResolveTestCase[] pathResolveCases = new PathResolveTestCase[]{
            new PathResolveTestCase(
                    "",
                    "",
                    ""
            ),
            new PathResolveTestCase(
                    "abc",
                    "",
                    "/abc"
            ),
            new PathResolveTestCase(
                    "abc",
                    "456",
                    "/456"
            ),
            new PathResolveTestCase(
                    "/abc",
                    "123",
                    "/123"
            ),
            new PathResolveTestCase(
                    "/abc/abc",
                    "/123",
                    "/123"
            ),
            new PathResolveTestCase(
                    "abc/def",
                    ".",
                    "/abc/"
            ),
            new PathResolveTestCase(
                    "abc/def",
                    "123",
                    "/abc/123"
            ),
            new PathResolveTestCase(
                    "abc/def",
                    "..",
                    "/"
            ),
            new PathResolveTestCase(
                    "abc/",
                    "..",
                    "/"
            ),
            new PathResolveTestCase(
                    "abc/",
                    "../..",
                    "/"
            ),
            new PathResolveTestCase(
                    "abc/def/hij",
                    "..",
                    "/abc/"
            ),
            new PathResolveTestCase(
                    "abc/def/hij",
                    ".",
                    "/abc/def/"
            ),
            new PathResolveTestCase(
                    "abc/def/hij",
                    "../123",
                    "/abc/123"
            ),
            new PathResolveTestCase(
                    "abc/def/hij",
                    ".././123",
                    "/abc/123"
            ),
            new PathResolveTestCase(
                    "abc/def/hij",
                    "../../123",
                    "/123"
            ),
            new PathResolveTestCase(
                    "abc/def/hij",
                    "./../123",
                    "/abc/123"
            ),
            new PathResolveTestCase(
                    "abc/../hij",
                    "",
                    "/hij"
            ),
            new PathResolveTestCase(
                    "abc/hij",
                    "./..",
                    "/"
            ),
            new PathResolveTestCase(
                    "abc/./hij",
                    ".",
                    "/abc/"
            ),
            new PathResolveTestCase(
                    "abc/./hij",
                    "",
                    "/abc/hij"
            ),
            new PathResolveTestCase(
                    "abc/../hij",
                    "",
                    "/hij"
            ),
            new PathResolveTestCase(
                    "abc/../hij",
                    ".",
                    "/"
            ),
            new PathResolveTestCase(
                    "",
                    "../../../././../",
                    "/"
            ),
            new PathResolveTestCase(
                    "../../../././../",
                    "../../../././../",
                    "/"
            ),
            new PathResolveTestCase(
                    "../////../.././/./../",
                    "../../../././../",
                    "/"
            ),
            new PathResolveTestCase(
                    "abc",
                    "////",
                    "/"
            ),
            new PathResolveTestCase(
                    "/////",
                    "abc",
                    "/abc"
            ),
            new PathResolveTestCase(
                    "abc/.././123",
                    "x",
                    "/x"
            ),
    };

    @Test
    public void testResolvePath() throws Exception {
        for (PathResolveTestCase testCase : pathResolveCases) {
            String resolved = PathResolver.resolve(testCase.inputBase, testCase.inputReference);
            Assert.assertEquals(testCase.expected, resolved);
        }
    }


    private class PathResolveTestCase {
        public String inputBase;
        public String inputReference;
        public String expected;

        public PathResolveTestCase(String inputBase, String inputReference, String expected) {
            this.inputBase = inputBase;
            this.inputReference = inputReference;
            this.expected = expected;
        }
    }
}
