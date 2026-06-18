package com.medikids.medikids.process.service;

import com.medikids.medikids.process.domain.RefreshToken;
import com.medikids.medikids.process.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${jwt.refresh.expiration}")
    private long refreshExpirationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public RefreshToken createRefreshToken(Integer userId, String fingerprint, String ipOrigen) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .idUsuario(userId)
                .fingerprint(fingerprint)
                .ipOrigen(ipOrigen)
                .expiryDate(LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public boolean isSessionValid(RefreshToken rt, String currentFingerprint, String currentIp) {
        return (rt.getFingerprint() == null || rt.getFingerprint().equals(currentFingerprint))
                && (rt.getIpOrigen() == null || rt.getIpOrigen().equals(currentIp));
    }

    @Transactional
    public void revokeToken(String token) {
        findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }

    @Transactional
    public void revokeAllUserTokens(Integer userId) {
        var tokens = refreshTokenRepository.findByIdUsuarioAndRevokedFalse(userId);
        tokens.forEach(t -> t.setRevoked(true));
        refreshTokenRepository.saveAll(tokens);
    }

    @Transactional
    @Scheduled(cron = "0 0 */6 * * *")
    public void purgeExpiredTokens() {
        refreshTokenRepository.deleteByExpiryDateBefore(LocalDateTime.now());
    }
}
