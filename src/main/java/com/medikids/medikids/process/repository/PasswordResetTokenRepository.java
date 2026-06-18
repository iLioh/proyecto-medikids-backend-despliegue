package com.medikids.medikids.process.repository;

import com.medikids.medikids.process.domain.PasswordResetToken;
import com.medikids.medikids.process.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUsuario(Usuario usuario);

    @Modifying
    @Transactional
    void deleteByExpiryDateBefore(LocalDateTime now);
}
