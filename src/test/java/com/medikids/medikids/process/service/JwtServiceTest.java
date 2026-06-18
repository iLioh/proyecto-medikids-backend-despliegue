package com.medikids.medikids.process.service;

import com.medikids.medikids.process.domain.Usuario;
import com.medikids.medikids.process.repository.RolPermisoRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("JwtService - Tests de generación y validación de tokens JWT")
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @MockitoBean(name = "rolPermisoRepository")
    private RolPermisoRepository rolPermisoRepository;

    private Usuario testUsuario;

    @BeforeEach
    void setUp() {
        testUsuario = new Usuario();
        testUsuario.setId_usuario(1);
        testUsuario.setEmail("test@medikids.com");
        testUsuario.setNombres("Juan Test");
        testUsuario.setApellidos("Pérez Test");
        testUsuario.setTelefono(300123456);
        testUsuario.setActivo(true);
    }

    @Test
    @DisplayName("Debe generar un token JWT válido")
    void testGenerateToken() {
        // Arrange & Act
        String token = jwtService.generateToken(testUsuario);

        // Assert
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT tiene 3 partes: header.payload.signature
        assertDoesNotThrow(() -> jwtService.extractEmail(token));
    }

    @Test
    @DisplayName("Debe extraer el email correctamente del token")
    void testExtractEmail() {
        // Arrange
        String token = jwtService.generateToken(testUsuario);

        // Act
        String extractedEmail = jwtService.extractEmail(token);

        // Assert
        assertEquals("test@medikids.com", extractedEmail);
    }

    @Test
    @DisplayName("Debe validar un token válido correctamente")
    void testValidateTokenValid() {
        // Arrange
        String token = jwtService.generateToken(testUsuario);

        // Act
        boolean isValid = jwtService.validateToken(token, "test@medikids.com");

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Debe rechazar un token con email incorrecto")
    void testValidateTokenInvalidEmail() {
        // Arrange
        String token = jwtService.generateToken(testUsuario);

        // Act
        boolean isValid = jwtService.validateToken(token, "otro@medikids.com");

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debe rechazar un token malformado")
    void testValidateTokenMalformed() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtService.validateToken(invalidToken, "test@medikids.com");

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debe extraer todos los claims del token")
    void testExtractAllClaims() {
        // Arrange
        String token = jwtService.generateToken(testUsuario);

        // Act
        Map<String, Object> claims = jwtService.extractAllClaims(token);

        // Assert
        assertNotNull(claims);
        assertTrue(claims.containsKey("sub")); // subject
        assertEquals("test@medikids.com", claims.get("sub"));
    }

    @Test
    @DisplayName("Debe generar token de admin con expiration diferente")
    void testGenerateAdminToken() {
        // Arrange & Act
        String adminToken = jwtService.generateAdminToken(testUsuario);
        String regularToken = jwtService.generateToken(testUsuario);

        // Assert
        assertNotNull(adminToken);
        assertNotNull(regularToken);
        assertNotEquals(adminToken, regularToken); // Diferentes debido a expiration
        assertTrue(jwtService.validateToken(adminToken, "test@medikids.com"));
    }

    @Test
    @DisplayName("Debe rechazar un token vacío")
    void testValidateTokenEmpty() {
        // Arrange & Act
        boolean isValid = jwtService.validateToken("", "test@medikids.com");

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debe rechazar un token null")
    void testValidateTokenNull() {
        // Arrange & Act
        boolean isValid = jwtService.validateToken(null, "test@medikids.com");

        // Assert
        assertFalse(isValid);
    }
}
