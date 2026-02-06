package com.example.healthdietapp.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * PasswordUtils - Utility class for password encryption and validation
 */
public class PasswordUtils {

    /**
     * Encrypt password using SHA-256
     */
    public static String encryptPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Verify password against encrypted hash
     */
    public static boolean verifyPassword(String password, String hash) {
        String encryptedPassword = encryptPassword(password);
        return encryptedPassword != null && encryptedPassword.equals(hash);
    }

    /**
     * Validate password strength
     * Requirements: at least 6 characters
     */
    public static boolean isPasswordStrong(String password) {
        return password != null && password.length() >= 6;
    }
}
