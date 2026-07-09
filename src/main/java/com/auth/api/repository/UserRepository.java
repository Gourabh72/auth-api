package com.auth.api.repository;

import com.auth.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByClientId(String clientId);
    Optional<User> findByApplicationCode(String applicationCode);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByClientId(String clientId);
    boolean existsByApplicationCode(String applicationCode);
}
