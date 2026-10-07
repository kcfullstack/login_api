package com.wefour.utility.auth.service;

import com.wefour.utility.auth.model.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

/**
 * Custom OAuth2UserService hook that intercepts OAuth2 login,
 * normalizes user attributes from identity providers (Google, GitHub),
 * and persists or updates the user in the database.
 */
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger log = LoggerFactory.getLogger(CustomOAuth2UserService.class);

    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        try {
            return processOAuth2User(registrationId, oAuth2User, userRequest);
        } catch (Exception ex) {
            log.error("Error processing OAuth2 user from provider: {}", registrationId, ex);
            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }

    private OAuth2User processOAuth2User(String registrationId,
                                         OAuth2User oAuth2User,
                                         OAuth2UserRequest userRequest) {
        OAuth2UserInfoExtractor userInfo = OAuth2UserInfoExtractor.extract(registrationId, oAuth2User.getAttributes());

        User user = userService.registerOrUpdateOAuthUser(
                userInfo.getProvider(),
                userInfo.getId(),
                userInfo.getEmail(),
                userInfo.getName(),
                userInfo.getAvatarUrl()
        );

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                new SimpleGrantedAuthority(user.getRole().name())
        );

        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), userNameAttributeName);
    }
}
