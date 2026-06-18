package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.ClienteRequest;
import com.medikids.medikids.process.domain.Cliente;
import com.medikids.medikids.process.dto.ClienteDto;
import com.medikids.medikids.process.domain.Usuario;
import com.medikids.medikids.process.repository.ClienteRepository;
import com.medikids.medikids.process.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("ClienteService - Tests de gestión de clientes")
class ClienteServiceTest {

    @Autowired
    private ClienteService clienteService;

    @MockitoBean
    private ClienteRepository clienteRepository;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    private Cliente testCliente;
    private ClienteRequest clienteRequest;

    @BeforeEach
    void setUp() {
        Usuario usuario = new Usuario();
        usuario.setId_usuario(1);

        testCliente = new Cliente();
        testCliente.setId_cliente(1);
        testCliente.setUsuario(usuario);
        testCliente.setDni_responsable(12345678);
        testCliente.setDireccion("Calle Principal 123");

        clienteRequest = new ClienteRequest();
        clienteRequest.setId_usuario(1);
        clienteRequest.setDni_responsable(12345678);
        clienteRequest.setDireccion("Calle Principal 123");
    }

    @Test
    @DisplayName("Debe obtener todos los clientes")
    void testGetAll() {
        // Arrange
        when(clienteRepository.findAll()).thenReturn(List.of(testCliente));

        // Act
        List<ClienteDto> result = clienteService.getAll();

        // Assert
        assertNotNull(result);
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener un cliente por ID")
    void testGetById() {
        // Arrange
        when(clienteRepository.findById(1)).thenReturn(Optional.of(testCliente));

        // Act
        ClienteDto result = clienteService.getById(1);

        // Assert
        assertNotNull(result);
        verify(clienteRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe retornar null al obtener cliente inexistente")
    void testGetByIdNotFound() {
        // Arrange
        when(clienteRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        ClienteDto result = clienteService.getById(99);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe obtener un cliente por ID de usuario")
    void testGetByIdUsuario() {
        // Arrange
        when(clienteRepository.findByIdUsuario(1)).thenReturn(Optional.of(testCliente));

        // Act
        ClienteDto result = clienteService.getByIdUsuario(1);

        // Assert
        assertNotNull(result);
        verify(clienteRepository, times(1)).findByIdUsuario(1);
    }

    @Test
    @DisplayName("Debe obtener un cliente por DNI")
    void testGetByDni() {
        // Arrange
        when(clienteRepository.findByDni("12345678")).thenReturn(Optional.of(testCliente));

        // Act
        ClienteDto result = clienteService.getByDni("12345678");

        // Assert
        assertNotNull(result);
        assertEquals(12345678, result.getDni_responsable());
        verify(clienteRepository, times(1)).findByDni("12345678");
    }

    @Test
    @DisplayName("Debe retornar null al buscar cliente por DNI inexistente")
    void testGetByDniNotFound() {
        // Arrange
        when(clienteRepository.findByDni("99999999")).thenReturn(Optional.empty());

        // Act
        ClienteDto result = clienteService.getByDni("99999999");

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe guardar un nuevo cliente")
    void testSave() {
        // Arrange
        when(clienteRepository.save(any(Cliente.class))).thenReturn(testCliente);

        // Act
        ClienteDto result = clienteService.save(clienteRequest);

        // Assert
        assertNotNull(result);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debe actualizar un cliente existente")
    void testUpdate() {
        // Arrange
        ClienteRequest updateRequest = new ClienteRequest();
        updateRequest.setDni_responsable(87654321);
        updateRequest.setDireccion("Calle Secundaria 456");

        when(clienteRepository.findById(1)).thenReturn(Optional.of(testCliente));
        when(clienteRepository.save(any(Cliente.class))).thenReturn(testCliente);

        // Act
        ClienteDto result = clienteService.update(1, updateRequest);

        // Assert
        assertNotNull(result);
        verify(clienteRepository, times(1)).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debe retornar null al actualizar cliente inexistente")
    void testUpdateNotFound() {
        // Arrange
        when(clienteRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        ClienteDto result = clienteService.update(99, clienteRequest);

        // Assert
        assertNull(result);
        verify(clienteRepository, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay clientes")
    void testGetAllEmpty() {
        // Arrange
        when(clienteRepository.findAll()).thenReturn(List.of());

        // Act
        List<ClienteDto> result = clienteService.getAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
