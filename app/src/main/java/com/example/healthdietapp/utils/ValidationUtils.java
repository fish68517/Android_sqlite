package com.example.healthdietapp.utils;

import java.util.regex.Pattern;

/**
 * ValidationUtils - Input validation utility class
 * Provides methods for validating user input across the application
 */
public class ValidationUtils {

    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 20;
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 50;
    private static final int MIN_NICKNAME_LENGTH = 1;
    private static final int MAX_NICKNAME_LENGTH = 30;

    /**
     * Validate username format and length
     */
    public static ValidationResult validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new ValidationResult(false, "用户名不能为空");
        }

        username = username.trim();

        if (username.length() < MIN_USERNAME_LENGTH) {
            return new ValidationResult(false, "用户名至少需要" + MIN_USERNAME_LENGTH + "个字符");
        }

        if (username.length() > MAX_USERNAME_LENGTH) {
            return new ValidationResult(false, "用户名最多" + MAX_USERNAME_LENGTH + "个字符");
        }

        // Allow alphanumeric and underscore
        if (!Pattern.matches("^[a-zA-Z0-9_]+$", username)) {
            return new ValidationResult(false, "用户名只能包含字母、数字和下划线");
        }

        return new ValidationResult(true, "");
    }

    /**
     * Validate password strength
     */
    public static ValidationResult validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return new ValidationResult(false, "密码不能为空");
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            return new ValidationResult(false, "密码至少需要" + MIN_PASSWORD_LENGTH + "个字符");
        }

        if (password.length() > MAX_PASSWORD_LENGTH) {
            return new ValidationResult(false, "密码最多" + MAX_PASSWORD_LENGTH + "个字符");
        }

        return new ValidationResult(true, "");
    }

    /**
     * Validate password confirmation
     */
    public static ValidationResult validatePasswordConfirmation(String password, String confirmPassword) {
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            return new ValidationResult(false, "确认密码不能为空");
        }

        if (!password.equals(confirmPassword)) {
            return new ValidationResult(false, "两次输入的密码不一致");
        }

        return new ValidationResult(true, "");
    }

    /**
     * Validate nickname
     */
    public static ValidationResult validateNickname(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            return new ValidationResult(false, "昵称不能为空");
        }

        nickname = nickname.trim();

        if (nickname.length() < MIN_NICKNAME_LENGTH) {
            return new ValidationResult(false, "昵称至少需要" + MIN_NICKNAME_LENGTH + "个字符");
        }

        if (nickname.length() > MAX_NICKNAME_LENGTH) {
            return new ValidationResult(false, "昵称最多" + MAX_NICKNAME_LENGTH + "个字符");
        }

        return new ValidationResult(true, "");
    }

    /**
     * Validate email format
     */
    public static ValidationResult validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return new ValidationResult(false, "邮箱不能为空");
        }

        email = email.trim();

        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!Pattern.matches(emailPattern, email)) {
            return new ValidationResult(false, "邮箱格式不正确");
        }

        return new ValidationResult(true, "");
    }

    /**
     * Validate phone number format
     */
    public static ValidationResult validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return new ValidationResult(false, "电话号码不能为空");
        }

        phoneNumber = phoneNumber.trim();

        // Allow various phone number formats
        String phonePattern = "^[0-9\\-\\+\\s()]+$";
        if (!Pattern.matches(phonePattern, phoneNumber)) {
            return new ValidationResult(false, "电话号码格式不正确");
        }

        if (phoneNumber.replaceAll("[^0-9]", "").length() < 7) {
            return new ValidationResult(false, "电话号码至少需要7位数字");
        }

        return new ValidationResult(true, "");
    }

    /**
     * Validate text input (not empty)
     */
    public static ValidationResult validateTextInput(String text, String fieldName) {
        if (text == null || text.trim().isEmpty()) {
            return new ValidationResult(false, fieldName + "不能为空");
        }

        return new ValidationResult(true, "");
    }

    /**
     * Validate text input with length constraints
     */
    public static ValidationResult validateTextInput(String text, String fieldName, int minLength, int maxLength) {
        if (text == null || text.trim().isEmpty()) {
            return new ValidationResult(false, fieldName + "不能为空");
        }

        text = text.trim();

        if (text.length() < minLength) {
            return new ValidationResult(false, fieldName + "至少需要" + minLength + "个字符");
        }

        if (text.length() > maxLength) {
            return new ValidationResult(false, fieldName + "最多" + maxLength + "个字符");
        }

        return new ValidationResult(true, "");
    }

    /**
     * Validate numeric input
     */
    public static ValidationResult validateNumericInput(String input, String fieldName) {
        if (input == null || input.trim().isEmpty()) {
            return new ValidationResult(false, fieldName + "不能为空");
        }

        try {
            Float.parseFloat(input.trim());
            return new ValidationResult(true, "");
        } catch (NumberFormatException e) {
            return new ValidationResult(false, fieldName + "必须是数字");
        }
    }

    /**
     * Validate numeric input with range
     */
    public static ValidationResult validateNumericInput(String input, String fieldName, float minValue, float maxValue) {
        ValidationResult basicValidation = validateNumericInput(input, fieldName);
        if (!basicValidation.isValid()) {
            return basicValidation;
        }

        try {
            float value = Float.parseFloat(input.trim());
            if (value < minValue || value > maxValue) {
                return new ValidationResult(false, fieldName + "必须在" + minValue + "到" + maxValue + "之间");
            }
            return new ValidationResult(true, "");
        } catch (NumberFormatException e) {
            return new ValidationResult(false, fieldName + "必须是数字");
        }
    }

    /**
     * Validation result class
     */
    public static class ValidationResult {
        private boolean valid;
        private String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}
