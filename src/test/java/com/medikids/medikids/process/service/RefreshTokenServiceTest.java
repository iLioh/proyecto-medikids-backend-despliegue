package com.medikids.medikids.process.service;

import com.medikids.medikids.process.domain.RefreshToken;
import com.medikids.medikids.process.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("RefreshTokenService - Tests de refresh tokens")
class RefreshTokenServiceTest {

    @Autowired
    private RefreshTokenService refreshTokenService;

    @MockitoBean
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshToken testToken;

    @BeforeEach
    void setUp() {
        testToken = RefreshToken.builder()
                .id(1L)
                .token("test-refresh-token-123")
                .idUsuario(1)
                .fingerprint("fingerprint-123")
                .ipOrigen("192.168.1.1")
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();
    }

    @Test
    @DisplayName("Debe crear un refresh token correctamente")
    void testCreateRefreshToken() {
        // Arrange
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testToken);

        // Act
        RefreshToken result = refreshTokenService.createRefreshToken(1, "fingerprint-123", "192.168.1.1");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getIdUsuario());
        assertFalse(result.isRevoked());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Debe encontrar un refresh token por su valor")
    void testFindByToken() {
        // Arrange
        when(refreshTokenRepository.findByToken("test-refresh-token-123"))
                .thenReturn(Optional.of(testToken));

        // Act
        Optional<RefreshToken> result = refreshTokenService.findByToken("test-refresh-token-123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test-refresh-token-123", result.get().getToken());
        verify(refreshTokenRepository, times(1)).findByToken("test-refresh-token-123");
    }

    @Test
    @DisplayName("Debe retornar vacío cuando token no existe")
    void testFindByTokenNotFound() {
        // Arrange
        when(refreshTokenRepository.findByToken("non-existent-token"))
                .thenReturn(Optional.empty());

        // Act
        Optional<RefreshToken> result = refreshTokenService.findByToken("non-existent-token");

        // Assert
        assertFalse(result.isPresent());
        verify(refreshTokenRepository, times(1)).findByToken("non-existent-token");
    }

    @Test
    @DisplayName("Debe validar una sesión correcta")
    void testIsSessionValidCorrect() {
        // Arrange & Act
        boolean result = refreshTokenService.isSessionValid(
                testToken, 
                "fingerprint-123", 
                "192.168.1.1"
        );

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe rechazar sesión con fingerprint incorrecto")
    void testIsSessionInvalidFingerprint() {
        // Arrange & Act
        boolean result = refreshTokenService.isSessionValid(
                testToken, 
                "wrong-fingerprint", 
                "192.168.1.1"
        );

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe rechazar sesión con IP incorrecta")
    void testIsSessionInvalidIp() {
        // Arrange & Act
        boolean result = refreshTokenService.isSessionValid(
                testToken, 
                "fingerprint-123", 
                "192.168.1.99"
        );

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe validar sesión sin validaciones (null)")
    void testIsSessionValidWithNullValues() {
        // Arrange
        RefreshToken tokenWithoutValidation = RefreshToken.builder()
                .id(2L)
                .token("test-token-2")
                .idUsuario(2)
                .fingerprint(null)
                .ipOrigen(null)
                .expiryDate(LocalDateTime.now().plusDays(7))
                .revoked(false)
                .build();

        // Act
        boolean result = refreshTokenService.isSessionValid(
                tokenWithoutValidation, 
                "any-fingerprint", 
                "any-ip"
        );

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe revocar un token correctamente")
    void testRevokeToken() {
        // Arrange
        when(refreshTokenRepository.findByToken("test-refresh-token-123"))
                .thenReturn(Optional.of(testToken));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(testToken);

        // Act
        refreshTokenService.revokeToken("test-refresh-token-123");

        // Assert
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("Debe revocar todos los tokens de un usuario")
    void testRevokeAllUserTokens() {
        // Arrange
        when(refreshTokenRepository.findByIdUsuarioAndRevokedFalse(1))
                .thenReturn(List.of(testToken));
        when(refreshTokenRepository.saveAll(any()))
                .thenReturn(List.of(testToken));

        // Act
        refreshTokenService.revokeAllUserTokens(1);

        // Assert
        verify(refreshTokenRepository, times(1)).findByIdUsuarioAndRevokedFalse(1);
        verify(refreshTokenRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("Debe no hacer nada al revocar tokens de usuario sin tokens activos")
    void testRevokeAllUserTokensEmpty() {
        // Arrange
        when(refreshTokenRepository.findByIdUsuarioAndRevokedFalse(99))
                .thenReturn(List.of());
        when(refreshTokenRepository.saveAll(any()))
                .thenReturn(List.of());

        // Act
        refreshTokenService.revokeAllUserTokens(99);

        // Assert
        verify(refreshTokenRepository, times(1)).findByIdUsuarioAndRevokedFalse(99);
        verify(refreshTokenRepository, times(1)).saveAll(any());
    }
}
