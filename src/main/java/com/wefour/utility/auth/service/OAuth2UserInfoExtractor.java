package com.wefour.utility.auth.service;

import com.wefour.utility.auth.model.entity.AuthProvider;

import java.util.Map;

/**
 * Utility extractor to normalize user attributes across different OAuth2 providers (Google, GitHub, etc.).
 */
public class OAuth2UserInfoExtractor {

    private final String id;
    private final String name;
    private final String email;
    private final String avatarUrl;
    private final AuthProvider provider;

    public OAuth2UserInfoExtractor(String id, String name, String email, String avatarUrl, AuthProvider provider) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.provider = provider;
    }

    public static OAuth2UserInfoExtractor extract(String registrationId, Map<String, Object> attributes) {
        AuthProvider provider = AuthProvider.fromString(registrationId);

        return switch (provider) {
            case GOOGLE -> extractGoogle(attributes);
            case GITHUB -> extractGitHub(attributes);
            default -> extractGeneric(attributes, provider);
        };
    }

    private static OAuth2UserInfoExtractor extractGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfoExtractor.builder()
                .provider(AuthProvider.GOOGLE)
                .id((String) attributes.get("sub"))
                .name((String) attributes.getOrDefault("name", "Google User"))
                .email((String) attributes.get("email"))
                .avatarUrl((String) attributes.get("picture"))
                .build();
    }

    private static OAuth2UserInfoExtractor extractGitHub(Map<String, Object> attributes) {
        Object idObj = attributes.get("id");
        String id = idObj != null ? String.valueOf(idObj) : null;
        String name = (String) attributes.get("name");
        String login = (String) attributes.get("login");
        String email = (String) attributes.get("email");

        if (name == null || name.isBlank()) {
            name = login != null ? login : "GitHub User";
        }
        if (email == null || email.isBlank()) {
            email = (login != null ? login : id) + "@users.noreply.github.com";
        }

        return OAuth2UserInfoExtractor.builder()
                .provider(AuthProvider.GITHUB)
                .id(id)
                .name(name)
                .email(email)
                .avatarUrl((String) attributes.get("avatar_url"))
                .build();
    }

    private static OAuth2UserInfoExtractor extractGeneric(Map<String, Object> attributes, AuthProvider provider) {
        Object idObj = attributes.get("id");
        String id = idObj != null ? String.valueOf(idObj) : (String) attributes.get("sub");
        String name = (String) attributes.getOrDefault("name", "OAuth User");
        String email = (String) attributes.get("email");

        return OAuth2UserInfoExtractor.builder()
                .provider(provider)
                .id(id)
                .name(name)
                .email(email != null ? email : id + "@oauth.local")
                .avatarUrl((String) attributes.get("avatar_url"))
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public static class Builder {
        private String id;
        private String name;
        private String email;
        private String avatarUrl;
        private AuthProvider provider;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public Builder provider(AuthProvider provider) {
            this.provider = provider;
            return this;
        }

        public OAuth2UserInfoExtractor build() {
            return new OAuth2UserInfoExtractor(id, name, email, avatarUrl, provider);
        }
    }
}
