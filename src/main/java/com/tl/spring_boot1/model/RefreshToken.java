package com.tl.spring_boot1.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name="refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String token;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // getters & setters

    public Integer getId() {return id;}
    public String getToken() {return token;}
    public Integer getUser_id() {return userId;}
    public LocalDateTime getCreated_at() {return createdAt;}
    public LocalDateTime getExpires_at() {return expiresAt;}

    public void setId(Integer id) {this.id = id;}
    public void setToken(String token) {this.token = token;}
    public void setUser_id(Integer user_id) {this.userId = user_id;}
    public void setCreated_at(LocalDateTime created_at) {this.createdAt = created_at;}
    public void setExpires_at(LocalDateTime expires_at) {this.expiresAt = expires_at;}
}
