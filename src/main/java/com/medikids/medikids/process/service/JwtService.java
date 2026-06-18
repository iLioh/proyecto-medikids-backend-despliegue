package com.medikids.medikids.process.service;

import com.medikids.medikids.process.domain.Usuario;
import com.medikids.medikids.process.repository.RolPermisoRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    @Autowired(required = false)
    private RolPermisoRepository rolPermisoRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    @Value("${jwt.admin.expiration:1800000}")
    private long adminExpirationTime;

    public String generateToken(Usuario usuario) {
        Map<String, Object> claims = buildClaims(usuario);
        return Jwts.builder()
                .claims(claims)
                .subject(usuario.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateAdminToken(Usuario usuario) {
        Map<String, Object> claims = buildClaims(usuario);
        return Jwts.builder()
                .claims(claims)
                .subject(usuario.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + adminExpirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public Map<String, Object> extractAllClaims(String token) {
        return getClaims(token);
    }

    public boolean validateToken(String token, String email) {
        try {
            String tokenEmail = extractEmail(token);
            return tokenEmail.equals(email) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Map<String, Object> buildClaims(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", usuario.getId_usuario());
        claims.put("email", usuario.getEmail());
        claims.put("nombres", usuario.getNombres());
        claims.put("apellidos", usuario.getApellidos());
        claims.put("id_rol", usuario.getId_rol());

        if (rolPermisoRepository != null) {
            List<String> permisos = rolPermisoRepository.findCodigosPermisoByIdRol(usuario.getId_rol());
            claims.put("permisos", permisos);
        }

        return claims;
    }
}
