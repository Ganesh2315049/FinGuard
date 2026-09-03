package com.finguard.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import java.util.*;

@Service
public class JwtService {
    private final SecretKey key;
    public JwtService(@Value("${finguard.jwt.secret}") String secret) { key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); }
    public String create(UUID id, String email, String role) { return Jwts.builder().subject(email).claim("uid", id.toString()).claim("role", role).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + 86400000)).signWith(key).compact(); }
    public Claims claims(String token) { return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload(); }
}
