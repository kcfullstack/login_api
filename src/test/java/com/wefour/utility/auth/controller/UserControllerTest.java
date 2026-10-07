package com.wefour.utility.auth.controller;

import com.wefour.utility.auth.model.entity.AuthProvider;
import com.wefour.utility.auth.model.entity.Role;
import com.wefour.utility.auth.model.entity.User;
import com.wefour.utility.auth.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("GET /api/v1/auth/status should be publicly accessible without authentication")
    void testPublicAuthStatus() throws Exception {
        mockMvc.perform(get("/api/v1/auth/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.data.authenticated").value(false));
    }

    @Test
    @DisplayName("GET /api/v1/auth/user without credentials should return 401 Unauthorized")
    void testUserEndpointUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/auth/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/v1/auth/user with OAuth2 login should return user details successfully")
    void testUserEndpointWithOAuth2Login() throws Exception {
        User mockUser = User.builder()
                .id(1L)
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .provider(AuthProvider.GOOGLE)
                .providerId("google-12345")
                .role(Role.ROLE_USER)
                .enabled(true)
                .build();

        when(userService.getUserByEmail("jane.doe@example.com")).thenReturn(Optional.of(mockUser));
        when(userService.toDto(any(), any())).thenCallRealMethod();

        mockMvc.perform(get("/api/v1/auth/user")
                        .with(oauth2Login()
                                .authorities(Set.of(new SimpleGrantedAuthority("ROLE_USER")))
                                .attributes(attrs -> {
                                    attrs.put("sub", "google-12345");
                                    attrs.put("name", "Jane Doe");
                                    attrs.put("email", "jane.doe@example.com");
                                    attrs.put("picture", "https://example.com/avatar.png");
                                })
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("jane.doe@example.com"))
                .andExpect(jsonPath("$.data.name").value("Jane Doe"))
                .andExpect(jsonPath("$.data.provider").value("GOOGLE"));
    }
}
