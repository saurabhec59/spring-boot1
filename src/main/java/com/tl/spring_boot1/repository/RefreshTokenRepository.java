package com.tl.spring_boot1.repository;

import com.tl.spring_boot1.model.RefreshToken;
import com.tl.spring_boot1.model.RefreshTokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    void deleteByUserId(Integer userId);
    Optional<RefreshToken> findByTokenHash(String tokenHash);

     // @Modifying is required: without it Spring Data treats the JPQL as a SELECT query and fails. It tells Spring this query mutates
     // data (UPDATE/DELETE) rather than reads it. This runs as one DB round-trip (no N+1) and writes straight to the database

    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.status = :status WHERE rt.userId = :userId")
    int revokeAllByUserId(@Param("userId") Integer userId, @Param("status") RefreshTokenStatus status);
}
