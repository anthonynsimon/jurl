package com.anthonynsimon.url;

import com.anthonynsimon.url.exceptions.MalformedURLException;

/**
 * A default URL parser implementation.
 */
final class DefaultURLParser implements URLParser {

    public DefaultURLParser() {
    }

    /**
     * Returns a the URL with the new values after parsing the provided URL string.
     */
    public URL parse(String rawUrl) throws MalformedURLException {
        if (rawUrl == null || rawUrl.isEmpty()) {
            throw new MalformedURLException("raw url string is empty");
        }

        URLBuilder builder = new URLBuilder();
        String remaining = rawUrl;

        int index = remaining.lastIndexOf("#");
        if (index >= 0) {
            String frag = remaining.substring(index + 1, remaining.length());
            builder.setFragment(frag.isEmpty() ? null : frag);
            remaining = remaining.substring(0, index);
        }

        if (remaining.isEmpty()) {
            return builder.build();
        }

        if (remaining.equals("*")) {
            builder.setPath("*");
            return builder.build();
        }

        index = remaining.indexOf("?");
        if (index > 0) {
            String qr = remaining.substring(index + 1, remaining.length());
            if (!qr.isEmpty()) {
                builder.setQuery(qr);
            }
            remaining = remaining.substring(0, index);
        }

        PartialParseResult parsedScheme = parseScheme(remaining);
        String scheme = parsedScheme.result;
        boolean hasScheme = scheme != null && !scheme.isEmpty();
        builder.setScheme(scheme);
        remaining = parsedScheme.remaining;

        if (hasScheme) {
            if (!remaining.startsWith("/")) {
                builder.setOpaque(remaining);
                return builder.build();
            }
        }
        if ((hasScheme || !remaining.startsWith("///")) && remaining.startsWith("//")) {
            remaining = remaining.substring(2, remaining.length());

            String authority = remaining;
            int i = remaining.indexOf("/");
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
        }

        return builder.build();
    }

    /**
     * Parses the scheme from the provided string.
     * <p>
     * * @throws MalformedURLException if there was a problem parsing the input string.
     */
    private PartialParseResult parseScheme(String remaining) throws MalformedURLException {
        for (int i = 0; i < remaining.length(); i++) {
            char c = remaining.charAt(i);
            if ('a' <= c && c <= 'z' || 'A' <= c && c <= 'Z') {
                continue;
            } else if (c == ':') {
                if (i == 0) {
                    throw new MalformedURLException("missing scheme");
                }
                String scheme = remaining.substring(0, i).toLowerCase();
                remaining = remaining.substring(i + 1, remaining.length());
                return new PartialParseResult(scheme, remaining);
            } else if ('0' <= c && c <= '9' || c == '+' || c == '-' || c == '.') {
                if (i == 0) {
                    return new PartialParseResult("", remaining);
                }
            }
        }
        return new PartialParseResult("", remaining);
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
        if (i >= 0) {
            String credentials = str.substring(0, i);
            if (credentials.contains(":")) {
                String[] parts = credentials.split(":", 2);
                username = PercentEncoder.decode(parts[0]);
                password = PercentEncoder.decode(parts[1]);
            } else {
                username = PercentEncoder.decode(credentials);
            }
            str = str.substring(i + 1, str.length());
        }

        return new UserInfoResult(username, password, str);
    }

    /**
     * Parses the host from the provided string. The port is considered part of the host and
     * will be checked to ensure that it's a numeric value.
     *
     * @throws MalformedURLException if there was a problem parsing the input string.
     */
    private PartialParseResult parseHost(String str) throws MalformedURLException {
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
        int i = portStr.indexOf(":");
        // Port format must be ':8080'
        if (i != 0) {
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
