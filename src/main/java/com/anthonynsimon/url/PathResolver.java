package com.anthonynsimon.url;

import java.util.ArrayList;
import java.util.List;

/**
 * PathResolver is a utility class that resolves a reference path against a base path.
 */
final class PathResolver {

    /**
     * Disallow instantiation of class.
     */
    private PathResolver() {
    }

    /**
     * Returns a resolved path.
     * <p>
     * For example:
     * <p>
     * '/some/path' resolve '..' => '/some'
     * '/some/path' resolve '.' => '/some/'
     * '/some/path' resolve './here' => '/some/here'
     * '/some/path' resolve '../here' => '/here'
     */
    public static String resolve(String base, String ref) {
        String merged;

        if (ref == null || ref.isEmpty()) {
            merged = base;
        } else if (!(ref.charAt(0) == '/') && base != null && !base.isEmpty()) {
            int i = base.lastIndexOf("/");
            merged = base.substring(0, i + 1) + ref;
        } else {
            merged = ref;
        }

        if (merged == null || merged.isEmpty()) {
            return "";
        }

        String[] parts = merged.split("/", -1);
        List<String> result = new ArrayList<>();

        for (int i = 0; i < parts.length; i++) {
            switch (parts[i]) {
                case "":
                case ".":
                    // Ignore
                    break;
                case "..":
                    if (result.size() > 0) {
                        result.remove(result.size() - 1);
                    }
                    break;
                default:
                    result.add(parts[i]);
                    break;
            }
        }

        if (parts.length > 0) {
            // Get last element, if it was '.' or '..' we need
            // to end in a slash.
            switch (parts[parts.length - 1]) {
                case ".":
                case "..":
                    // Add an empty last string, it will be turned into
                    // a slash when joined together.
                    result.add("");
                    break;
            }
        }

        return "/" + String.join("/", result);
    }
}
