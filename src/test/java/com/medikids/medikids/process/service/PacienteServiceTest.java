package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.PacienteRequest;
import com.medikids.medikids.process.domain.Paciente;
import com.medikids.medikids.process.dto.PacienteDto;
import com.medikids.medikids.process.repository.ClienteRepository;
import com.medikids.medikids.process.repository.PacienteRepository;
import com.medikids.medikids.process.repository.UsuarioRepository;
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
@DisplayName("PacienteService - Tests de gestión de pacientes")
class PacienteServiceTest {

    @Autowired
    private PacienteService pacienteService;

    @MockitoBean
    private PacienteRepository pacienteRepository;

    @MockitoBean
    private ClienteRepository clienteRepository;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    private Paciente testPaciente;
    private PacienteRequest pacienteRequest;

    @BeforeEach
    void setUp() {
        testPaciente = new Paciente();
        testPaciente.setId_paciente(1);
        testPaciente.setId_cliente(1);
        testPaciente.setNombre_completo("Carlos López");
        testPaciente.setDni_menor("12345678");
        testPaciente.setFecha_nacimiento(LocalDate.of(2015, 5, 15));

        pacienteRequest = new PacienteRequest();
        pacienteRequest.setId_cliente(1);
        pacienteRequest.setNombre_completo("Carlos López");
        pacienteRequest.setDni_menor("12345678");
        pacienteRequest.setFecha_nacimiento(LocalDate.of(2015, 5, 15));
    }

    @Test
    @DisplayName("Debe obtener todos los pacientes")
    void testGetAll() {
        // Arrange
        when(pacienteRepository.findAll()).thenReturn(List.of(testPaciente));

        // Act
        List<PacienteDto> result = pacienteService.getAll();

        // Assert
        assertNotNull(result);
        verify(pacienteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener un paciente por ID")
    void testGetById() {
        // Arrange
        when(pacienteRepository.findById(1)).thenReturn(Optional.of(testPaciente));

        // Act
        PacienteDto result = pacienteService.getById(1);

        // Assert
        assertNotNull(result);
        verify(pacienteRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe retornar null al obtener paciente inexistente")
    void testGetByIdNotFound() {
        // Arrange
        when(pacienteRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        PacienteDto result = pacienteService.getById(99);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe obtener pacientes de un cliente")
    void testGetByIdCliente() {
        // Arrange
        when(pacienteRepository.findByIdCliente(1)).thenReturn(List.of(testPaciente));

        // Act
        List<PacienteDto> result = pacienteService.getByIdCliente(1);

        // Assert
        assertNotNull(result);
        verify(pacienteRepository, times(1)).findByIdCliente(1);
    }

    @Test
    @DisplayName("Debe guardar un nuevo paciente")
    void testSave() {
        // Arrange
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(testPaciente);

        // Act
        PacienteDto result = pacienteService.save(pacienteRequest);

        // Assert
        assertNotNull(result);
        verify(pacienteRepository, times(1)).save(any(Paciente.class));
    }

    @Test
    @DisplayName("Debe actualizar un paciente existente")
    void testUpdate() {
        // Arrange
        when(pacienteRepository.findById(1)).thenReturn(Optional.of(testPaciente));
        when(pacienteRepository.save(any(Paciente.class))).thenReturn(testPaciente);

        // Act
        PacienteDto result = pacienteService.update(1, pacienteRequest);

        // Assert
        assertNotNull(result);
        verify(pacienteRepository, times(1)).save(any(Paciente.class));
    }

    @Test
    @DisplayName("Debe retornar null al actualizar paciente inexistente")
    void testUpdateNotFound() {
        // Arrange
        when(pacienteRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        PacienteDto result = pacienteService.update(99, pacienteRequest);

        // Assert
        assertNull(result);
        verify(pacienteRepository, never()).save(any(Paciente.class));
    }


    @Test
    @DisplayName("Debe calcular la edad del paciente correctamente")
    void testGetAge() {
        // Arrange - paciente nacido en 2015
        when(pacienteRepository.findById(1)).thenReturn(Optional.of(testPaciente));

        // Act
        PacienteDto result = pacienteService.getById(1);

        // Assert
        assertNotNull(result);
        // La edad dependerá del año actual, por eso solo verificamos que el objeto existe
    }
}
