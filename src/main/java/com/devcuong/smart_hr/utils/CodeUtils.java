package com.devcuong.smart_hr.utils;

public final class CodeUtils {

    public static String generateCode(String prefix, Integer id) {
        if (prefix == null || prefix.isEmpty()) {
            throw new IllegalArgumentException("Prefix cannot be null or empty");
        }
        String formattedId = String.format("%06d", id);

        return prefix.toUpperCase() + formattedId;
    }

    public static String generateCode(String prefix, Long id) {
        if (prefix == null || prefix.isEmpty()) {
            throw new IllegalArgumentException("Prefix cannot be null or empty");
        }
        String formattedId = String.format("%06d", id);
        return prefix.toUpperCase() + formattedId;
    }
}
