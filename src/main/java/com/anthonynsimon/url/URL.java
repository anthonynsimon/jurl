package com.anthonynsimon.url;

public class URL {

    private String scheme;
    private String username;
    private String password;
    private String host;
    private String path;
    private String query;
    private String fragment;
    private String opaque;

    // TODO: allow for manual construction of a url?
    public URL(String rawUrl) throws MalformedURLException {
        parse(rawUrl);
    }

    private void parse(String rawUrl) throws MalformedURLException {
        if (rawUrl == null) {
            throw new MalformedURLException("url is empty");
        }

        String remaining = rawUrl;

        int index = remaining.lastIndexOf("#");
        if (index > 0) {
            String frag = remaining.substring(index + 1, remaining.length());
            fragment = frag.isEmpty() ? null : frag;
            remaining = remaining.substring(0, index);
        }

        if (remaining.isEmpty()) {
            throw new MalformedURLException("invalid url");
        }

        if (remaining.equals("*")) {
            path = "*";
            return;
        }

        index = remaining.indexOf("?");
        if (index > 0) {
            String qr = remaining.substring(index + 1, remaining.length());
            if (!qr.isEmpty()) {
                query = qr;
            }
            remaining = remaining.substring(0, index);
        }

        remaining = parseScheme(remaining);

        if (scheme != null && !scheme.isEmpty()) {
            if (!remaining.startsWith("/")) {
                opaque = remaining;
                return;
            }
        }
        if (((scheme != null && !scheme.isEmpty()) || !remaining.startsWith("///")) && remaining.startsWith("//")) {
            remaining = remaining.substring(2, remaining.length());
            int i = remaining.indexOf("/");
            if (i >= 0) {
                parseAuthority(remaining.substring(0, i));
                remaining = remaining.substring(i, remaining.length());
            } else {
                parseAuthority(remaining);
                remaining = "";
            }
        }

        if (!remaining.isEmpty()) {
            path = URLEscaper.unescape(remaining, URLPart.PATH);
        }
    }

    private String parseScheme(String remaining) throws MalformedURLException {
        for (int i = 0; i < remaining.length(); i++) {
            char c = remaining.charAt(i);
            if ('a' <= c && c <= 'z' || 'A' <= c && c <= 'Z') {
                continue;
            } else if (c == ':') {
                if (i == 0) {
                    throw new MalformedURLException("missing scheme");
                }
                scheme = remaining.substring(0, i).toLowerCase();
                remaining = remaining.substring(i + 1, remaining.length());
                return remaining;
            } else if ('0' <= c && c <= '9' || c == '+' || c == '-' || c == '.') {
                if (i == 0) {
                    throw new MalformedURLException("bad scheme format");
                }
            }
        }
        return remaining;
    }

    private void parseAuthority(String authority) throws MalformedURLException {
        int i = authority.lastIndexOf('@');
        if (i >= 0) {
            String credentials = authority.substring(0, i);
            if (credentials.contains(":")) {
                String[] parts = credentials.split(":", 2);
                username = URLEscaper.unescape(parts[0], URLPart.CREDENTIALS);
                password = URLEscaper.unescape(parts[1], URLPart.CREDENTIALS);
            } else {
                username = URLEscaper.unescape(credentials, URLPart.CREDENTIALS);
            }
            authority = authority.substring(i + 1, authority.length());
        }
        parseHost(authority);
    }

    private void parseHost(String str) throws MalformedURLException {
        if (str.startsWith("[")) {
            int i = str.lastIndexOf("]");
            if (i < 0) {
                throw new MalformedURLException("IPv6 detected, but missing closing ']' token");
            }
            String portPart = str.substring(i + 1, str.length());
            if (!validOptionalPort(portPart)) {
                throw new MalformedURLException("invalid port");
            }
        } else {
            String[] parts = str.split(":", -1);
            if (parts.length > 2) {
                throw new MalformedURLException("invalid host");
            }
            String hostPart = parts[0];
            if (parts.length == 2) {
                try {
                    Integer.valueOf(parts[1]);
                } catch (NumberFormatException e) {
                    throw new MalformedURLException("invalid port");
                }
            }
        }
        String ht = URLEscaper.unescape(str.toLowerCase(), URLPart.HOST);
        if (!ht.isEmpty()) {
            host = ht;
        }
    }

    private boolean validOptionalPort(String portStr) {
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

    public String scheme() {
        return scheme;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public String host() {
        return host;
    }


    public String path() {
        return path;
    }

    public String query() {
        return query;
    }

    public String fragment() {
        return fragment;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof URL)) {
            return false;
        }
        URL otherURL = (URL) other;
        return toString().equals(other.toString());
    }

    @Override
    public String toString() {
        String result = "";
        if (scheme != null && !scheme.isEmpty()) {
            result += scheme + ":";
        }
        if (opaque != null) {
            result += opaque;
        } else {
            if (scheme != null || host != null) {
                result += "//";
                if (username != null) {
                    result += URLEscaper.escape(username, URLPart.CREDENTIALS);
                    if (password != null) {
                        result += ":" + URLEscaper.escape(password, URLPart.CREDENTIALS);
                    }
                    result += "@";
                }
                if (host != null) {
                    result += URLEscaper.escape(host, URLPart.HOST);
                }
            }
            if (path != null) {
                result += URLEscaper.escape(path, URLPart.PATH);
            }
        }
        if (query != null) {
            result += "?" + query;
        }
        if (fragment != null) {
            result += "#" + fragment;
        }
        return result;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
