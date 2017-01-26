package com.anthonynsimon.url;

import java.io.UnsupportedEncodingException;

class URLEscaper {

    private static final char[] reservedChars = {'$', '&', '+', ',', '/', ':', ';', '=', '?', '@'};
    private static final char[] subDelimsChars = {'!', '$', '&', '\'', '(', ')', '*', '+', ',', ';', '=', ':', '[', ']', '<', '>', '"'};
    private static final char[] unreservedChars = {'-', '_', '.', '~'};
    private static final short[] utf8Masks = new short[]{0b00000000, 0b11000000, 0b11100000, 0b11110000};


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
        byte[] bytes;
        try {
            bytes = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        int i = 0;
        String result = "";
        while (i < bytes.length) {
            int readBytes = 0;
            for (short mask : utf8Masks) {
                if ((bytes[i] & mask) == mask) {
                    readBytes++;
                } else {
                    break;
                }
            }
            for (int j = 0; j < readBytes; j++) {
                char c = (char) bytes[i];
                if (shouldEscape(c, zone)) {
//                    result += String.format("%%%02X", bytes[i]);
                    result += "%" + "0123456789ABCDEF".charAt((bytes[i] & 0xFF) >> 4) + "0123456789ABCDEF".charAt((bytes[i] & 0xFF) & 15); // ~15x faster than above
                } else {
                    result += c;
                }
                i++;
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
