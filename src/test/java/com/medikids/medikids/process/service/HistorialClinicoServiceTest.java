package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.HistorialClinicoRequest;
import com.medikids.medikids.process.domain.HistorialClinico;
import com.medikids.medikids.process.dto.HistorialClinicoDto;
import com.medikids.medikids.process.repository.CitaRepository;
import com.medikids.medikids.process.repository.HistorialClinicoRepository;
import com.medikids.medikids.process.repository.PacienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("HistorialClinicoService - Tests de gestión de historial clínico")
class HistorialClinicoServiceTest {

    @Autowired
    private HistorialClinicoService historialClinicoService;

    @MockitoBean
    private HistorialClinicoRepository historialClinicoRepository;

    @MockitoBean
    private CitaRepository citaRepository;

    @MockitoBean
    private PacienteRepository pacienteRepository;

    private HistorialClinico testHistorial;
    private HistorialClinicoRequest historialRequest;

    @BeforeEach
    void setUp() {
        testHistorial = new HistorialClinico();
        testHistorial.setId_historial_clinico(1);
        testHistorial.setId_cita(1);
        testHistorial.setId_paciente(1);
        testHistorial.setDiagnostico("Infección respiratoria");
        testHistorial.setTratamiento("Antibiótico prescrito");
        testHistorial.setFecha_registro(LocalDate.now());
        testHistorial.setObservaciones("Paciente responde bien al tratamiento");

        historialRequest = new HistorialClinicoRequest();
        historialRequest.setId_cita(1);
        historialRequest.setId_paciente(1);
        historialRequest.setDiagnostico("Infección respiratoria");
        historialRequest.setTratamiento("Antibiótico prescrito");
        historialRequest.setObservaciones("Paciente responde bien al tratamiento");
    }

    @Test
    @DisplayName("Debe obtener todos los registros del historial clínico")
    void testGetAll() {
        // Arrange
        when(historialClinicoRepository.findAll()).thenReturn(List.of(testHistorial));

        // Act
        List<HistorialClinicoDto> result = historialClinicoService.getAll();

        // Assert
        assertNotNull(result);
        verify(historialClinicoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener un registro de historial clínico por ID")
    void testGetById() {
        // Arrange
        when(historialClinicoRepository.findById(1)).thenReturn(Optional.of(testHistorial));

        // Act
        HistorialClinicoDto result = historialClinicoService.getById(1);

        // Assert
        assertNotNull(result);
        assertEquals("Infección respiratoria", result.getDiagnostico());
        verify(historialClinicoRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe retornar null al obtener registro inexistente")
    void testGetByIdNotFound() {
        // Arrange
        when(historialClinicoRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        HistorialClinicoDto result = historialClinicoService.getById(99);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe guardar un nuevo registro de historial clínico")
    void testSave() {
        // Arrange
        when(historialClinicoRepository.save(any(HistorialClinico.class))).thenReturn(testHistorial);

        // Act
        HistorialClinicoDto result = historialClinicoService.save(historialRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Infección respiratoria", result.getDiagnostico());
        verify(historialClinicoRepository, times(1)).save(any(HistorialClinico.class));
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay registros")
    void testGetAllEmpty() {
        // Arrange
        when(historialClinicoRepository.findAll()).thenReturn(List.of());

        // Act
        List<HistorialClinicoDto> result = historialClinicoService.getAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe actualizar un registro de historial clínico existente")
    void testUpdate() {
        // Arrange
        when(historialClinicoRepository.findById(1)).thenReturn(Optional.of(testHistorial));
        when(historialClinicoRepository.save(any(HistorialClinico.class))).thenReturn(testHistorial);

        HistorialClinicoRequest updateRequest = new HistorialClinicoRequest();
        updateRequest.setId_cita(1);
        updateRequest.setId_paciente(1);
        updateRequest.setDiagnostico("Infección mejorada");
        updateRequest.setTratamiento("Descanso");
        updateRequest.setObservaciones("Sin cambios");
        updateRequest.setFecha_registro(LocalDate.now());

        // Act
        HistorialClinicoDto result = historialClinicoService.update(1, updateRequest);

        // Assert
        assertNotNull(result);
        verify(historialClinicoRepository, times(1)).save(any(HistorialClinico.class));
    }

    @Test
    @DisplayName("Debe registrar diagnóstico correctamente")
    void testSaveWithDiagnostic() {
        // Arrange
        HistorialClinicoRequest diagnosticRequest = new HistorialClinicoRequest();
        diagnosticRequest.setId_cita(1);
        diagnosticRequest.setId_paciente(1);
        diagnosticRequest.setDiagnostico("Varicela");
        diagnosticRequest.setTratamiento("Reposo y antihistamínicos");
        diagnosticRequest.setObservaciones("Vigilar complicaciones");

        when(historialClinicoRepository.save(any(HistorialClinico.class))).thenReturn(testHistorial);

        // Act
        HistorialClinicoDto result = historialClinicoService.save(diagnosticRequest);

        // Assert
        assertNotNull(result);
        verify(historialClinicoRepository, times(1)).save(any(HistorialClinico.class));
    }
}
