package com.wefour.utility.auth.model.dto;

import com.wefour.utility.auth.model.entity.AuthProvider;
import com.wefour.utility.auth.model.entity.Role;

import java.util.Map;

/**
 * Data Transfer Object representing authenticated user profile and attributes.
 */
public class UserResponseDto {

    private Long id;
    private String email;
    private String name;
    private String avatarUrl;
    private AuthProvider provider;
    private String providerId;
    private Role role;
    private Map<String, Object> attributes;

    public UserResponseDto() {
    }

    public UserResponseDto(Long id, String email, String name, String avatarUrl,
                           AuthProvider provider, String providerId, Role role, Map<String, Object> attributes) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
        this.attributes = attributes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public void setProvider(AuthProvider provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public static class Builder {
        private Long id;
        private String email;
        private String name;
        private String avatarUrl;
        private AuthProvider provider;
        private String providerId;
        private Role role;
        private Map<String, Object> attributes;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
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

        public Builder providerId(String providerId) {
            this.providerId = providerId;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder attributes(Map<String, Object> attributes) {
            this.attributes = attributes;
            return this;
        }

        public UserResponseDto build() {
            return new UserResponseDto(id, email, name, avatarUrl, provider, providerId, role, attributes);
        }
    }
}
