package com.anthonynsimon.url;

public class URL {
    protected enum EncodeZone {
        CREDENTIALS,
        HOST,
        PATH,
        QUERY,
        FRAGMENT,
    }

    private static final char[] reservedChars = {'$', '&', '+', ',', '/', ':', ';', '=', '?', '@'};
    private static final char[] subDelimsChars = {'!', '$', '&', '\'', '(', ')', '*', '+', ',', ';', '=', ':', '[', ']', '<', '>', '"'};
    private static final char[] unreservedChars = {'-', '_', '.', '~'};

    private String protocol;
    private String username;
    private String password;
    private String host;
    private String path;
    private String query;
    private String fragment;
    private String opaque;

    public URL(String rawUrl) throws MalformedURLException {
        parse(rawUrl);
    }

    private boolean shouldEscape(char c, EncodeZone zone) {
        if ('A' <= c && c <= 'Z' || 'a' <= c && c <= 'z' || '0' <= c && c <= '9') {
            return false;
        }

        if (zone == EncodeZone.HOST || zone == EncodeZone.PATH) {
            if (c == '%') {
                return true;
            }
            for (char reserved : subDelimsChars) {
                if (reserved == c) {
                    return false;
                }
            }
        }

        for (char unreserved : unreservedChars) {
            if (unreserved == c) {
                return false;
            }
        }

        for (char reserved : new char[]{'$', '&', '+', ',', '/', ':', ';', '=', '?', '@'}) {
            if (reserved == c) {
                switch (zone) {
                    case PATH:
                        return c == '?';
                    case CREDENTIALS:
                        return c == '@' || c == '/' || c == '?' || c == ':';
                    case QUERY:
                        return true;
                    case FRAGMENT:
                        return false;
                }
            }
        }

        return true;
    }

    private String escape(String str, EncodeZone zone) {
        char[] chars = str.toCharArray();
        String result = "";
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (shouldEscape(c, zone)) {
                result += "%" + Integer.toHexString(c).toUpperCase();
            } else {
                result += c;
            }
        }
        return result;
    }

    private String unescape(String str, EncodeZone zone) throws MalformedURLException {
        char[] chars = str.toCharArray();
        String result = "";
        for (int i = 0; i < chars.length; ) {
            char c = chars[i];
            if (c == '%' && i + 2 < chars.length) {
                String hex = "" + chars[i + 1] + chars[i + 2];
                if (zone == EncodeZone.HOST && hex == "25") {
                    continue;
                }
                try {
                    int val = Integer.parseInt(hex, 16);
                    result += (char) val;
                } catch (NumberFormatException e) {
                    throw new MalformedURLException("invalid escape sequence: %" + hex);
                }
                i += 3;
            } else {
                result += c;
                i++;
            }
        }
        return result;
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

        remaining = parseProtocol(remaining);

        if (protocol != null && !protocol.isEmpty()) {
            if (!remaining.startsWith("/")) {
                opaque = remaining;
                return;
            }
        }
        if (((protocol != null && !protocol.isEmpty()) || !remaining.startsWith("///")) && remaining.startsWith("//")) {
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
            path = unescape(remaining, EncodeZone.PATH);
        }
    }

    private String parseProtocol(String remaining) throws MalformedURLException {
        for (int i = 0; i < remaining.length(); i++) {
            char c = remaining.charAt(i);
            if ('a' <= c && c <= 'z' || 'A' <= c && c <= 'Z') {
                continue;
            } else if (c == ':') {
                if (i == 0) {
                    throw new MalformedURLException("missing protocol");
                }
                protocol = remaining.substring(0, i).toLowerCase();
                remaining = remaining.substring(i + 1, remaining.length());
                return remaining;
            } else if ('0' <= c && c <= '9' || c == '+' || c == '-' || c == '.') {
                if (i == 0) {
                    throw new MalformedURLException("bad protocol format");
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
                username = unescape(parts[0], EncodeZone.CREDENTIALS);
                password = unescape(parts[1], EncodeZone.CREDENTIALS);
            } else {
                username = unescape(credentials, EncodeZone.CREDENTIALS);
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
        String ht = unescape(str.toLowerCase(), EncodeZone.HOST);
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

    public String protocol() {
        return protocol;
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

    // TODO: use a bytes buffer to speed this up?
    @Override
    public String toString() {
        String result = "";
        if (protocol != null && !protocol.isEmpty()) {
            result += protocol + ":";
        }
        if (opaque != null) {
            result += opaque;
        } else {
            if (protocol != null || host != null) {
                result += "//";
                if (username != null) {
                    result += escape(username, EncodeZone.CREDENTIALS);
                    if (password != null) {
                        result += ":" + escape(password, EncodeZone.CREDENTIALS);
                    }
                    result += "@";
                }
                if (host != null) {
                    result += escape(host, EncodeZone.HOST);
                }
            }
            if (path != null) {
                result += escape(path, EncodeZone.PATH);
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
