package com.wefour.utility.auth.service;

import com.wefour.utility.auth.model.dto.UserResponseDto;
import com.wefour.utility.auth.model.entity.AuthProvider;
import com.wefour.utility.auth.model.entity.Role;
import com.wefour.utility.auth.model.entity.User;
import com.wefour.utility.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

/**
 * Service managing user entity lifecycle, registration, and persistence.
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User registerOrUpdateOAuthUser(AuthProvider provider,
                                          String providerId,
                                          String email,
                                          String name,
                                          String avatarUrl) {
        log.info("Processing OAuth2 login for email: {} with provider: {}", email, provider);

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(name);
            user.setAvatarUrl(avatarUrl);
            user.setProvider(provider);
            user.setProviderId(providerId);
            log.info("Updating existing user ID: {}", user.getId());
            return userRepository.save(user);
        } else {
            User newUser = User.builder()
                    .email(email)
                    .name(name)
                    .avatarUrl(avatarUrl)
                    .provider(provider)
                    .providerId(providerId)
                    .role(Role.ROLE_USER)
                    .enabled(true)
                    .build();
            log.info("Registering new OAuth2 user with email: {}", email);
            return userRepository.save(newUser);
        }
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public UserResponseDto toDto(User user, Map<String, Object> attributes) {
        return UserResponseDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .avatarUrl(user.getAvatarUrl())
                .provider(user.getProvider())
                .providerId(user.getProviderId())
                .role(user.getRole())
                .attributes(attributes)
                .build();
    }
}
