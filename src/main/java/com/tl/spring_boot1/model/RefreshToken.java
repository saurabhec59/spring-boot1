package com.tl.spring_boot1.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name="refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "token_hash", unique = true, nullable = false)
    private String tokenHash;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false, updatable = false)
    private Instant expiresAt;

    // getters & setters

    public Integer getId() {return id;}
    public String getTokenHash() {return tokenHash;}
    public Integer getUserId() {return userId;}
    public Instant getCreatedAt() {return createdAt;}
    public Instant getExpiresAt() {return expiresAt;}

    public void setId(Integer id) {this.id = id;}
    public void setTokenHash(String tokenHash) {this.tokenHash = tokenHash;}
    public void setUserId(Integer userId) {this.userId = userId;}
    public void setCreatedAt(Instant createdAt) {this.createdAt = createdAt;}
    public void setExpiresAt(Instant expiresAt) {this.expiresAt = expiresAt;}
}
