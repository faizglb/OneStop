package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findAllByUserIdAndRevokedFalse(UUID userId);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = :now, rt.revokeReason = :reason WHERE rt.user.id = :userId AND rt.revoked = false")
    void revokeAllByUserId(@Param("userId") UUID userId, @Param("now") Instant now, @Param("reason") String reason);

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true, rt.revokedAt = :now, rt.revokeReason = :reason WHERE rt.sessionId = :sessionId")
    void revokeBySessionId(@Param("sessionId") String sessionId, @Param("now") Instant now, @Param("reason") String reason);

    /** Cleanup job: delete all expired or revoked tokens older than 30 days. */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiresAt < :cutoff OR (rt.revoked = true AND rt.revokedAt < :cutoff)")
    void deleteExpiredAndRevoked(@Param("cutoff") Instant cutoff);

    boolean existsByTokenHashAndRevokedFalse(String tokenHash);
}
