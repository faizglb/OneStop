package com.acea.backend.service;

import com.acea.backend.domain.entity.User;
import com.acea.backend.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link UserDetailsService} for Spring Security.
 * Loads user by email (used as the username in this system).
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findActiveByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(
                "No active user found with email: " + email));
    }
}
