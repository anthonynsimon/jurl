package com.anthonynsimon.url;

class URLEscaper {

    private static final char[] reservedChars = {'$', '&', '+', ',', '/', ':', ';', '=', '?', '@'};
    private static final char[] subDelimsChars = {'!', '$', '&', '\'', '(', ')', '*', '+', ',', ';', '=', ':', '[', ']', '<', '>', '"'};
    private static final char[] unreservedChars = {'-', '_', '.', '~'};

    private static boolean shouldEscape(char c, URLPart zone) {
        if ('A' <= c && c <= 'Z' || 'a' <= c && c <= 'z' || '0' <= c && c <= '9') {
            return false;
        }

        if (zone == URLPart.HOST || zone == URLPart.PATH) {
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

    public static String escape(String str, URLPart zone) {
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

    public static String unescape(String str, URLPart zone) throws MalformedURLException {
        char[] chars = str.toCharArray();
        String result = "";
        for (int i = 0; i < chars.length; ) {
            char c = chars[i];
            if (c == '%' && i + 2 < chars.length) {
                String hex = "" + chars[i + 1] + chars[i + 2];
                if (zone == URLPart.HOST && hex == "25") {
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
}
