package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.MedicoRequest;
import com.medikids.medikids.process.domain.Medico;
import com.medikids.medikids.process.dto.MedicoDto;
import com.medikids.medikids.process.repository.EspecialidadRepository;
import com.medikids.medikids.process.repository.MedicoRepository;
import com.medikids.medikids.process.repository.UsuarioRepository;
import com.medikids.medikids.utils.config.SimpleCache;
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
@DisplayName("MedicoService - Tests de gestión de médicos")
class MedicoServiceTest {

    @Autowired
    private MedicoService medicoService;

    @MockitoBean
    private MedicoRepository medicoRepository;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private EspecialidadRepository especialidadRepository;

    @MockitoBean(name = "medicoListCache")
    private SimpleCache<String, List<MedicoDto>> medicoListCache;

    private Medico testMedico;
    private MedicoRequest medicoRequest;

    @BeforeEach
    void setUp() {
        testMedico = new Medico();
        testMedico.setId_medico(1);
        testMedico.setId_usuario(1);
        testMedico.setId_especialidad(1);
        testMedico.setNro_colegiatura("12345");
        testMedico.setUrl_foto("http://example.com/foto.jpg");
        testMedico.setGenero(Medico.Genero.masculino);
        testMedico.setEstado(Medico.EstadoMedico.activo);
        testMedico.setActivo('1');

        medicoRequest = new MedicoRequest();
        medicoRequest.setId_usuario(1);
        medicoRequest.setId_especialidad(1);
        medicoRequest.setNro_colegiatura("12345");
        medicoRequest.setUrl_foto("http://example.com/foto.jpg");
        medicoRequest.setGenero("masculino");
        medicoRequest.setEstado(Medico.EstadoMedico.activo);
    }

    @Test
    @DisplayName("Debe obtener todos los médicos")
    void testGetAll() {
        // Arrange
        when(medicoListCache.get("all")).thenReturn(null);
        when(medicoRepository.findAll()).thenReturn(List.of(testMedico));

        // Act
        List<MedicoDto> result = medicoService.getAll();

        // Assert
        assertNotNull(result);
        verify(medicoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe obtener médico desde cache si existe")
    void testGetAllFromCache() {
        // Arrange
        List<MedicoDto> cachedList = List.of();
        when(medicoListCache.get("all")).thenReturn(cachedList);

        // Act
        List<MedicoDto> result = medicoService.getAll();

        // Assert
        assertNotNull(result);
        verify(medicoRepository, never()).findAll();
    }

    @Test
    @DisplayName("Debe obtener un médico por ID")
    void testGetById() {
        // Arrange
        when(medicoRepository.findById(1)).thenReturn(Optional.of(testMedico));

        // Act
        MedicoDto result = medicoService.getById(1);

        // Assert
        assertNotNull(result);
        verify(medicoRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Debe retornar null al obtener médico inexistente")
    void testGetByIdNotFound() {
        // Arrange
        when(medicoRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        MedicoDto result = medicoService.getById(99);

        // Assert
        assertNull(result);
        verify(medicoRepository, times(1)).findById(99);
    }

    @Test
    @DisplayName("Debe obtener médico por ID de usuario")
    void testGetByIdUsuario() {
        // Arrange
        when(medicoRepository.findByIdUsuario(1)).thenReturn(Optional.of(testMedico));

        // Act
        MedicoDto result = medicoService.getByIdUsuario(1);

        // Assert
        assertNotNull(result);
        verify(medicoRepository, times(1)).findByIdUsuario(1);
    }

    @Test
    @DisplayName("Debe cambiar el estado de un médico (toggle status)")
    void testToggleStatus() {
        // Arrange
        when(medicoRepository.findById(1)).thenReturn(Optional.of(testMedico));
        when(medicoRepository.save(any(Medico.class))).thenReturn(testMedico);
        doNothing().when(medicoListCache).invalidate("all");

        // Act
        MedicoDto result = medicoService.toggleStatus(1);

        // Assert
        assertNotNull(result);
        verify(medicoRepository, times(1)).save(any(Medico.class));
        verify(medicoListCache, times(1)).invalidate("all");
    }

    @Test
    @DisplayName("Debe retornar null al cambiar estado de médico inexistente")
    void testToggleStatusNotFound() {
        // Arrange
        when(medicoRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        MedicoDto result = medicoService.toggleStatus(99);

        // Assert
        assertNull(result);
        verify(medicoRepository, never()).save(any(Medico.class));
    }

    @Test
    @DisplayName("Debe eliminar un médico")
    void testDeleteSuccess() {
        // Arrange
        when(medicoRepository.existsById(1)).thenReturn(true);
        doNothing().when(medicoListCache).invalidate("all");

        // Act
        boolean result = medicoService.delete(1);

        // Assert
        assertTrue(result);
        verify(medicoRepository, times(1)).deleteById(1);
        verify(medicoListCache, times(1)).invalidate("all");
    }

    @Test
    @DisplayName("Debe retornar false al eliminar médico inexistente")
    void testDeleteNotFound() {
        // Arrange
        when(medicoRepository.existsById(99)).thenReturn(false);

        // Act
        boolean result = medicoService.delete(99);

        // Assert
        assertFalse(result);
        verify(medicoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Debe guardar un nuevo médico")
    void testSave() {
        // Arrange
        when(medicoRepository.save(any(Medico.class))).thenReturn(testMedico);
        doNothing().when(medicoListCache).invalidate("all");

        // Act
        MedicoDto result = medicoService.save(medicoRequest);

        // Assert
        assertNotNull(result);
        verify(medicoRepository, times(1)).save(any(Medico.class));
        verify(medicoListCache, times(1)).invalidate("all");
    }

    @Test
    @DisplayName("Debe actualizar un médico existente")
    void testUpdate() {
        // Arrange
        when(medicoRepository.findById(1)).thenReturn(Optional.of(testMedico));
        when(medicoRepository.save(any(Medico.class))).thenReturn(testMedico);
        doNothing().when(medicoListCache).invalidate("all");

        // Act
        MedicoDto result = medicoService.update(1, medicoRequest);

        // Assert
        assertNotNull(result);
        verify(medicoRepository, times(1)).save(any(Medico.class));
        verify(medicoListCache, times(1)).invalidate("all");
    }

    @Test
    @DisplayName("Debe retornar null al actualizar médico inexistente")
    void testUpdateNotFound() {
        // Arrange
        when(medicoRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        MedicoDto result = medicoService.update(99, medicoRequest);

        // Assert
        assertNull(result);
        verify(medicoRepository, never()).save(any(Medico.class));
    }
}
