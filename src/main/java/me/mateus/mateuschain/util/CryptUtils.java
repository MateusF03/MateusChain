package me.mateus.mateuschain.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptUtils {

    public static String stringToSha256(String str) {
        try {
            MessageDigest msg = MessageDigest.getInstance("SHA-256");
            byte[] hash = msg.digest(str.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexStr = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1)
                    hexStr.append(0);
                hexStr.append(hex);
            }
            return hexStr.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
