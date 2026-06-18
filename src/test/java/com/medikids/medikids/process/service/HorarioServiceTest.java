package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.SemanaRequest;
import com.medikids.medikids.process.domain.Horario;
import com.medikids.medikids.process.dto.HorarioDto;
import com.medikids.medikids.process.repository.HorarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.sql.Time;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("HorarioService - Tests de gestión de horarios")
class HorarioServiceTest {

    @Autowired
    private HorarioService horarioService;

    @MockitoBean
    private HorarioRepository horarioRepository;

    private Horario testHorario;

    @BeforeEach
    void setUp() {
        testHorario = Horario.builder()
                .id_horario(1)
                .id_medico(1)
                .medico_id(1)
                .fecha(LocalDate.now().plusDays(1))
                .hora_inicio(Time.valueOf("09:00:00"))
                .hora_fin(Time.valueOf("10:00:00"))
                .disponible('1')
                .build();
    }

    @Test
    @DisplayName("Debe obtener horarios de un médico")
    void testGetByMedico() {
        // Arrange
        when(horarioRepository.findByMedico(1)).thenReturn(List.of(testHorario));

        // Act
        List<HorarioDto> result = horarioService.getByMedico(1);

        // Assert
        assertNotNull(result);
        verify(horarioRepository, times(1)).findByMedico(1);
    }

    @Test
    @DisplayName("Debe obtener horarios disponibles de un médico")
    void testGetDisponiblesByMedico() {
        // Arrange
        when(horarioRepository.findDisponiblesByMedico(1)).thenReturn(List.of(testHorario));

        // Act
        List<HorarioDto> result = horarioService.getDisponiblesByMedico(1);

        // Assert
        assertNotNull(result);
        assertTrue(result.size() > 0);
        verify(horarioRepository, times(1)).findDisponiblesByMedico(1);
    }

    @Test
    @DisplayName("Debe obtener horarios en un rango de fechas")
    void testGetHorariosBySemana() {
        // Arrange
        LocalDate inicio = LocalDate.now();
        LocalDate fin = LocalDate.now().plusDays(7);
        when(horarioRepository.findByMedicoAndFechaBetween(1, inicio, fin))
                .thenReturn(List.of(testHorario));

        // Act
        List<HorarioDto> result = horarioService.getHorariosBySemana(1, inicio, fin);

        // Assert
        assertNotNull(result);
        verify(horarioRepository, times(1)).findByMedicoAndFechaBetween(1, inicio, fin);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay horarios disponibles")
    void testGetDisponiblesByMedicoEmpty() {
        // Arrange
        when(horarioRepository.findDisponiblesByMedico(99)).thenReturn(List.of());

        // Act
        List<HorarioDto> result = horarioService.getDisponiblesByMedico(99);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe guardar horarios de una semana")
    void testSaveSemana() {
        // Arrange
        SemanaRequest request = new SemanaRequest();
        request.setId_medico(1);
        request.setInicio(LocalDate.now());
        request.setFin(LocalDate.now().plusDays(7));

        SemanaRequest.HorarioBloque bloque = new SemanaRequest.HorarioBloque();
        bloque.setFecha(LocalDate.now().plusDays(1));
        bloque.setHora_inicio(Time.valueOf("09:00:00"));
        bloque.setHora_fin(Time.valueOf("10:00:00"));

        request.setBloques(List.of(bloque));

        when(horarioRepository.saveAll(any())).thenReturn(List.of(testHorario));

        // Act
        horarioService.saveSemana(request);

        // Assert
        verify(horarioRepository, times(1)).deleteDisponiblesByMedicoAndFechaBetween(
                1, LocalDate.now(), LocalDate.now().plusDays(7)
        );
        verify(horarioRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("Debe limpiar horarios previos al guardar una nueva semana")
    void testSaveSemanaCleansPrevious() {
        // Arrange
        SemanaRequest request = new SemanaRequest();
        request.setId_medico(1);
        LocalDate inicio = LocalDate.now();
        LocalDate fin = LocalDate.now().plusDays(7);
        request.setInicio(inicio);
        request.setFin(fin);

        SemanaRequest.HorarioBloque bloque = new SemanaRequest.HorarioBloque();
        bloque.setFecha(LocalDate.now().plusDays(1));
        bloque.setHora_inicio(Time.valueOf("09:00:00"));
        bloque.setHora_fin(Time.valueOf("10:00:00"));

        request.setBloques(List.of(bloque));

        when(horarioRepository.saveAll(any())).thenReturn(List.of(testHorario));

        // Act
        horarioService.saveSemana(request);

        // Assert
        verify(horarioRepository, times(1)).deleteDisponiblesByMedicoAndFechaBetween(
                1, inicio, fin
        );
    }

    @Test
    @DisplayName("Debe guardar múltiples bloques horarios en una semana")
    void testSaveSemanaMultipleBloques() {
        // Arrange
        SemanaRequest request = new SemanaRequest();
        request.setId_medico(1);
        request.setInicio(LocalDate.now());
        request.setFin(LocalDate.now().plusDays(7));

        List<SemanaRequest.HorarioBloque> bloques = new java.util.ArrayList<>();
        for (int i = 0; i < 5; i++) {
            SemanaRequest.HorarioBloque bloque = new SemanaRequest.HorarioBloque();
            bloque.setFecha(LocalDate.now().plusDays(i + 1));
            bloque.setHora_inicio(Time.valueOf("09:00:00"));
            bloque.setHora_fin(Time.valueOf("17:00:00"));
            bloques.add(bloque);
        }

        request.setBloques(bloques);

        when(horarioRepository.saveAll(any())).thenReturn(List.of());

        // Act
        horarioService.saveSemana(request);

        // Assert
        verify(horarioRepository, times(1)).saveAll(argThat(list -> {
            int count = 0;
            for (Horario ignored : list) {
                count++;
            }
            return count == 5;
        }));
    }
}
