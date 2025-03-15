package com.muic.ssc.backend.Utils;

import java.security.SecureRandom;

/**
 * Utility class for generating secure unique IDs
 */
public class IdGenerator {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    // 6-digit minimum and maximum
    private static final long MIN_ID = 100000L;
    private static final long MAX_ID = 999999L;

    /**
     * Generates a secure random 6-digit ID
     * @return A random ID between 100000 and 999999
     */
    public static Long generateSixDigitId() {
        return MIN_ID + (long) (SECURE_RANDOM.nextDouble() * (MAX_ID - MIN_ID + 1));
    }
}