package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.CitaRequest;
import com.medikids.medikids.process.domain.Cita;
import com.medikids.medikids.process.domain.Horario;
import com.medikids.medikids.process.dto.CitaDto;
import com.medikids.medikids.process.repository.*;
import com.medikids.medikids.utils.config.SimpleCache;
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
@DisplayName("CitaService - Tests de gestión de citas")
class CitaServiceTest {

    @Autowired
    private CitaService citaService;

    @MockitoBean
    private CitaRepository citaRepository;

    @MockitoBean
    private MedicoRepository medicoRepository;

    @MockitoBean
    private PacienteRepository pacienteRepository;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private EspecialidadRepository especialidadRepository;

    @MockitoBean
    private ClienteRepository clienteRepository;

    @MockitoBean
    private EmailService emailService;

    @MockitoBean
    private PdfService pdfService;

    @MockitoBean
    private HorarioRepository horarioRepository;

    @MockitoBean
    private PagoRepository pagoRepository;

    @MockitoBean(name = "medicoEntityCache")
    private SimpleCache medicoEntityCache;

    @MockitoBean(name = "usuarioEntityCache")
    private SimpleCache usuarioEntityCache;

    private Cita testCita;
    private CitaRequest citaRequest;

    @BeforeEach
    void setUp() {
        testCita = new Cita();
        testCita.setId_cita(1);
        testCita.setId_paciente(1);
        testCita.setId_medico(1);
        testCita.setId_horario(1);
        testCita.setId_pago(0);
        testCita.setFecha_cita(LocalDate.now().plusDays(5));
        testCita.setEstado("pendiente");

        citaRequest = new CitaRequest();
        citaRequest.setId_paciente(1);
        citaRequest.setId_medico(1);
        citaRequest.setId_horario(1);
        citaRequest.setFecha_cita(LocalDate.now().plusDays(5).toString());
        citaRequest.setEstado("pendiente");
    }

    @Test
    @DisplayName("Debe obtener todas las citas")
    void testGetAll() {
        // Arrange
        when(citaRepository.findAll()).thenReturn(List.of(testCita));

        // Act
        List<CitaDto> result = citaService.getAll();

        // Assert
        assertNotNull(result);
        verify(citaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener una cita por ID")
    void testGetById() {
        // Arrange
        when(citaRepository.findById(1)).thenReturn(Optional.of(testCita));

        // Act
        CitaDto result = citaService.getById(1);

        // Assert
        assertNotNull(result);
        verify(citaRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe retornar null al obtener cita inexistente")
    void testGetByIdNotFound() {
        // Arrange
        when(citaRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        CitaDto result = citaService.getById(99);

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("Debe obtener citas de un paciente")
    void testGetByPaciente() {
        // Arrange
        when(citaRepository.findByIdPaciente(1)).thenReturn(List.of(testCita));

        // Act
        List<CitaDto> result = citaService.getByPaciente(1);

        // Assert
        assertNotNull(result);
        verify(citaRepository, times(1)).findByIdPaciente(1);
    }

    @Test
    @DisplayName("Debe obtener citas de un médico")
    void testGetByMedico() {
        // Arrange
        when(citaRepository.findByIdMedico(1)).thenReturn(List.of(testCita));

        // Act
        List<CitaDto> result = citaService.getByMedico(1);

        // Assert
        assertNotNull(result);
        verify(citaRepository, times(1)).findByIdMedico(1);
    }

    @Test
    @DisplayName("Debe guardar una nueva cita")
    void testSave() {
        // Arrange
        Horario testHorario = new Horario();
        testHorario.setId_horario(1);
        testHorario.setDisponible('1');
        when(horarioRepository.findById(1)).thenReturn(Optional.of(testHorario));
        when(horarioRepository.save(any(Horario.class))).thenReturn(testHorario);
        when(citaRepository.save(any(Cita.class))).thenReturn(testCita);

        // Act
        CitaDto result = citaService.save(citaRequest);

        // Assert
        assertNotNull(result);
        verify(citaRepository, times(1)).save(any(Cita.class));
        verify(horarioRepository, times(1)).findById(1);
        verify(horarioRepository, times(1)).save(any(Horario.class));
    }

    @Test
    @DisplayName("Debe actualizar una cita existente")
    void testUpdate() {
        // Arrange
        when(citaRepository.findById(1)).thenReturn(Optional.of(testCita));
        when(citaRepository.save(any(Cita.class))).thenReturn(testCita);

        // Act
        CitaDto result = citaService.update(1, citaRequest);

        // Assert
        assertNotNull(result);
        verify(citaRepository, times(1)).save(any(Cita.class));
    }
}
