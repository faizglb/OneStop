package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link User} entity.
 * Extends JpaRepository for full CRUD + pagination support.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    @Query("SELECT u FROM User u WHERE u.email = :email AND u.deletedAt IS NULL AND u.active = true")
    Optional<User> findActiveByEmail(@Param("email") String email);

    @Modifying
    @Query("UPDATE User u SET u.lastLoginAt = :now, u.loginCount = u.loginCount + 1, u.failedLoginCount = 0, u.lockedUntil = null WHERE u.id = :id")
    void recordSuccessfulLogin(@Param("id") UUID id, @Param("now") Instant now);

    @Modifying
    @Query("UPDATE User u SET u.failedLoginCount = u.failedLoginCount + 1 WHERE u.id = :id")
    void incrementFailedLoginCount(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE User u SET u.lockedUntil = :until WHERE u.id = :id")
    void lockAccount(@Param("id") UUID id, @Param("until") Instant until);

    @Modifying
    @Query("UPDATE User u SET u.emailVerified = true WHERE u.id = :id")
    void markEmailVerified(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE User u SET u.password = :hash WHERE u.id = :id")
    void updatePassword(@Param("id") UUID id, @Param("hash") String hash);

    org.springframework.data.domain.Page<User> findByEmailContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
        String email, String firstName, String lastName, org.springframework.data.domain.Pageable pageable
    );

    org.springframework.data.domain.Page<User> findByRole(com.acea.backend.domain.enums.Role role, org.springframework.data.domain.Pageable pageable);

    long countByActiveTrueAndSuspendedFalse();
}
