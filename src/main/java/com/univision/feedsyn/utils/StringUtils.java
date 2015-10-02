package com.univision.feedsyn.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * String helper methods for Univision
 */
public class StringUtils {

    private static final char[] HEX_CHARACTERS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String hex(byte[] bytes) {
        if(bytes == null) {
            return null;
        } else {
            int bytesLength = bytes.length;
            char[] hex = new char[bytesLength * 2];
            int byteIndex = 0;

            for(int hexIndex = 0; byteIndex < bytesLength; hexIndex += 2) {
                byte currentByte = bytes[byteIndex];
                hex[hexIndex] = HEX_CHARACTERS[(currentByte & 240) >> 4];
                hex[hexIndex + 1] = HEX_CHARACTERS[currentByte & 15];
                ++byteIndex;
            }

            return new String(hex);
        }
    }

    public static byte[] sha1(String string) {
        return hash("SHA-1", string);
    }

    public static byte[] hash(String algorithm, String string) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException var6) {
            throw new IllegalArgumentException(String.format("[%s] isn\'t a valid hash algorithm!", algorithm), var6);
        }

        byte[] bytes;
        try {
            bytes = string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException var5) {
            throw new IllegalStateException(var5);
        }

        return digest.digest(bytes);
    }

}