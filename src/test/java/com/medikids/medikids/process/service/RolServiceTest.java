package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.RolRequest;
import com.medikids.medikids.process.domain.Rol;
import com.medikids.medikids.process.dto.RolDto;
import com.medikids.medikids.process.repository.RolRepository;
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
@DisplayName("RolService - Tests de gestión de roles")
class RolServiceTest {

    @Autowired
    private RolService rolService;

    @MockitoBean
    private RolRepository rolRepository;

    private Rol testRol;
    private RolRequest rolRequest;

    @BeforeEach
    void setUp() {
        testRol = new Rol();
        testRol.setId_rol(1);
        testRol.setNombre_rol("CLIENTE");

        rolRequest = new RolRequest();
        rolRequest.setNombre_rol("CLIENTE");
    }

    @Test
    @DisplayName("Debe obtener todos los roles")
    void testGetAll() {
        // Arrange
        when(rolRepository.findAll()).thenReturn(List.of(testRol));

        // Act
        List<RolDto> result = rolService.getAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.size() > 0);
        verify(rolRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener un rol por ID")
    void testGetById() {
        // Arrange
        when(rolRepository.findById(1)).thenReturn(Optional.of(testRol));

        // Act
        RolDto result = rolService.getById(1);

        // Assert
        assertNotNull(result);
        assertEquals("CLIENTE", result.getNombre_rol());
        verify(rolRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe retornar null al obtener rol inexistente")
    void testGetByIdNotFound() {
        // Arrange
        when(rolRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        RolDto result = rolService.getById(99);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe guardar un nuevo rol")
    void testSave() {
        // Arrange
        when(rolRepository.save(any(Rol.class))).thenReturn(testRol);

        // Act
        RolDto result = rolService.save(rolRequest);

        // Assert
        assertNotNull(result);
        assertEquals("CLIENTE", result.getNombre_rol());
        verify(rolRepository, times(1)).save(any(Rol.class));
    }

    @Test
    @DisplayName("Debe actualizar un rol existente")
    void testUpdate() {
        // Arrange
        RolRequest updateRequest = new RolRequest();
        updateRequest.setNombre_rol("MEDICO");

        when(rolRepository.findById(1)).thenReturn(Optional.of(testRol));
        when(rolRepository.save(any(Rol.class))).thenReturn(testRol);

        // Act
        RolDto result = rolService.update(1, updateRequest);

        // Assert
        assertNotNull(result);
        verify(rolRepository, times(1)).save(any(Rol.class));
    }

    @Test
    @DisplayName("Debe retornar null al actualizar rol inexistente")
    void testUpdateNotFound() {
        // Arrange
        when(rolRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        RolDto result = rolService.update(99, rolRequest);

        // Assert
        assertNull(result);
        verify(rolRepository, never()).save(any(Rol.class));
    }

    @Test
    @DisplayName("Debe obtener múltiples roles")
    void testGetAllMultiple() {
        // Arrange
        Rol rol2 = new Rol();
        rol2.setId_rol(2);
        rol2.setNombre_rol("MEDICO");

        Rol rol3 = new Rol();
        rol3.setId_rol(3);
        rol3.setNombre_rol("ADMIN");

        when(rolRepository.findAll()).thenReturn(List.of(testRol, rol2, rol3));

        // Act
        List<RolDto> result = rolService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay roles")
    void testGetAllEmpty() {
        // Arrange
        when(rolRepository.findAll()).thenReturn(List.of());

        // Act
        List<RolDto> result = rolService.getAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
