package com.wefour.utility.auth.model.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for authentication status and service health check.
 */
public class AuthStatusDto {

    private String status;
    private boolean authenticated;
    private String principal;
    private String authType;
    private LocalDateTime timestamp;
    private String message;

    public AuthStatusDto() {
    }

    public AuthStatusDto(String status, boolean authenticated, String principal,
                         String authType, LocalDateTime timestamp, String message) {
        this.status = status;
        this.authenticated = authenticated;
        this.principal = principal;
        this.authType = authType;
        this.timestamp = timestamp;
        this.message = message;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class Builder {
        private String status;
        private boolean authenticated;
        private String principal;
        private String authType;
        private LocalDateTime timestamp;
        private String message;

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder authenticated(boolean authenticated) {
            this.authenticated = authenticated;
            return this;
        }

        public Builder principal(String principal) {
            this.principal = principal;
            return this;
        }

        public Builder authType(String authType) {
            this.authType = authType;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public AuthStatusDto build() {
            return new AuthStatusDto(status, authenticated, principal, authType, timestamp, message);
        }
    }
}
