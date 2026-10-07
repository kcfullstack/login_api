package com.wefour.utility.auth.controller;

import com.wefour.utility.auth.model.dto.ApiResponse;
import com.wefour.utility.auth.model.dto.AuthStatusDto;
import com.wefour.utility.auth.model.dto.UserResponseDto;
import com.wefour.utility.auth.model.entity.AuthProvider;
import com.wefour.utility.auth.model.entity.Role;
import com.wefour.utility.auth.model.entity.User;
import com.wefour.utility.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller exposing user profile and authentication health endpoints.
 */
@RestController
@RequestMapping
@Tag(name = "User Authentication", description = "Endpoints for OAuth2 user profile and authentication status")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves authenticated user details.
     * Supports both /user and /api/v1/auth/user endpoints.
     */
    @GetMapping({"/user", "/api/v1/auth/user"})
    @Operation(
            summary = "Get Authenticated User Details",
            description = "Returns current authenticated user details and OAuth2 attributes",
            security = {@SecurityRequirement(name = "BearerAuth")}
    )
    public ResponseEntity<ApiResponse<UserResponseDto>> getCurrentUser(
            @AuthenticationPrincipal Object principal,
            Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("Unauthorized: No active authentication session found."));
        }

        String email = null;
        String name = authentication.getName();
        Map<String, Object> attributes = new HashMap<>();

        if (principal instanceof OAuth2User oAuth2User) {
            attributes = oAuth2User.getAttributes();
            email = (String) attributes.get("email");
            if (attributes.containsKey("name") && attributes.get("name") != null) {
                name = (String) attributes.get("name");
            }
        } else if (principal instanceof Jwt jwt) {
            attributes = jwt.getClaims();
            email = jwt.getClaimAsString("email");
            if (jwt.hasClaim("name")) {
                name = jwt.getClaimAsString("name");
            }
        }

        UserResponseDto userResponseDto;
        if (email != null) {
            Optional<User> userOptional = userService.getUserByEmail(email);
            if (userOptional.isPresent()) {
                userResponseDto = userService.toDto(userOptional.get(), attributes);
            } else {
                userResponseDto = buildFallbackDto(name, email, attributes);
            }
        } else {
            userResponseDto = buildFallbackDto(name, email, attributes);
        }

        return ResponseEntity.ok(ApiResponse.success(userResponseDto, "User details retrieved successfully."));
    }

    /**
     * Public endpoint to check service health and current authentication status.
     */
    @GetMapping("/api/v1/auth/status")
    @Operation(
            summary = "Authentication & Service Health Status",
            description = "Public endpoint checking service health and whether the current session is authenticated."
    )
    public ResponseEntity<ApiResponse<AuthStatusDto>> getAuthStatus() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isAuthenticated = authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());

        AuthStatusDto statusDto = AuthStatusDto.builder()
                .status("UP")
                .authenticated(isAuthenticated)
                .principal(isAuthenticated ? authentication.getName() : "anonymous")
                .authType(isAuthenticated ? authentication.getClass().getSimpleName() : "NONE")
                .timestamp(LocalDateTime.now())
                .message(isAuthenticated
                        ? "Session is active and authenticated."
                        : "Service is online. Authentication is required to access protected resources.")
                .build();

        return ResponseEntity.ok(ApiResponse.success(statusDto, "Authentication service status checked."));
    }

    private UserResponseDto buildFallbackDto(String name, String email, Map<String, Object> attributes) {
        return UserResponseDto.builder()
                .name(name)
                .email(email)
                .role(Role.ROLE_USER)
                .provider(AuthProvider.LOCAL)
                .attributes(attributes != null ? attributes : Collections.emptyMap())
                .build();
    }
}
