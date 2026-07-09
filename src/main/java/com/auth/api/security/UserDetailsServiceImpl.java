package com.auth.api.security;

import com.auth.api.entity.User;
import com.auth.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    /** Called by Spring Security — looks up by username (used internally) */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + username));
        return UserPrincipal.build(user);
    }

    /** Used by AuthService for client-credential login */
    @Transactional
    public UserDetails loadUserByClientId(String clientId) throws UsernameNotFoundException {
        User user = userRepository.findByClientId(clientId)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No user found for clientId: " + clientId));
        return UserPrincipal.build(user);
    }
}
