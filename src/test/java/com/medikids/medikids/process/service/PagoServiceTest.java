package com.medikids.medikids.process.service;

import com.medikids.medikids.expose.model.request.PagoRequest;
import com.medikids.medikids.process.domain.Pago;
import com.medikids.medikids.process.dto.PagoDto;
import com.medikids.medikids.process.repository.PagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("PagoService - Tests de gestión de pagos")
class PagoServiceTest {

    @Autowired
    private PagoService pagoService;

    @MockitoBean
    private PagoRepository pagoRepository;

    private Pago testPago;
    private PagoRequest pagoRequest;

    @BeforeEach
    void setUp() {
        testPago = new Pago();
        testPago.setId_pago(1);
        testPago.setId_cita(1);
        testPago.setMonto(50.00);
        testPago.setEstado_transaccion("completado");
        testPago.setFecha_pago(LocalDateTime.now());
        testPago.setMetodo_pago("tarjeta");

        pagoRequest = new PagoRequest();
        pagoRequest.setMonto(50.00);
        pagoRequest.setMetodo_pago("tarjeta");
    }

    @Test
    @DisplayName("Debe obtener todos los pagos")
    void testListarPagos() {
        // Arrange
        when(pagoRepository.findAll()).thenReturn(List.of(testPago));

        // Act
        List<PagoDto> result = pagoService.listarPagos();

        // Assert
        assertNotNull(result);
        assertTrue(result.size() > 0);
        verify(pagoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe guardar un nuevo pago")
    void testGuardarPago() {
        // Arrange
        when(pagoRepository.save(any(Pago.class))).thenReturn(testPago);

        // Act
        PagoDto result = pagoService.guardarPago(pagoRequest);

        // Assert
        assertNotNull(result);
        assertEquals(50.00, result.getMonto());
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    @Test
    @DisplayName("Debe listar pagos de un cliente específico")
    void testListarPagosPorCliente() {
        // Arrange
        when(pagoRepository.findByCliente(1)).thenReturn(List.of(testPago));

        // Act
        List<PagoDto> result = pagoService.listarPagosPorCliente(1);

        // Assert
        assertNotNull(result);
        assertTrue(result.size() > 0);
        verify(pagoRepository, times(1)).findByCliente(1);
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no hay pagos")
    void testListarPagosEmpty() {
        // Arrange
        when(pagoRepository.findAll()).thenReturn(List.of());

        // Act
        List<PagoDto> result = pagoService.listarPagos();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe retornar lista vacía de pagos para cliente sin transacciones")
    void testListarPagosPorClienteEmpty() {
        // Arrange
        when(pagoRepository.findByCliente(99)).thenReturn(List.of());

        // Act
        List<PagoDto> result = pagoService.listarPagosPorCliente(99);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Debe obtener múltiples pagos de un cliente")
    void testListarPagosPorClienteMultiple() {
        // Arrange
        Pago pago2 = new Pago();
        pago2.setId_pago(2);
        pago2.setId_cita(2);
        pago2.setMonto(75.00);
        pago2.setEstado_transaccion("completado");

        when(pagoRepository.findByCliente(1)).thenReturn(List.of(testPago, pago2));

        // Act
        List<PagoDto> result = pagoService.listarPagosPorCliente(1);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Debe guardar pago con validación de monto")
    void testGuardarPagoValido() {
        // Arrange
        PagoRequest validPago = new PagoRequest();
        validPago.setMonto(100.50);
        validPago.setMetodo_pago("efectivo");

        when(pagoRepository.save(any(Pago.class))).thenReturn(testPago);

        // Act
        PagoDto result = pagoService.guardarPago(validPago);

        // Assert
        assertNotNull(result);
        assertTrue(result.getMonto() > 0);
    }
}
