package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.TarjetaGuardadaRequest;
import com.medikids.medikids.process.domain.TarjetaGuardada;
import com.medikids.medikids.process.dto.TarjetaGuardadaDto;
import com.medikids.medikids.process.repository.TarjetaGuardadaRepository;
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
@DisplayName("TarjetaGuardadaService - Tests de gestión de tarjetas guardadas")
class TarjetaGuardadaServiceTest {

    @Autowired
    private TarjetaGuardadaService tarjetaGuardadaService;

    @MockitoBean
    private TarjetaGuardadaRepository tarjetaGuardadaRepository;

    private TarjetaGuardada testTarjeta;
    private TarjetaGuardadaRequest tarjetaRequest;

    @BeforeEach
    void setUp() {
        testTarjeta = new TarjetaGuardada();
        testTarjeta.setId_tarjeta(1);
        testTarjeta.setId_usuario(1);
        testTarjeta.setAlias("Tarjeta de Juan");
        testTarjeta.setUltimos_digitos("1234");
        testTarjeta.setMarca("VISA");
        testTarjeta.setNombre_titular("Juan Perez");
        testTarjeta.setMes_vencimiento(12);
        testTarjeta.setAnio_vencimiento(2025);
        testTarjeta.setActivo(true);
        testTarjeta.setFecha_creacion(LocalDateTime.now());

        tarjetaRequest = new TarjetaGuardadaRequest();
        tarjetaRequest.setAlias("Tarjeta de Juan");
        tarjetaRequest.setUltimos_digitos("1234");
        tarjetaRequest.setMarca("VISA");
        tarjetaRequest.setNombre_titular("Juan Perez");
        tarjetaRequest.setMes_vencimiento(12);
        tarjetaRequest.setAnio_vencimiento(2025);
    }

    @Test
    @DisplayName("Debe listar tarjetas activas de un usuario")
    void testListarPorUsuario() {
        // Arrange
        when(tarjetaGuardadaRepository.findByIdUsuarioAndActivoTrue(1))
                .thenReturn(List.of(testTarjeta));

        // Act
        List<TarjetaGuardadaDto> result = tarjetaGuardadaService.listarPorUsuario(1);

        // Assert
        assertNotNull(result);
        assertTrue(result.size() > 0);
        verify(tarjetaGuardadaRepository, times(1)).findByIdUsuarioAndActivoTrue(1);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando usuario no tiene tarjetas")
    void testListarPorUsuarioEmpty() {
        // Arrange
        when(tarjetaGuardadaRepository.findByIdUsuarioAndActivoTrue(99))
                .thenReturn(List.of());

        // Act
        List<TarjetaGuardadaDto> result = tarjetaGuardadaService.listarPorUsuario(99);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe guardar una nueva tarjeta")
    void testGuardar() {
        // Arrange
        when(tarjetaGuardadaRepository.save(any(TarjetaGuardada.class)))
                .thenReturn(testTarjeta);

        // Act
        TarjetaGuardadaDto result = tarjetaGuardadaService.guardar(tarjetaRequest, 1);

        // Assert
        assertNotNull(result);
        verify(tarjetaGuardadaRepository, times(1)).save(any(TarjetaGuardada.class));
    }

    @Test
    @DisplayName("Debe eliminar (desactivar) una tarjeta guardada")
    void testEliminar() {
        // Arrange
        when(tarjetaGuardadaRepository.findById(1))
                .thenReturn(Optional.of(testTarjeta));
        when(tarjetaGuardadaRepository.save(any(TarjetaGuardada.class)))
                .thenReturn(testTarjeta);

        // Act
        tarjetaGuardadaService.eliminar(1, 1);

        // Assert
        verify(tarjetaGuardadaRepository, times(1)).save(any(TarjetaGuardada.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar tarjeta inexistente")
    void testEliminarNotFound() {
        // Arrange
        when(tarjetaGuardadaRepository.findById(99))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> tarjetaGuardadaService.eliminar(99, 1));
    }

    @Test
    @DisplayName("Debe lanzar excepción si usuario intenta eliminar tarjeta ajena")
    void testEliminarUnauthorized() {
        // Arrange
        TarjetaGuardada otraTarjeta = new TarjetaGuardada();
        otraTarjeta.setId_tarjeta(1);
        otraTarjeta.setId_usuario(2); // Usuario diferente

        when(tarjetaGuardadaRepository.findById(1))
                .thenReturn(Optional.of(otraTarjeta));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> tarjetaGuardadaService.eliminar(1, 1));
    }

    @Test
    @DisplayName("Debe obtener múltiples tarjetas de un usuario")
    void testListarPorUsuarioMultiple() {
        // Arrange
        TarjetaGuardada tarjeta2 = new TarjetaGuardada();
        tarjeta2.setId_tarjeta(2);
        tarjeta2.setId_usuario(1);
        tarjeta2.setAlias("Tarjeta de Juan 2");
        tarjeta2.setUltimos_digitos("5678");
        tarjeta2.setMarca("VISA");
        tarjeta2.setNombre_titular("Juan Perez");
        tarjeta2.setMes_vencimiento(6);
        tarjeta2.setAnio_vencimiento(2026);
        tarjeta2.setActivo(true);

        when(tarjetaGuardadaRepository.findByIdUsuarioAndActivoTrue(1))
                .thenReturn(List.of(testTarjeta, tarjeta2));

        // Act
        List<TarjetaGuardadaDto> result = tarjetaGuardadaService.listarPorUsuario(1);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Debe enmascarar número de tarjeta correctamente")
    void testTarjetaMascarada() {
        // Arrange
        when(tarjetaGuardadaRepository.findByIdUsuarioAndActivoTrue(1))
                .thenReturn(List.of(testTarjeta));

        // Act
        List<TarjetaGuardadaDto> result = tarjetaGuardadaService.listarPorUsuario(1);

        // Assert
        assertNotNull(result);
        assertEquals("1234", result.get(0).getUltimos_digitos());
        assertEquals("Tarjeta de Juan", result.get(0).getAlias());
    }

    @Test
    @DisplayName("Debe solo listar tarjetas activas, no desactivadas")
    void testListarSoloActivas() {
        // Arrange
        TarjetaGuardada tarjetaInactiva = new TarjetaGuardada();
        tarjetaInactiva.setId_tarjeta(2);
        tarjetaInactiva.setId_usuario(1);
        tarjetaInactiva.setActivo(false);

        when(tarjetaGuardadaRepository.findByIdUsuarioAndActivoTrue(1))
                .thenReturn(List.of(testTarjeta));

        // Act
        List<TarjetaGuardadaDto> result = tarjetaGuardadaService.listarPorUsuario(1);

        // Assert
        assertNotNull(result);
        verify(tarjetaGuardadaRepository, times(1)).findByIdUsuarioAndActivoTrue(1);
    }
}
