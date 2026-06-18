package com.medikids.medikids.process.repository;

import com.medikids.medikids.process.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findByIdUsuarioAndRevokedFalse(Integer idUsuario);
    void deleteByExpiryDateBefore(LocalDateTime before);
}
