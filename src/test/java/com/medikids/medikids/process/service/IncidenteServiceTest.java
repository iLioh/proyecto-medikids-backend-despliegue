package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.IncidenteRequest;
import com.medikids.medikids.expose.model.request.IncidenteRespuestaRequest;
import com.medikids.medikids.process.domain.Incidente;
import com.medikids.medikids.process.dto.IncidenteDto;
import com.medikids.medikids.process.repository.IncidenteRepository;
import com.medikids.medikids.process.repository.MedicoRepository;
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
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("IncidenteService - Tests de gestión de incidentes")
class IncidenteServiceTest {

    @Autowired
    private IncidenteService incidenteService;

    @MockitoBean
    private IncidenteRepository incidenteRepository;

    @MockitoBean
    private MedicoRepository medicoRepository;

    private Incidente testIncidente;
    private IncidenteRequest incidenteRequest;

    @BeforeEach
    void setUp() {
        testIncidente = new Incidente();
        testIncidente.setId_incidente(1);
        testIncidente.setId_medico(1);
        testIncidente.setTipo_incidente("Problema con cita");
        testIncidente.setDescripcion("El médico no llegó a la cita");
        testIncidente.setFecha_registro(LocalDateTime.now());

        incidenteRequest = new IncidenteRequest();
        incidenteRequest.setId_medico(1);
        incidenteRequest.setTipo_incidente("Problema con cita");
        incidenteRequest.setDescripcion("El médico no llegó a la cita");
    }

    @Test
    @DisplayName("Debe obtener todos los incidentes")
    void testGetAll() {
        // Arrange
        when(incidenteRepository.findAll()).thenReturn(List.of(testIncidente));

        // Act
        List<IncidenteDto> result = incidenteService.getAll();

        // Assert
        assertNotNull(result);
        verify(incidenteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener un incidente por ID")
    void testGetById() {
        // Arrange
        when(incidenteRepository.findById(1)).thenReturn(Optional.of(testIncidente));

        // Act
        IncidenteDto result = incidenteService.getById(1);

        // Assert
        assertNotNull(result);
        assertEquals("Problema con cita", result.getTipo_incidente());
        verify(incidenteRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe retornar null al obtener incidente inexistente")
    void testGetByIdNotFound() {
        // Arrange
        when(incidenteRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        IncidenteDto result = incidenteService.getById(99);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe obtener incidentes de un médico")
    void testGetByMedico() {
        // Arrange
        when(incidenteRepository.findByIdMedico(1)).thenReturn(List.of(testIncidente));

        // Act
        List<IncidenteDto> result = incidenteService.getByMedico(1);

        // Assert
        assertNotNull(result);
        assertTrue(result.size() > 0);
        verify(incidenteRepository, times(1)).findByIdMedico(1);
    }

    @Test
    @DisplayName("Debe guardar un nuevo incidente")
    void testSave() {
        // Arrange
        when(incidenteRepository.save(any(Incidente.class))).thenReturn(testIncidente);

        // Act
        IncidenteDto result = incidenteService.save(incidenteRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Problema con cita", result.getTipo_incidente());
        verify(incidenteRepository, times(1)).save(any(Incidente.class));
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay incidentes")
    void testGetAllEmpty() {
        // Arrange
        when(incidenteRepository.findAll()).thenReturn(List.of());

        // Act
        List<IncidenteDto> result = incidenteService.getAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe retornar lista vacía de incidentes para médico inexistente")
    void testGetByMedicoEmpty() {
        // Arrange
        when(incidenteRepository.findByIdMedico(99)).thenReturn(List.of());

        // Act
        List<IncidenteDto> result = incidenteService.getByMedico(99);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe obtener múltiples incidentes de un médico")
    void testGetByMedicoMultiple() {
        // Arrange
        Incidente incidente2 = new Incidente();
        incidente2.setId_incidente(2);
        incidente2.setId_medico(1);
        incidente2.setTipo_incidente("Otra queja");
        incidente2.setDescripcion("Servicio deficiente");
        incidente2.setFecha_registro(LocalDateTime.now());

        when(incidenteRepository.findByIdMedico(1)).thenReturn(List.of(testIncidente, incidente2));

        // Act
        List<IncidenteDto> result = incidenteService.getByMedico(1);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
