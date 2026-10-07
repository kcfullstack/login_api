package com.wefour.utility.auth.controller;

import com.wefour.utility.auth.model.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.ResolvableType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller providing OAuth2 login navigation and provider discovery endpoints.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "OAuth2 Discovery", description = "Endpoints for OAuth2 provider discovery and login links")
public class AuthController {

    private final ClientRegistrationRepository clientRegistrationRepository;

    private static final String OAUTH2_AUTHORIZATION_BASE_URI = "/oauth2/authorization/";

    public AuthController(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    /**
     * Lists available OAuth2 identity providers and their initiation URLs.
     */
    @GetMapping("/providers")
    @Operation(
            summary = "List Configured OAuth2 Providers",
            description = "Provides authorization flow URLs for each configured identity provider (e.g., Google, GitHub)."
    )
    public ResponseEntity<ApiResponse<Map<String, String>>> getLoginUrls() {
        Map<String, String> oauth2AuthenticationUrls = new HashMap<>();

        Iterable<ClientRegistration> clientRegistrations = null;
        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository).as(Iterable.class);
        if (type != ResolvableType.NONE && ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
        }

        if (clientRegistrations != null) {
            for (ClientRegistration registration : clientRegistrations) {
                oauth2AuthenticationUrls.put(
                        registration.getClientName(),
                        OAUTH2_AUTHORIZATION_BASE_URI + registration.getRegistrationId()
                );
            }
        }

        return ResponseEntity.ok(ApiResponse.success(
                oauth2AuthenticationUrls,
                "Configured OAuth2 providers retrieved successfully."
        ));
    }
}
