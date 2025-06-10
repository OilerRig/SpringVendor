package com.oilerrig.vendor.auth;

import com.oilerrig.vendor.data.entities.UserEntity;
import com.oilerrig.vendor.data.repository.jpa.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ApiAuthenticationService {

    private final String AUTH_TOKEN_HEADER_NAME;
    private List<String> AUTH_TOKENS;
    private final UserRepository userRepository;

    @Autowired
    public ApiAuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.AUTH_TOKEN_HEADER_NAME = "X-API-KEY";
        this.AUTH_TOKENS = userRepository
                .findAll()
                .stream()
                .map(UserEntity::getApiKey)
                .collect(Collectors.toList());
    }

    @Scheduled(initialDelay = 1, fixedDelay = 5, timeUnit = TimeUnit.MINUTES)
    public void updateKeyCache() {
        this.AUTH_TOKENS = userRepository
                .findAll()
                .stream()
                .map(UserEntity::getApiKey)
                .collect(Collectors.toList());
    }


    public Authentication getAuthentication(HttpServletRequest request) {
        String apiKey = request.getHeader(AUTH_TOKEN_HEADER_NAME);
        if (apiKey == null || !this.AUTH_TOKENS.contains(apiKey)) {
            throw new BadCredentialsException("Invalid API Key");
        }

        return new ApiKeyAuthentication(apiKey, AuthorityUtils.NO_AUTHORITIES);
    }
}