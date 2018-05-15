package com.anthonynsimon.url;

import com.anthonynsimon.url.exceptions.MalformedURLException;

/**
 * A default URL parser implementation.
 */
final class DefaultURLParser implements URLParser {

    /**
     * Returns a the URL with the new values after parsing the provided URL string.
     */
    public URL parse(String rawUrl) throws MalformedURLException {
        if (rawUrl == null || rawUrl.isEmpty()) {
            throw new MalformedURLException("raw url string is empty");
        }

        URLBuilder builder = new URLBuilder();
        String remaining = rawUrl;

        int index = remaining.lastIndexOf('#');
        if (index >= 0) {
            String frag = remaining.substring(index + 1, remaining.length());
            builder.setFragment(frag.isEmpty() ? null : frag);
            remaining = remaining.substring(0, index);
        }

        if (remaining.isEmpty()) {
            return builder.build();
        }

        if ("*".equals(remaining)) {
            builder.setPath("*");
            return builder.build();
        }

        index = remaining.indexOf('?');
        if (index > 0) {
            String query = remaining.substring(index + 1, remaining.length());
            if (query.isEmpty()) {
                builder.setQuery("?");
            } else {
                builder.setQuery(query);
            }
            remaining = remaining.substring(0, index);
        }

        PartialParseResult parsedScheme = parseScheme(remaining);
        String scheme = parsedScheme.result;
        boolean hasScheme = scheme != null && !scheme.isEmpty();
        builder.setScheme(scheme);
        remaining = parsedScheme.remaining;

        if (hasScheme && remaining.charAt(0) != '/') {
            builder.setOpaque(remaining);
            return builder.build();
        }
        if ((hasScheme || !remaining.startsWith("///")) && remaining.startsWith("//")) {
            remaining = remaining.substring(2, remaining.length());

            String authority = remaining;
            int i = remaining.indexOf('/');
            if (i >= 0) {
                authority = remaining.substring(0, i);
                remaining = remaining.substring(i, remaining.length());
            } else {
                remaining = "";
            }

            if (!authority.isEmpty()) {
                UserInfoResult userInfoResult = parseUserInfo(authority);
                builder.setUsername(userInfoResult.user);
                builder.setPassword(userInfoResult.password);
                authority = userInfoResult.remaining;
            }

            PartialParseResult hostResult = parseHost(authority);
            builder.setHost(hostResult.result);
        }

        if (!remaining.isEmpty()) {
            builder.setPath(PercentEncoder.decode(remaining));
            builder.setRawPath(remaining);
        }

        return builder.build();
    }

    /**
     * Parses the scheme from the provided string.
     *
     * * @throws MalformedURLException if there was a problem parsing the input string.
     */
    private PartialParseResult parseScheme(String remaining) throws MalformedURLException {
        int indexColon = remaining.indexOf(':');
        if (indexColon == 0) {
            throw new MalformedURLException("missing scheme");
        }
        if (indexColon < 0) {
            return new PartialParseResult("", remaining);
        }

        // if first char is special then its not a scheme
        char first = remaining.charAt(0);
        if ('0' <= first && first <= '9' || first == '+' || first == '-' || first == '.') {
            return new PartialParseResult("", remaining);
        }

        String scheme = remaining.substring(0, indexColon).toLowerCase();
        String rest = remaining.substring(indexColon + 1, remaining.length());
        return new PartialParseResult(scheme, rest);
    }

    /**
     * Parses the authority (user:password@host:port) from the provided string.
     *
     * @throws MalformedURLException if there was a problem parsing the input string.
     */
    private UserInfoResult parseUserInfo(String str) throws MalformedURLException {
        int i = str.lastIndexOf('@');
        String username = null;
        String password = null;
        String rest = str;
        if (i >= 0) {
            String credentials = str.substring(0, i);
            if (credentials.indexOf(':') >= 0) {
                String[] parts = credentials.split(":", 2);
                username = PercentEncoder.decode(parts[0]);
                password = PercentEncoder.decode(parts[1]);
            } else {
                username = PercentEncoder.decode(credentials);
            }
            rest = str.substring(i + 1, str.length());
        }

        return new UserInfoResult(username, password, rest);
    }

    /**
     * Parses the host from the provided string. The port is considered part of the host and
     * will be checked to ensure that it's a numeric value.
     *
     * @throws MalformedURLException if there was a problem parsing the input string.
     */
    private PartialParseResult parseHost(String str) throws MalformedURLException {
        if (str.length() == 0) {
            return new PartialParseResult("", "");
        }
        if (str.charAt(0) == '[') {
            int i = str.lastIndexOf(']');
            if (i < 0) {
                throw new MalformedURLException("IPv6 detected, but missing closing ']' token");
            }
            String portPart = str.substring(i + 1, str.length());
            if (!isPortValid(portPart)) {
                throw new MalformedURLException("invalid port");
            }
        } else {
            if (str.indexOf(':') != -1) {
                String[] parts = str.split(":", -1);
                if (parts.length > 2) {
                    throw new MalformedURLException("invalid host in: " + str);
                }
                if (parts.length == 2) {
                    try {
                        Integer.valueOf(parts[1]);
                    } catch (NumberFormatException e) {
                        throw new MalformedURLException("invalid port");
                    }
                }
            }
        }
        return new PartialParseResult(PercentEncoder.decode(str.toLowerCase()), "");
    }

    /**
     * Returns true if the provided port string contains a valid port number.
     * Note that an empty string is a valid port number since it's optional.
     * <p>
     * For example:
     * <p>
     * ''      => TRUE
     * null    => TRUE
     * ':8080' => TRUE
     * ':ab80' => FALSE
     * ':abc'  => FALSE
     */
    protected boolean isPortValid(String portStr) {
        if (portStr == null || portStr.isEmpty()) {
            return true;
        }
        int i = portStr.indexOf(':');
        // Port format must be ':8080'
        if (i != 0) {
            return false;
        }
        String segment = portStr.substring(i + 1, portStr.length());
        try {
            Integer.valueOf(segment);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private class PartialParseResult {
        public final String result;
        public final String remaining;


        public PartialParseResult(String result, String remaining) {
            this.result = result;
            this.remaining = remaining;
        }
    }

    private class UserInfoResult {
        public final String user;
        public final String password;
        public final String remaining;


        public UserInfoResult(String user, String password, String remaining) {
            this.user = user;
            this.password = password;
            this.remaining = remaining;
        }
    }
}
