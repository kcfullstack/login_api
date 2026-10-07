package com.wefour.utility.auth.model.entity;

/**
 * Enumeration of supported OAuth2 Identity Providers.
 */
public enum AuthProvider {
    LOCAL,
    GOOGLE,
    GITHUB;

    public static AuthProvider fromString(String provider) {
        if (provider == null) {
            return LOCAL;
        }
        try {
            return AuthProvider.valueOf(provider.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return LOCAL;
        }
    }
}
