package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.UsuarioRequest;
import com.medikids.medikids.process.domain.Usuario;
import com.medikids.medikids.process.dto.UsuarioDto;
import com.medikids.medikids.process.repository.RolRepository;
import com.medikids.medikids.process.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("UsuarioService - Tests de gestión de usuarios")
class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Usuario testUsuario;
    private UsuarioRequest usuarioRequest;

    @BeforeEach
    void setUp() {
        testUsuario = new Usuario();
        testUsuario.setId_usuario(1);
        testUsuario.setEmail("user@medikids.com");
        testUsuario.setNombres("Juan");
        testUsuario.setApellidos("Pérez");
        testUsuario.setPassword(passwordEncoder.encode("password123"));
        testUsuario.setTelefono(300123456);
        testUsuario.setActivo(true);
        testUsuario.setVisible('1');

        usuarioRequest = new UsuarioRequest();
        usuarioRequest.setEmail("user@medikids.com");
        usuarioRequest.setNombres("Juan");
        usuarioRequest.setApellidos("Pérez");
        usuarioRequest.setPassword("password123");
        usuarioRequest.setTelefono(300123456);
        usuarioRequest.setId_rol(1);
    }

    @Test
    @DisplayName("Debe obtener un usuario por ID")
    void testGetById() {
        // Arrange
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(testUsuario));

        // Act
        UsuarioDto result = usuarioService.getById(1);

        // Assert
        assertNotNull(result);
        assertEquals("user@medikids.com", result.getEmail());
        verify(usuarioRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe retornar null cuando usuario no existe")
    void testGetByIdNotFound() {
        // Arrange
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        UsuarioDto result = usuarioService.getById(99);

        // Assert
        assertNull(result);
        verify(usuarioRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debe cambiar contraseña correctamente")
    void testChangePasswordSuccess() {
        // Arrange
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(testUsuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(testUsuario);

        // Act
        boolean result = usuarioService.changePassword(1, "password123", "newPassword456");

        // Assert
        assertTrue(result);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe fallar al cambiar contraseña con contraseña actual incorrecta")
    void testChangePasswordWrongCurrent() {
        // Arrange
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(testUsuario));

        // Act
        boolean result = usuarioService.changePassword(1, "wrongPassword", "newPassword456");

        // Assert
        assertFalse(result);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe fallar al cambiar contraseña de usuario inexistente")
    void testChangePasswordUserNotFound() {
        // Arrange
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        boolean result = usuarioService.changePassword(99, "password123", "newPassword456");

        // Assert
        assertFalse(result);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe eliminar (soft delete) un usuario")
    void testDeleteUser() {
        // Arrange
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(testUsuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(testUsuario);

        // Act
        Boolean result = usuarioService.delete(1);

        // Assert
        assertTrue(result);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe retornar false al eliminar usuario inexistente")
    void testDeleteUserNotFound() {
        // Arrange
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        Boolean result = usuarioService.delete(99);

        // Assert
        assertFalse(result);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe actualizar perfil del usuario")
    void testUpdateProfile() {
        // Arrange
        usuarioRequest.setNombres("Pedro");
        usuarioRequest.setApellidos("García");
        
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(testUsuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(testUsuario);

        // Act
        UsuarioDto result = usuarioService.updateProfile(1, usuarioRequest);

        // Assert
        assertNotNull(result);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    @DisplayName("Debe retornar null al actualizar perfil de usuario inexistente")
    void testUpdateProfileNotFound() {
        // Arrange
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        UsuarioDto result = usuarioService.updateProfile(99, usuarioRequest);

        // Assert
        assertNull(result);
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
