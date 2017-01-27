package com.anthonynsimon.url;

import com.anthonynsimon.url.exceptions.MalformedURLException;

class Parser {
    /**
     * Returns a the URL with the new values after parsing the provided URL string.
     */
    public static URL parse(String rawUrl, URL target) throws MalformedURLException {
        if (rawUrl == null || rawUrl.isEmpty()) {
            throw new MalformedURLException("raw url string is empty");
        }
        if (target == null) {
            throw new MalformedURLException("target url is null");
        }

        String remaining = rawUrl;

        int index = remaining.lastIndexOf("#");
        if (index >= 0) {
            String frag = remaining.substring(index + 1, remaining.length());
            target.fragment = frag.isEmpty() ? null : frag;
            remaining = remaining.substring(0, index);
        }

        if (remaining.isEmpty()) {
            return target;
        }

        if (remaining.equals("*")) {
            target.path = "*";
            return target;
        }

        index = remaining.indexOf("?");
        if (index > 0) {
            String qr = remaining.substring(index + 1, remaining.length());
            if (!qr.isEmpty()) {
                target.query = qr;
            }
            remaining = remaining.substring(0, index);
        }

        remaining = parseScheme(remaining, target);

        if (target.scheme != null && !target.scheme.isEmpty()) {
            if (!remaining.startsWith("/")) {
                target.opaque = remaining;
                return target;
            }
        }
        if (((target.scheme != null && !target.scheme.isEmpty()) || !remaining.startsWith("///")) && remaining.startsWith("//")) {
            remaining = remaining.substring(2, remaining.length());
            int i = remaining.indexOf("/");
            if (i >= 0) {
                parseAuthority(remaining.substring(0, i), target);
                remaining = remaining.substring(i, remaining.length());
            } else {
                parseAuthority(remaining, target);
                remaining = "";
            }
        }

        if (!remaining.isEmpty()) {
            target.path = PercentEscaper.unescape(remaining);
        }

        return target;
    }

    /**
     * Parses the scheme from the provided string.
     *
     * * @throws MalformedURLException if there was a problem parsing the input string.
     */
    private static String parseScheme(String remaining, URL target) throws MalformedURLException {
        for (int i = 0; i < remaining.length(); i++) {
            char c = remaining.charAt(i);
            if ('a' <= c && c <= 'z' || 'A' <= c && c <= 'Z') {
                continue;
            } else if (c == ':') {
                if (i == 0) {
                    throw new MalformedURLException("missing scheme");
                }
                target.scheme = remaining.substring(0, i).toLowerCase();
                remaining = remaining.substring(i + 1, remaining.length());
                return remaining;
            } else if ('0' <= c && c <= '9' || c == '+' || c == '-' || c == '.') {
                if (i == 0) {
                    return remaining;
                }
            }
        }
        return remaining;
    }

    /**
     * Parses the authority (user:password@host:port) from the provided string.
     *
     * @throws MalformedURLException if there was a problem parsing the input string.
     */
    private static void parseAuthority(String authority, URL target) throws MalformedURLException {
        int i = authority.lastIndexOf('@');
        if (i >= 0) {
            String credentials = authority.substring(0, i);
            if (credentials.contains(":")) {
                String[] parts = credentials.split(":", 2);
                target.username = PercentEscaper.unescape(parts[0]);
                target.password = PercentEscaper.unescape(parts[1]);
            } else {
                target.username = PercentEscaper.unescape(credentials);
            }
            authority = authority.substring(i + 1, authority.length());
        }
        parseHost(authority, target);
    }

    /**
     * Parses the host from the provided string. The port is considered part of the host and
     * will be checked to ensure that it's a numeric value.
     *
     * @throws MalformedURLException if there was a problem parsing the input string.
     */
    private static void parseHost(String str, URL target) throws MalformedURLException {
        if (str.startsWith("[")) {
            int i = str.lastIndexOf("]");
            if (i < 0) {
                throw new MalformedURLException("IPv6 detected, but missing closing ']' token");
            }
            String portPart = str.substring(i + 1, str.length());
            if (!isPortValid(portPart)) {
                throw new MalformedURLException("invalid port");
            }
        } else {
            String[] parts = str.split(":", -1);
            if (parts.length > 2) {
                throw new MalformedURLException("invalid host: " + parts.toString());
            }
            if (parts.length == 2) {
                try {
                    Integer.valueOf(parts[1]);
                } catch (NumberFormatException e) {
                    throw new MalformedURLException("invalid port");
                }
            }
        }
        String ht = PercentEscaper.unescape(str.toLowerCase());
        if (!ht.isEmpty()) {
            target.host = ht;
        }
    }

    /**
     * Returns true if the provided port string contains a valid port number.
     * Note that an empty string is a valid port number since it's optional.
     *
     * For example:
     *
     * ''      => TRUE
     * null    => TRUE
     * ':8080' => TRUE
     * ':ab80' => FALSE
     * ':abc'  => FALSE
     */
    private static boolean isPortValid(String portStr) {
        if (portStr == null || portStr.isEmpty()) {
            return true;
        }
        int i = portStr.indexOf(":");
        if (i < 0) {
            return false;
        }
        portStr = portStr.substring(i + 1, portStr.length());
        try {
            Integer.valueOf(portStr);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
