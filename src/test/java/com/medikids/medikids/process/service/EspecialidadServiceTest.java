package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.EspecialidadRequest;
import com.medikids.medikids.process.domain.Especialidad;
import com.medikids.medikids.process.dto.EspecialidadDto;
import com.medikids.medikids.process.repository.EspecialidadRepository;
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
@DisplayName("EspecialidadService - Tests de gestión de especialidades")
class EspecialidadServiceTest {

    @Autowired
    private EspecialidadService especialidadService;

    @MockitoBean
    private EspecialidadRepository especialidadRepository;

    private Especialidad testEspecialidad;
    private EspecialidadRequest especialidadRequest;

    @BeforeEach
    void setUp() {
        testEspecialidad = Especialidad.builder()
                .id_especialidad(1)
                .nombre("Pediatría General")
                .descripcion("Atención pediátrica general")
                .precio(50.00)
                .build();

        especialidadRequest = new EspecialidadRequest();
        especialidadRequest.setNombre("Pediatría General");
        especialidadRequest.setDescripcion("Atención pediátrica general");
        especialidadRequest.setPrecio(50.00);
    }

    @Test
    @DisplayName("Debe obtener todas las especialidades")
    void testGetAll() {
        // Arrange
        when(especialidadRepository.findAll()).thenReturn(List.of(testEspecialidad));

        // Act
        List<EspecialidadDto> result = especialidadService.getAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.size() > 0);
        verify(especialidadRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener una especialidad por ID")
    void testGetById() {
        // Arrange
        when(especialidadRepository.findById(1)).thenReturn(Optional.of(testEspecialidad));

        // Act
        EspecialidadDto result = especialidadService.getById(1);

        // Assert
        assertNotNull(result);
        assertEquals("Pediatría General", result.getNombre());
        verify(especialidadRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe retornar null al obtener especialidad inexistente")
    void testGetByIdNotFound() {
        // Arrange
        when(especialidadRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        EspecialidadDto result = especialidadService.getById(99);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe guardar una nueva especialidad")
    void testSave() {
        // Arrange
        when(especialidadRepository.save(any(Especialidad.class))).thenReturn(testEspecialidad);

        // Act
        EspecialidadDto result = especialidadService.save(especialidadRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Pediatría General", result.getNombre());
        verify(especialidadRepository, times(1)).save(any(Especialidad.class));
    }

    @Test
    @DisplayName("Debe actualizar una especialidad existente")
    void testUpdate() {
        // Arrange
        EspecialidadRequest updateRequest = new EspecialidadRequest();
        updateRequest.setNombre("Pediatría Avanzada");
        updateRequest.setDescripcion("Atención pediátrica avanzada");
        updateRequest.setPrecio(75.00);

        when(especialidadRepository.findById(1)).thenReturn(Optional.of(testEspecialidad));
        when(especialidadRepository.save(any(Especialidad.class))).thenReturn(testEspecialidad);

        // Act
        EspecialidadDto result = especialidadService.update(1, updateRequest);

        // Assert
        assertNotNull(result);
        verify(especialidadRepository, times(1)).save(any(Especialidad.class));
    }

    @Test
    @DisplayName("Debe retornar null al actualizar especialidad inexistente")
    void testUpdateNotFound() {
        // Arrange
        when(especialidadRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        EspecialidadDto result = especialidadService.update(99, especialidadRequest);

        // Assert
        assertNull(result);
        verify(especialidadRepository, never()).save(any(Especialidad.class));
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay especialidades")
    void testGetAllEmpty() {
        // Arrange
        when(especialidadRepository.findAll()).thenReturn(List.of());

        // Act
        List<EspecialidadDto> result = especialidadService.getAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe obtener múltiples especialidades")
    void testGetAllMultiple() {
        // Arrange
        Especialidad especialidad2 = Especialidad.builder()
                .id_especialidad(2)
                .nombre("Dermatología")
                .descripcion("Salud de la piel")
                .precio(60.00)
                .build();

        when(especialidadRepository.findAll()).thenReturn(List.of(testEspecialidad, especialidad2));

        // Act
        List<EspecialidadDto> result = especialidadService.getAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
