package com.anthonynsimon.url;

import com.anthonynsimon.url.exceptions.InvalidHexException;
import com.anthonynsimon.url.exceptions.MalformedURLException;

import java.io.UnsupportedEncodingException;

class EscapeUtils {

    private static final char[] reservedChars = {'$', '&', '+', ',', '/', ':', ';', '=', '?', '@'};
    private static final char[] subDelimsChars = {'!', '$', '&', '\'', '(', ')', '*', '+', ',', ';', '=', ':', '[', ']', '<', '>', '"'};
    private static final char[] unreservedChars = {'-', '_', '.', '~'};
    private static final short[] utf8Masks = new short[]{0b00000000, 0b11000000, 0b11100000, 0b11110000};

    private static boolean shouldEscape(char c, URL.URLPart zone) {
        if ('A' <= c && c <= 'Z' || 'a' <= c && c <= 'z' || '0' <= c && c <= '9') {
            return false;
        }

        if (zone == URL.URLPart.HOST || zone == URL.URLPart.PATH) {
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

    public static String escape(String str, URL.URLPart zone) {
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
                    result += "%" + "0123456789ABCDEF".charAt((bytes[i] & 0xFF) >> 4) + "0123456789ABCDEF".charAt((bytes[i] & 0xFF) & 15);
                } else {
                    result += c;
                }
                i++;
            }
        }

        return result;
    }

    public static String unescape(String str) throws MalformedURLException {
        char[] chars = str.toCharArray();
        String result = "";
        int len = str.length();
        int i = 0;
        while (i < chars.length) {
            char c = chars[i];
            if (c != '%') {
                result += c;
                i++;
            } else {
                if (i + 2 >= len) {
                    throw new MalformedURLException("invalid escape sequence");
                }
                byte code;
                try {
                    code = unhex(str.substring(i + 1, i + 3).toCharArray());
                } catch (InvalidHexException e) {
                    throw new MalformedURLException(e.getMessage());
                }
                int readBytes = 0;
                for (short mask : utf8Masks) {
                    if ((code & mask) == mask) {
                        readBytes++;
                    } else {
                        break;
                    }
                }
                byte[] buffer = new byte[readBytes];
                for (int j = 0; j < readBytes; j++) {
                    try {
                        buffer[j] = unhex(str.substring(i + 1, i + 3).toCharArray());
                    } catch (InvalidHexException e) {
                        throw new MalformedURLException(e.getMessage());
                    }
                    i += 3;
                }
                result += new String(buffer);
            }
        }
        return result;
    }

    private static byte unhex(char[] hex) throws InvalidHexException {
        int result = 0;
        for (int i = 0; i < hex.length; i++) {
            char c = hex[hex.length - i - 1];
            int index = -1;
            if ('0' <= c && c <= '9') {
                index = c - '0';
            } else if ('a' <= c && c <= 'f') {
                index = c - 'a' + 10;
            } else if ('A' <= c && c <= 'F') {
                index = c - 'A' + 10;
            }
            if (index < 0 || index >= 16) {
                throw new InvalidHexException("not a valid hex char: " + c);
            }
            result += index * pow(16, i);
        }
        return (byte) result;
    }

    private static int pow(int base, int exp) {
        int result = 1;
        while (exp > 0) {
            result *= base;
            exp--;
        }
        return result;
    }
}
